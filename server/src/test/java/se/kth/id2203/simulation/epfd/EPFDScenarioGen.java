package se.kth.id2203.simulation.epfd;

import se.kth.id2203.networking.NetAddress;
import se.kth.id2203.simulation.beb.BEBObserver;
import se.kth.id2203.simulation.beb.BEBParent;
import se.sics.kompics.Init;
import se.sics.kompics.network.Address;
import se.sics.kompics.simulator.SimulationScenario;
import se.sics.kompics.simulator.adaptor.Operation;
import se.sics.kompics.simulator.adaptor.Operation1;
import se.sics.kompics.simulator.adaptor.distributions.extra.BasicIntSequentialDistribution;
import se.sics.kompics.simulator.events.system.StartNodeEvent;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

public class EPFDScenarioGen {
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
    static Operation startObserverOp = new Operation<StartNodeEvent>() {
        @Override
        public StartNodeEvent generate() {
            return new StartNodeEvent() {
                NetAddress selfAdr;

                {
                    try {
                        selfAdr = new NetAddress(InetAddress.getByName("0.0.0.0"), 0);
                    } catch (UnknownHostException ex) {
                        throw new RuntimeException(ex);
                    }
                }

                @Override
                public Map<String, Object> initConfigUpdate() {
                    HashMap<String, Object> config = new HashMap<>();
                    config.put("pingpong.simulation.checktimeout", 2000);
                    return config;
                }

                @Override
                public Address getNodeAddress() {
                    return selfAdr;
                }

                @Override
                public Class getComponentDefinition() {
                    return EPFDObserver.class;
                }

                @Override
                public Init getComponentInit() {
                    return new EPFDObserver.Init(3);
                }
            };
        }
    };
    public static SimulationScenario simpleEPFD(final int servers) {
        return new SimulationScenario() {
            {
                SimulationScenario.StochasticProcess observer = new SimulationScenario.StochasticProcess() {
                    {
                        raise(1, startObserverOp);
                    }
                };

                SimulationScenario.StochasticProcess cluster = new SimulationScenario.StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(1000));
                        raise(3, startServerOp, new BasicIntSequentialDistribution(1));
                    }
                };

                observer.start();
                cluster.startAfterTerminationOf(0, observer);
                terminateAfterTerminationOf(10000, observer);
            }
        };
    }
}
