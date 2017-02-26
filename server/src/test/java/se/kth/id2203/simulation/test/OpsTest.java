/*
 * The MIT License
 *
 * Copyright 2017 Lars Kroll <lkroll@kth.se>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package se.kth.id2203.simulation.test;

import junit.framework.Assert;
import org.junit.Test;
import se.kth.id2203.kvstore.OpResponse;
import se.kth.id2203.simulation.ScenarioGen;
import se.kth.id2203.simulation.SimulationResultMap;
import se.kth.id2203.simulation.SimulationResultSingleton;
import se.sics.kompics.simulator.SimulationScenario;
import se.sics.kompics.simulator.run.LauncherComp;

public class OpsTest {
    
    private static final int NUM_MESSAGES = 10;
    private final SimulationResultMap res = SimulationResultSingleton.getInstance();

    @Test
    public void GetTest() {
        long seed = 123;
        SimulationScenario.setSeed(seed);
        SimulationScenario simpleBootScenario = ScenarioGen.simpleOps(3, "GET");
        res.put("messages", NUM_MESSAGES);
        simpleBootScenario.simulate(LauncherComp.class);
        for (int i = 0; i < NUM_MESSAGES; i++) {
            Assert.assertEquals("NOT_FOUND", res.get(""+i, String.class));
        }
    }

    @Test
    public void PutTest() {
        long seed = 123;
        SimulationScenario.setSeed(seed);
        SimulationScenario simpleBootScenario = ScenarioGen.simpleOps(3, "PUT");
        res.put("messages", NUM_MESSAGES);
        simpleBootScenario.simulate(LauncherComp.class);
        for (int i = 0; i < NUM_MESSAGES; i++) {
            Assert.assertEquals("OK", res.get(""+i, String.class));
        }
    }

    @Test
    public void CasTest() {
        long seed = 123;
        SimulationScenario.setSeed(seed);
        SimulationScenario simpleBootScenario = ScenarioGen.simpleOps(3, "CAS");
        res.put("messages", NUM_MESSAGES);
        simpleBootScenario.simulate(LauncherComp.class);
        for (int i = 0; i < NUM_MESSAGES; i++) {
            Assert.assertEquals("NOT_FOUND", res.get(""+i, String.class));
        }
    }

    @Test
    public void InterleaveTest() {
        long seed = 123;
        SimulationScenario.setSeed(seed);
        SimulationScenario simpleBootScenario = ScenarioGen.simpleOps(3, "INTERLEAVE");
        simpleBootScenario.simulate(LauncherComp.class);
        Assert.assertEquals("Status: NOT_FOUND Data: ", res.get("0", String.class));
        Assert.assertEquals("Status: NOT_FOUND Data: ", res.get("1", String.class));
        Assert.assertEquals("Status: OK Data: ", res.get("2", String.class));
        Assert.assertEquals("Status: OK Data: 1", res.get("3", String.class));
        Assert.assertEquals("Status: OK Data: ", res.get("4", String.class));
        Assert.assertEquals("Status: NO_MATCH Data: ", res.get("5", String.class));
        Assert.assertEquals("Status: OK Data: 5", res.get("6", String.class));
        Assert.assertEquals("Status: OK Data: ", res.get("7", String.class));
        Assert.assertEquals("Status: OK Data: ", res.get("8", String.class));
        Assert.assertEquals("Status: OK Data: 1", res.get("9", String.class));
        Assert.assertEquals("Status: OK Data: 1", res.get("10", String.class));
    }

    @Test
    public void LinearizabilityTest() {

    }

    @Test
    public void FailureDetectorTest() {

    }

    @Test
    public void PartitionTest() {

    }
}
