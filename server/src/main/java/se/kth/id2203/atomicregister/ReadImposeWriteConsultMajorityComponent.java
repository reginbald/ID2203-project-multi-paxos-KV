package se.kth.id2203.atomicregister;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Negative;

public class ReadImposeWriteConsultMajorityComponent extends ComponentDefinition {
    final static Logger LOG = LoggerFactory.getLogger(ReadImposeWriteConsultMajorityComponent.class);


    Negative<AtomicRegister> nnar = provides(AtomicRegister.class);
    //Positive<BestEffortBroadcast> beb = requires(BestEffortBroadcast.class);
    //Positive<PerfectPointToPointLink> pp2p = requires(PerfectPointToPointLink.class);
}
