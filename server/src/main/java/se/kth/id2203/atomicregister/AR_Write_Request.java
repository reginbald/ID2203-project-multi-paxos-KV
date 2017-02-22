package se.kth.id2203.atomicregister;

import se.sics.kompics.KompicsEvent;

import java.io.Serializable;

public class AR_Write_Request implements KompicsEvent, Serializable {
    private static final long serialVersionUID = -5481045153332189199L;
    public final Object value;

    public AR_Write_Request(Object value){
        this.value = value;
    }
}
