package se.kth.id2203.simulation.beb;

import org.junit.Test;
import se.kth.id2203.simulation.SimulationResultMap;
import se.kth.id2203.simulation.SimulationResultSingleton;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.simulator.SimulationScenario;
import se.sics.kompics.simulator.run.LauncherComp;

public class BroadcastTest extends ComponentDefinition {

    private static final int NUM_MESSAGES = 10;
    private final SimulationResultMap res = SimulationResultSingleton.getInstance();

    @Test
    public void AllNodesAreCorrect() {
        long seed = 123;
        SimulationScenario.setSeed(seed);
        SimulationScenario simpleBootScenario = BEBScenarioGen.broadcastScenario();
        simpleBootScenario.simulate(LauncherComp.class);

        junit.framework.Assert.assertEquals(new Integer(1), res.get("/192.168.0.1:45678sent", Integer.class));
        junit.framework.Assert.assertEquals(new Integer(1), res.get("/192.168.0.2:45678sent", Integer.class));
        junit.framework.Assert.assertEquals(new Integer(1), res.get("/192.168.0.3:45678sent", Integer.class));
        junit.framework.Assert.assertEquals(new Integer(1), res.get("/192.168.0.4:45678sent", Integer.class));
        junit.framework.Assert.assertEquals(new Integer(1), res.get("/192.168.0.5:45678sent", Integer.class));
        junit.framework.Assert.assertEquals(new Integer(5), res.get("/192.168.0.1:45678got", Integer.class));
        junit.framework.Assert.assertEquals(new Integer(5), res.get("/192.168.0.2:45678got", Integer.class));
        junit.framework.Assert.assertEquals(new Integer(5), res.get("/192.168.0.3:45678got", Integer.class));
        junit.framework.Assert.assertEquals(new Integer(5), res.get("/192.168.0.4:45678got", Integer.class));
        junit.framework.Assert.assertEquals(new Integer(5), res.get("/192.168.0.5:45678got", Integer.class));
    }

    @Test
    public void CrashOneBroadcaster() {
        long seed = 123;
        SimulationScenario.setSeed(seed);
        SimulationScenario simpleBootScenario = BEBScenarioGen.broadcastKillScenario();
        simpleBootScenario.simulate(LauncherComp.class);

        junit.framework.Assert.assertEquals(new Integer(1), res.get("/192.168.0.1:45678sent", Integer.class));
        junit.framework.Assert.assertEquals(new Integer(1), res.get("/192.168.0.2:45678sent", Integer.class));
        junit.framework.Assert.assertEquals(new Integer(1), res.get("/192.168.0.3:45678sent", Integer.class));
        junit.framework.Assert.assertEquals(new Integer(1), res.get("/192.168.0.4:45678sent", Integer.class));
        junit.framework.Assert.assertEquals(new Integer(1), res.get("/192.168.0.5:45678sent", Integer.class));
        junit.framework.Assert.assertEquals(new Integer(5), res.get("/192.168.0.1:45678got", Integer.class));
        junit.framework.Assert.assertEquals(new Integer(5), res.get("/192.168.0.2:45678got", Integer.class));
        junit.framework.Assert.assertEquals(new Integer(5), res.get("/192.168.0.3:45678got", Integer.class));
        junit.framework.Assert.assertEquals(new Integer(5), res.get("/192.168.0.4:45678got", Integer.class));
        junit.framework.Assert.assertEquals(new Integer(5), res.get("/192.168.0.5:45678got", Integer.class));
    }

}