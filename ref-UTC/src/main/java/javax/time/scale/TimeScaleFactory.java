package javax.time.scale;

import javax.time.TimeScale;
import java.util.Collection;

/** Service interface for obtaining time scales.
 */
public interface TimeScaleFactory {
    TimeScale getTimeScale(String name);
    /** Get names of supported time scales */
    Collection<String> getNames();
}
