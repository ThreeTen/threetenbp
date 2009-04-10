package javax.time.scale;

/** Report UTCHistory content.
 * @author Mark Thornton
 */
public class ShowUTCHistory {
    public static void main(String[] args) {
        UTCHistory current = UTCHistory.current();
        System.out.println("Start leap seconds: "+UTCHistory.UTC_START_LEAP_SECONDS);
        System.out.println("version="+current.getVersion());
        System.out.println("maximumKnown="+current.getMaximumKnownInstant());
        for (UTCHistoryEntry e: current.getEntries()) {
            System.out.println(e);
        }
    }
}
