package javax.time;

/** Redirect calls to Duration class being tested.
 */
public abstract class AbstractDurationFactory<T extends AbstractDuration> {
    public abstract T seconds(long seconds);
    public abstract T millis(long millis);
    public abstract T nanos(long nanos);
    
    public T parse(String text) {
        throw new UnsupportedOperationException();
    }
}
