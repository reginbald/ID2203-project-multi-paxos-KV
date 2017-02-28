package se.kth.id2203.atomicregister;

import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;
import java.util.UUID;

public class AR_CAS_Request implements KompicsEvent, Serializable {
    private static final long serialVersionUID = -1111111111111111111L;

    public final UUID request_id;
    public final NetAddress request_source;

    public final String key;


    public final String referenceValue;
    public final String newValue;

    public AR_CAS_Request(UUID id, NetAddress source, String key, String referenceValue, String newValue) {
        this.request_id = id;
        this.request_source = source;

        this.key = key;
        this.referenceValue = referenceValue;
        this.newValue = newValue;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AR_CAS_Request that = (AR_CAS_Request) o;

        if (request_id != null ? !request_id.equals(that.request_id) : that.request_id != null) return false;
        if (request_source != null ? !request_source.equals(that.request_source) : that.request_source != null)
            return false;
        if (key != null ? !key.equals(that.key) : that.key != null) return false;
        if (referenceValue != null ? !referenceValue.equals(that.referenceValue) : that.referenceValue != null)
            return false;
        return newValue != null ? newValue.equals(that.newValue) : that.newValue == null;
    }

    @Override
    public int hashCode() {
        int result = request_id != null ? request_id.hashCode() : 0;
        result = 31 * result + (request_source != null ? request_source.hashCode() : 0);
        result = 31 * result + (key != null ? key.hashCode() : 0);
        result = 31 * result + (referenceValue != null ? referenceValue.hashCode() : 0);
        result = 31 * result + (newValue != null ? newValue.hashCode() : 0);
        return result;
    }
}
