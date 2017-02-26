package se.kth.id2203.simulation.test;

import org.junit.Test;
import se.kth.id2203.simulation.SimulationResultMap;
import se.kth.id2203.simulation.SimulationResultSingleton;

public class FailureDetectorTest {

    private static final int NUM_MESSAGES = 10;
    private final SimulationResultMap res = SimulationResultSingleton.getInstance();

    @Test
    public void StrongCompletenessTest() {
        // Eventually, every process that crashes is perma- nently suspected by every correct process.
    }

    @Test
    public void EventualStrongAccuracyTest() {
        // Eventually, no correct process is suspected by any correct process.

    }
}
