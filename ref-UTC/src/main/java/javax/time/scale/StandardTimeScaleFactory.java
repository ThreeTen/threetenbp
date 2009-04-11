package javax.time.scale;

import javax.time.TimeScale;
import javax.time.CalendricalException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.lang.reflect.Field;

/** Obtain standard TimeScale's.
 */
public class StandardTimeScaleFactory implements TimeScaleFactory {
    private static final Collection<String> names = Collections.unmodifiableList(Arrays.asList("UTC", "TrueUTC", "TAI", "GPS"));
    private static final Map<String, TimeScale> loadedTimeScales = new ConcurrentHashMap<String, TimeScale>();

    public TimeScale getTimeScale(String name) {
        if (!names.contains(name))
            return null;
        TimeScale ts = loadedTimeScales.get(name);
        if (ts == null) {
            // Use reflection to avoid loading TimeScale's that haven't been requested
            String classname = "javax.time.scale.".concat(name);
            try {
                Class type = Class.forName(classname);
                Field f = type.getField("SCALE");
                ts = (TimeScale)f.get(null);
                loadedTimeScales.put(name, ts);
            }
            catch (ClassNotFoundException e) {
                throw new CalendricalException("Failed to create time scale: "+name, e);
            }
            catch (NoSuchFieldException e) {
                throw new CalendricalException("Failed to create time scale: "+name, e);
            }
            catch (IllegalAccessException e) {
                throw new CalendricalException("Failed to create time scale: "+name, e);
            }
        }
        return ts;
    }

    public Collection<String> getNames() {
        return names;
    }
}
