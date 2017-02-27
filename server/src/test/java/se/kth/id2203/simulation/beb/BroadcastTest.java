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

        int node1_sent = res.get("/192.168.0.1:45678sent", Integer.class);
        int node2_sent = res.get("/192.168.0.2:45678sent", Integer.class);
        int node3_sent = res.get("/192.168.0.3:45678sent", Integer.class);
        int node4_sent = res.get("/192.168.0.4:45678sent", Integer.class);
        int node5_sent = res.get("/192.168.0.5:45678sent", Integer.class);
        int node1_got = res.get("/192.168.0.1:45678got", Integer.class);
        int node2_got = res.get("/192.168.0.2:45678got", Integer.class);
        int node3_got = res.get("/192.168.0.3:45678got", Integer.class);
        int node4_got = res.get("/192.168.0.4:45678got", Integer.class);
        int node5_got = res.get("/192.168.0.5:45678got", Integer.class);

        junit.framework.Assert.assertEquals(node1_got, node1_sent + node2_sent + node3_sent + node4_sent + node5_sent);
        junit.framework.Assert.assertEquals(node2_got, node1_sent + node2_sent + node3_sent + node4_sent + node5_sent);
        junit.framework.Assert.assertEquals(node3_got, node1_sent + node2_sent + node3_sent + node4_sent + node5_sent);
        junit.framework.Assert.assertEquals(node4_got, node1_sent + node2_sent + node3_sent + node4_sent + node5_sent);
        junit.framework.Assert.assertEquals(node5_got, node1_sent + node2_sent + node3_sent + node4_sent + node5_sent);
    }

    @Test
    public void CrashOneBroadcaster() {
        long seed = 123;
        SimulationScenario.setSeed(seed);
        SimulationScenario simpleBootScenario = BEBScenarioGen.broadcastKillScenario();
        simpleBootScenario.simulate(LauncherComp.class);

        int node1_sent = res.get("/192.168.0.1:45678sent", Integer.class);
        int node2_sent = res.get("/192.168.0.2:45678sent", Integer.class);
        int node3_sent = res.get("/192.168.0.3:45678sent", Integer.class);
        int node4_sent = res.get("/192.168.0.4:45678sent", Integer.class);
        int node5_sent = res.get("/192.168.0.5:45678sent", Integer.class);
        int node1_got = res.get("/192.168.0.1:45678got", Integer.class);
        int node2_got = res.get("/192.168.0.2:45678got", Integer.class);
        int node3_got = res.get("/192.168.0.3:45678got", Integer.class);
        int node4_got = res.get("/192.168.0.4:45678got", Integer.class);
        int node5_got = res.get("/192.168.0.5:45678got", Integer.class);

        System.out.println("node1 got: " + node1_got);
        System.out.println("node2 got: " + node2_got);
        System.out.println("node3 got: " + node3_got);
        System.out.println("node4 got: " + node4_got);
        System.out.println("node5 got: " + node5_got);

        junit.framework.Assert.assertEquals(node1_got, node1_sent + node2_sent + node3_sent + node4_sent + node5_sent);
        junit.framework.Assert.assertEquals(node2_got, node1_sent + node2_sent + node3_sent + node4_sent + node5_sent);
        junit.framework.Assert.assertEquals(node3_got, node1_sent + node2_sent + node3_sent + node4_sent + node5_sent);
        junit.framework.Assert.assertEquals(node4_got, node1_sent + node2_sent + node3_sent + node4_sent + node5_sent);
        junit.framework.Assert.assertEquals(node5_got, node1_sent + node2_sent + node3_sent + node4_sent + node5_sent);
    }

}