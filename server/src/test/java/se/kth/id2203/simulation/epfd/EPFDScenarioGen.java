package se.kth.id2203.simulation.epfd;

import se.kth.id2203.networking.NetAddress;
import se.kth.id2203.simulation.beb.BEBObserver;
import se.kth.id2203.simulation.beb.BEBParent;
import se.kth.id2203.simulation.beb.EPFDObserver;
import se.sics.kompics.Init;
import se.sics.kompics.network.Address;
import se.sics.kompics.simulator.SimulationScenario;
import se.sics.kompics.simulator.adaptor.Operation;
import se.sics.kompics.simulator.adaptor.Operation1;
import se.sics.kompics.simulator.adaptor.distributions.extra.BasicIntSequentialDistribution;
import se.sics.kompics.simulator.events.system.KillNodeEvent;
import se.sics.kompics.simulator.events.system.StartNodeEvent;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

public abstract class EPFDScenarioGen {

    private static final Operation1 startServerOp = new Operation1<StartNodeEvent, Integer>() {

        @Override
        public StartNodeEvent generate(final Integer self) {
            return new StartNodeEvent() {
                final NetAddress selfAdr;

                {
                    try {
                        selfAdr = new NetAddress(InetAddress.getByName("192.168.0." + self), 45678);
                    } catch (UnknownHostException ex) {
                        throw new RuntimeException(ex);
                    }
                }

                @Override
                public Address getNodeAddress() {
                    return selfAdr;
                }

                @Override
                public Class getComponentDefinition() {
                    return EPFDParent.class;
                }

                @Override
                public String toString() {
                    return "StartNode<" + selfAdr.toString() + ">";
                }

                @Override
                public Init getComponentInit() {
                    return Init.NONE;
                }

                @Override
                public Map<String, Object> initConfigUpdate() {
                    HashMap<String, Object> config = new HashMap<>();
                    config.put("id2203.project.address", selfAdr);
                    return config;
                }
            };
        }
    };

    static Operation1 killOp = new Operation1<KillNodeEvent, Integer>() {
        @Override
        public KillNodeEvent generate(final Integer self) {
            return new KillNodeEvent() {
                NetAddress selfAddress;
                {
                    try {
                        selfAddress = new NetAddress(InetAddress.getByName("192.168.0." + self), 45678);
                    } catch (UnknownHostException ex) {
                        throw new RuntimeException(ex);
                    }
                }

                @Override
                public Address getNodeAddress() {
                    return selfAddress;
                }

                @Override
                public String toString() {
                    return "Kill<" + selfAddress.toString() + ">";
                }
            };
        }
    };

    public static SimulationScenario failureScenario() {

        return new SimulationScenario() {
            {

                final SimulationScenario.StochasticProcess servers = new SimulationScenario.StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(1000));
                        raise(5, startServerOp, new BasicIntSequentialDistribution(1));
                    }
                };

                //observer.start();
                servers.start();
                terminateAfterTerminationOf(100000, servers);
            }
        };
    }

    public static SimulationScenario failureKillScenario() {

        return new SimulationScenario() {
            {

                final SimulationScenario.StochasticProcess servers = new SimulationScenario.StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(1000));
                        raise(5, startServerOp, new BasicIntSequentialDistribution(1));
                    }
                };

                SimulationScenario.StochasticProcess killer = new SimulationScenario.StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(0));
                        raise(1, killOp, new BasicIntSequentialDistribution((1)));
                    }
                };

                servers.start();
                killer.startAfterTerminationOf(0, servers);
                terminateAfterTerminationOf(1000*10000, servers);
            }
        };
    }
}

