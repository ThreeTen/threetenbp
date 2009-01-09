package javax.time.scale;

import javax.time.TimeScale;
import java.io.Serializable;

/** International Atomic Time.
 *  * @author Mark Thornton
 */
public class TAI extends TimeScale implements Serializable {
    public static final TAI SCALE = new TAI();

    private TAI() {}

    private Object readResolve() {
        return SCALE;
    }

    public String getName() {
        return "TAI";
    }

    @Override
    protected AbstractInstant fromTAI(AbstractInstant tsiTAI) {
        return tsiTAI;
    }

    protected AbstractInstant toTAI(AbstractInstant t) {
        return t;
    }

}
