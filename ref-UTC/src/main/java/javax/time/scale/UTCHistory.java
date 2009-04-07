package javax.time.scale;

import javax.time.Instant;
import java.io.*;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.logging.Logger;
import java.util.logging.Level;

/** Record of relationship between UTC and TAI.
 * @author  Mark Thornton
 */
public class UTCHistory implements Serializable {
    /** Time when leap seconds started.
     * 1972-01-01
     */
    public static final Instant START_LEAP_SECONDS = new UTC.Instant(2*365*86400L, 0);

    private static UTCHistory current;
    private final int version;
    private final Instant maximumKnownInstant;
    private final List<UTCHistoryEntry> entries;
    private transient long[] simpleStartEpochSeconds;
    private transient long[] trueStartEpochSeconds;
    private transient TAI.Instant[] taiStart;

    static {
        /*
        Should look for definition in ${JAVA-HOME}/lib so that it can be updated independently
         of the JDK (and without replacing a large jar file).
         For now we will load it from a resource.
         */
        InputStream in = UTCHistory.class.getResourceAsStream("UTCHistory.data");
        UTCHistory history = null;
        if (in != null) {
            try {
                history = load(in);
                in.close();
            }
            catch (IOException ex) {
                Logger.getAnonymousLogger().log(Level.WARNING, "Failed to load serialized UTCHistory", ex);
            }
            catch (ClassNotFoundException ex) {
                Logger.getAnonymousLogger().log(Level.WARNING, "Failed to load serialized UTCHistory", ex);
            }
        }
        if (history == null) {
            in = UTCHistory.class.getResourceAsStream("UTCHistory.txt");
            if (in != null) {
                try {
                    history = load(new InputStreamReader(in, "UTF-8"));
                    in.close();
                }
                catch (IOException ex)
                {
                    Logger.getAnonymousLogger().log(Level.WARNING, "Failed to load UTCHistory", ex);
                }
            }
        }
        current = history;
    }

    /** Read history from a text description.
     * @param text
     * @return
     */
    public static UTCHistory load(Reader text) throws IOException {
        BufferedReader reader = new BufferedReader(text);
        int version = -1;
        Instant maximum=null;
        ArrayList<UTCHistoryEntry> entries = new ArrayList<UTCHistoryEntry>();
        Pattern commaSeparated = Pattern.compile("\\s*,\\s*");
        while (true) {
            String line = reader.readLine();
            if (line == null)
                break;
            line = line.trim();
            if (line.length() > 0 && line.charAt(0) != '#') {
                String[] fields = commaSeparated.split(line);
                if (fields[0].equals("version"))
                    version = Integer.parseInt(fields[1]);
                else if (fields[0].equals("maximum"))
                    maximum = UTCHistoryEntry.parseInstant(fields[1]);
                else
                    entries.add(UTCHistoryEntry.parseEntry(fields));
            }
        }
        entries.trimToSize();
        return new UTCHistory(version, maximum, entries);
    }

    /** Load a serialized history
     * @param in
     * @return
     */
    public static UTCHistory load(InputStream in) throws IOException, ClassNotFoundException {
        ObjectInputStream stream = new ObjectInputStream(new BufferedInputStream(in));
        try {
            return (UTCHistory)stream.readObject();
        }
        finally {
            stream.close();
        }
    }

    public static UTCHistory current() {
        return current;
    }

    private UTCHistory(int version, Instant maximum, List<UTCHistoryEntry> entries) {
        this.version = version;
        this.maximumKnownInstant = maximum;
        this.entries = Collections.unmodifiableList(entries);
        computeTransientFields();
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        computeTransientFields();
    }

    /** Compute derived data.
     * This information is not part of the serialised state, but
     * is recomputed once the entries have been loaded.
     */
    private void computeTransientFields() {
        UTCHistoryEntry.setNextPrevious(entries);
        int n = entries.size()-1;
        simpleStartEpochSeconds = new long[n];
        trueStartEpochSeconds = new long[n];
        taiStart = new TAI.Instant[n];
        for (int i=0; i<n; i++) {
            UTCHistoryEntry e = entries.get(i+1);
            simpleStartEpochSeconds[i] = e.getStartUTC().getEpochSeconds();
            assert e.getStartUTC().getNanoOfSecond() == 0;
            trueStartEpochSeconds[i] = simpleStartEpochSeconds[i] + e.getLeapSecondCount();
            taiStart[i] = e.getStartTAI();
        }
    }

    /** Write serialized history.
     *
     * @param out
     * @throws IOException
     */
    public void store(OutputStream out) throws IOException {
        ObjectOutputStream stream = new ObjectOutputStream(new BufferedOutputStream(out));
        stream.writeObject(this);
        stream.close();
    }

    /** Version of history.
     * The history may be updated to correct errors or add new leap seconds.
     * @return Simple integer version.
     */
    public int getVersion() {
        return version;
    }

    /** latest instant for which leap seconds are known.
     * Leap seconds aren't known more than a few months in advance.
     * @return UTC instant.
     */
    public Instant getMaximumKnownInstant() {
        return maximumKnownInstant;
    }

    /** Immutable list of history entries */
    public List<UTCHistoryEntry> getEntries() {
        return entries;
    }

    private UTCHistoryEntry getSearchedEntry(int index) {
        return entries.get(index < 0 ? -index-1 : index+1);
    }

    /** Find an entry given simple epoch seconds.
     * @param simpleEpochSeconds seconds from 1970-01-01, not counting leap seconds.
     * @return UTCHistoryEntry containing specified time
     */
    public UTCHistoryEntry findEntrySimple(long simpleEpochSeconds) {
       return getSearchedEntry(Arrays.binarySearch(simpleStartEpochSeconds, simpleEpochSeconds));
    }

    /** Find an entry given true epoch seconds.
     *
     * @param trueEpochSeconds seconds from 1970-01-01 including leap seconds
     * @return UTCHistoryEntry containing specified time
     */
    public UTCHistoryEntry findEntryTrue(long trueEpochSeconds) {
        return getSearchedEntry(Arrays.binarySearch(trueStartEpochSeconds, trueEpochSeconds));
    }

    /** Find an entry given the instant.
     * @param t Instant for which entry is required. If not in TAI it will be converted.
     * @return  Entry containing the instant.
     */
    public UTCHistoryEntry findEntry(Instant t) {
        if (t instanceof UTC.Instant)
            return findEntrySimple(t.getEpochSeconds());
        if (!(t instanceof TAI.Instant))
            t = TAI.SCALE.instant(t);
        return findEntry((TAI.Instant)t);
    }

    /** Find an entry given a TAI instant.
     * @param t Instant for which entry is required.
     * @return  Entry containing the instant.
     */
    public UTCHistoryEntry findEntry(TAI.Instant t) {
        return getSearchedEntry(Arrays.binarySearch(taiStart, t));
    }
}
