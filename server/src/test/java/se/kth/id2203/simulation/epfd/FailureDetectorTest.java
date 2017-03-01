package se.kth.id2203.simulation.epfd;

import org.junit.Assert;
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
    public void FiveCorrectProcessesTest() {
        // Eventually, every process that crashes is permanently suspected by every correct process.
        long seed = 123;
        SimulationScenario.setSeed(seed);
        SimulationScenario simpleBootScenario = EPFDScenarioGen.failureScenario();
        simpleBootScenario.simulate(LauncherComp.class);

        junit.framework.Assert.assertEquals(new Integer(0), res.get("/192.168.0.1:45678", Integer.class));
        junit.framework.Assert.assertEquals(new Integer(0), res.get("/192.168.0.2:45678", Integer.class));
        junit.framework.Assert.assertEquals(new Integer(0), res.get("/192.168.0.3:45678", Integer.class));
        junit.framework.Assert.assertEquals(new Integer(0), res.get("/192.168.0.4:45678", Integer.class));
        junit.framework.Assert.assertEquals(new Integer(0), res.get("/192.168.0.5:45678", Integer.class));
    }

    @Test
    public void OneNodeCrashesTest() {
        // Eventually, no correct process is suspected by any correct process.
        long seed = 123;
        SimulationScenario.setSeed(seed);
        SimulationScenario simpleBootScenario = EPFDScenarioGen.failureKillScenario();
        simpleBootScenario.simulate(LauncherComp.class);

        junit.framework.Assert.assertEquals(new Integer(1), res.get("/192.168.0.2:45678", Integer.class));
        junit.framework.Assert.assertEquals(new Integer(1), res.get("/192.168.0.3:45678", Integer.class));
        junit.framework.Assert.assertEquals(new Integer(1), res.get("/192.168.0.4:45678", Integer.class));
        junit.framework.Assert.assertEquals(new Integer(1), res.get("/192.168.0.5:45678", Integer.class));
    }
}
