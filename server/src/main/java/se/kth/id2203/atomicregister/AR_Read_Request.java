package se.kth.id2203.atomicregister;

import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.KompicsEvent;
import java.io.Serializable;
import java.util.UUID;

public class AR_Read_Request implements KompicsEvent, Serializable {
    private static final long serialVersionUID = -2481045153332189199L;

    public final UUID request_id;
    public final String request_key;
    public final NetAddress request_source;

    public AR_Read_Request(UUID id, String key, NetAddress source) {
        this.request_id = id;
        this.request_key = key;
        this.request_source = source;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AR_Read_Request that = (AR_Read_Request) o;

        if (request_id != null ? !request_id.equals(that.request_id) : that.request_id != null) return false;
        if (request_key != null ? !request_key.equals(that.request_key) : that.request_key != null) return false;
        return request_source != null ? request_source.equals(that.request_source) : that.request_source == null;
    }

    @Override
    public int hashCode() {
        int result = request_id != null ? request_id.hashCode() : 0;
        result = 31 * result + (request_key != null ? request_key.hashCode() : 0);
        result = 31 * result + (request_source != null ? request_source.hashCode() : 0);
        return result;
    }
}
