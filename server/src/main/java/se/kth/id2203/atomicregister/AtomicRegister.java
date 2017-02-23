package se.kth.id2203.atomicregister;

import se.sics.kompics.PortType;

public class AtomicRegister extends PortType {
    {
        //Input to the port
        request(AR_Read_Request.class);
        request(AR_Write_Request.class);
        request(AR_CAS_Request.class);

        //Output to the port
        indication(AR_Read_Response.class);
        indication(AR_Write_Response.class);
        indication(AR_CAS_Response.class);
    }
}