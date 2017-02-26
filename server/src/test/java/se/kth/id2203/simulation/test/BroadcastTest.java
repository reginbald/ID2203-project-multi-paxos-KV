package se.kth.id2203.simulation.test;

import org.junit.Test;
import se.kth.id2203.simulation.BEBScenarioGen;
import se.kth.id2203.simulation.SimulationResultMap;
import se.kth.id2203.simulation.SimulationResultSingleton;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.simulator.SimulationScenario;
import se.sics.kompics.simulator.run.LauncherComp;

public class BroadcastTest extends ComponentDefinition {

    private static final int NUM_MESSAGES = 10;
    private final SimulationResultMap res = SimulationResultSingleton.getInstance();

    @Test
    public void BestEffortValidityTest() {
        long seed = 123;
        SimulationScenario.setSeed(seed);
        SimulationScenario simpleBootScenario = BEBScenarioGen.broadcastScenario();
        simpleBootScenario.simulate(LauncherComp.class);

        junit.framework.Assert.assertEquals(new Integer(5), res.get("/192.168.0.1:45678", Integer.class));
        junit.framework.Assert.assertEquals(new Integer(5), res.get("/192.168.0.2:45678", Integer.class));
        junit.framework.Assert.assertEquals(new Integer(5), res.get("/192.168.0.3:45678", Integer.class));
        junit.framework.Assert.assertEquals(new Integer(5), res.get("/192.168.0.4:45678", Integer.class));
        junit.framework.Assert.assertEquals(new Integer(5), res.get("/192.168.0.5:45678", Integer.class));
    }

    @Test
    public void NoDuplicationTest() {
        long seed = 123;
        SimulationScenario.setSeed(seed);
        SimulationScenario simpleBootScenario = BEBScenarioGen.broadcastScenario();
        simpleBootScenario.simulate(LauncherComp.class);

        junit.framework.Assert.assertEquals(new Integer(5), res.get("/192.168.0.1:45678", Integer.class));
        junit.framework.Assert.assertEquals(new Integer(5), res.get("/192.168.0.2:45678", Integer.class));
        junit.framework.Assert.assertEquals(new Integer(5), res.get("/192.168.0.3:45678", Integer.class));
        junit.framework.Assert.assertEquals(new Integer(5), res.get("/192.168.0.4:45678", Integer.class));
        junit.framework.Assert.assertEquals(new Integer(5), res.get("/192.168.0.5:45678", Integer.class));
    }

    @Test
    public void NoCreationTest(){
        long seed = 123;
        SimulationScenario.setSeed(seed);
        SimulationScenario simpleBootScenario = BEBScenarioGen.broadcastScenario();
        simpleBootScenario.simulate(LauncherComp.class);

        junit.framework.Assert.assertEquals(new Integer(5), res.get("/192.168.0.1:45678", Integer.class));
        junit.framework.Assert.assertEquals(new Integer(5), res.get("/192.168.0.2:45678", Integer.class));
        junit.framework.Assert.assertEquals(new Integer(5), res.get("/192.168.0.3:45678", Integer.class));
        junit.framework.Assert.assertEquals(new Integer(5), res.get("/192.168.0.4:45678", Integer.class));
        junit.framework.Assert.assertEquals(new Integer(5), res.get("/192.168.0.5:45678", Integer.class));

    }
}