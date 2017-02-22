package se.kth.id2203.atomicregister;

import se.sics.kompics.KompicsEvent;

import java.io.Serializable;

public class AR_Read_Response implements KompicsEvent, Serializable {
    private static final long serialVersionUID = -5481045153332189199L;
    public final Object value;

    public AR_Read_Response(Object value){
        this.value = value;
    }
}
