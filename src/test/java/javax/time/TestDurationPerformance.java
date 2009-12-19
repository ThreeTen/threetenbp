package javax.time;

import java.util.ArrayList;

/** Generic Duration test framework.
 * A test is run by creating an instance of this class with the desired duration factory
 * and then executing the run() method. Only one factory type should be tested within a
 * single JVM instance. This allows the JIT to easily replace virtual AbstractDuration methods with static
 * calls and potentiually inline them.
 */
public class TestDurationPerformance<T extends AbstractDuration<T>> implements Runnable {
    private static final int N_CREATE = 10000000;
    private static final int N_ADD = 10000000;

    private final AbstractDurationFactory<T> factory;
    private Runnable[] tests;
    private boolean report;
    private int z;
    /** reference for comparisons.
     * This is used to try to prevent the JIT from optimising away much of the computation being tested.
     */
    private T zero;

    public static <T extends AbstractDuration<T>> TestDurationPerformance<T> instance(AbstractDurationFactory<T> factory) {
        return new TestDurationPerformance<T>(factory);
    }

    public TestDurationPerformance(AbstractDurationFactory<T> factory) {
        this.factory = factory;
        zero = factory.seconds(0);
        tests = new Runnable[] {new ObjectCreation(), new SecondsCreation(), new MillisCreation(), new NanosCreation(),
            new Parse(), new ToString(),
            new AddDuration(), new AddSeconds(), new AddMilliSeconds(), new AddNanoSeconds(),
        new Compare()};
    }

    public void run() {
        // warmup
        for (int i=0; i<5; i++) {
            runTrial();
        }
        report = true;
        for (int i=0; i<3; i++) {
            System.out.println("Trial "+i);
            runTrial();
        }
        System.out.println("z="+z);
    }

    private void runTrial() {
        for (Runnable test: tests) {
            try {
                long t = System.nanoTime();
                test.run();
                t = System.nanoTime()-t;
                if (report) {
                    String name = test.getClass().getName();
                    name = name.substring(name.indexOf('$')+1);
                    System.out.println((t*1e-9f)+"s\t"+name);
                }
            }
            catch (UnsupportedOperationException e) {
                // ignore
            }
        }
        if (report) {
            System.out.println();
        }
    }

    private class ObjectCreation implements Runnable {
        public void run() {
            int n = N_CREATE*10;
            for (int i=0; i<n; i++) {
                z += new Object().getClass().getName().length();
            }
        }
    }

    private class SecondsCreation implements Runnable {
        public void run() {
            for (int i=0; i<N_CREATE; i++) {
                z += zero.compareTo(factory.seconds(i));
            }
        }
    }

    private class MillisCreation implements Runnable {
        public void run() {
            for (int i=0; i<N_CREATE; i++) {
                z += zero.compareTo(factory.millis(i));
            }
        }
    }

    private class NanosCreation implements Runnable {
        public void run() {
            for (int i=0; i<N_CREATE; i++) {
                z += zero.compareTo(factory.nanos(i+1234567));
            }
        }
    }

    private class Parse implements Runnable {
        public void run() {
            int n = N_CREATE/2;
            for (int i=0; i<n; i++) {
                z += zero.compareTo(factory.parse("PT"+i+".456S"));
            }
        }
    }

    private class ToString implements Runnable {
        public void run() {
            int n = N_CREATE/2;
            for (int i=0; i<n; i++) {
                z += factory.millis(i).toString().length();
            }
        }
    }

    private class AddSeconds implements Runnable {
        public void run() {
            T duration = factory.seconds(0);
            for (int i=0; i<N_ADD; i++) {
                duration = duration.plusSeconds(i);
            }
            z += zero.compareTo(duration);
        }
    }

    private class AddMilliSeconds implements Runnable {
        public void run() {
            T duration = factory.seconds(0);
            for (int i=0; i<N_ADD; i++) {
                duration = duration.plusMillis(i);
            }
            z += zero.compareTo(duration);
        }
    }

    private class AddNanoSeconds implements Runnable {
        public void run() {
            T duration = factory.seconds(0);
            for (int i=0; i<N_ADD; i++) {
                duration = duration.plusNanos(i+1234567);
            }
            z += zero.compareTo(duration);
        }
    }

    private ArrayList<T> buildValues(int N) {
        ArrayList<T> values = new ArrayList<T>(N);
        for (int i=N/3; --i >= 0;) {
            values.add(factory.seconds(values.size()));
        }
        for (int i=N/3; --i >= 0;) {
            values.add(factory.millis(1000L*values.size()+i));
        }
        for (int i=N/3; --i >= 0;) {
            values.add(factory.nanos(1000000000L*values.size()+i));
        }
        return values;
    }

    private class AddDuration implements Runnable {
        public void run() {
            ArrayList<T> values = buildValues(2000);
            for (int i=0; i<values.size(); i++) {
                T x = values.get(i);
                for (int j=0; j<values.size(); j++) {
                    z += zero.compareTo(x.plus(values.get(j)));
                }
            }
        }
    }

    private class Compare implements Runnable {
        public void run() {
            ArrayList<T> values = buildValues(2500);
            for (int i=0; i<values.size(); i++) {
                T x = values.get(i);
                for (int j=0; j<values.size(); j++) {
                    z += x.compareTo(values.get(j));
                }
            }
        }
    }
}
