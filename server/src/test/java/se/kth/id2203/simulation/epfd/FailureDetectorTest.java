package se.kth.id2203.simulation.epfd;

import org.junit.Test;
import se.kth.id2203.simulation.SimulationResultMap;
import se.kth.id2203.simulation.SimulationResultSingleton;
import se.kth.id2203.simulation.beb.BEBScenarioGen;
import se.sics.kompics.simulator.SimulationScenario;
import se.sics.kompics.simulator.run.LauncherComp;

public class FailureDetectorTest {

    private static final int NUM_MESSAGES = 10;
    private final SimulationResultMap res = SimulationResultSingleton.getInstance();

    @Test
    public void StrongCompletenessTest() {
        // Eventually, every process that crashes is permanently suspected by every correct process.
        long seed = 123;
        SimulationScenario.setSeed(seed);
        SimulationScenario simpleBootScenario = EPFDScenarioGen.simpleEPFD(3);
        simpleBootScenario.simulate(LauncherComp.class);
    }

    @Test
    public void EventualStrongAccuracyTest() {
        // Eventually, no correct process is suspected by any correct process.

    }
}
