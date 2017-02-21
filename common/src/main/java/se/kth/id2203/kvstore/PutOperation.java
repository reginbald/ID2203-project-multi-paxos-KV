package se.kth.id2203.kvstore;

import com.google.common.base.MoreObjects;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;
import java.util.UUID;

public class PutOperation implements KompicsEvent, Serializable {
    private static final long serialVersionUID = 2525600659083087179L; //Todo: what is this?
    public final String key;
    public final String value;
    public final UUID id;

    public PutOperation(String key, String value) {
        this.key = key;
        this.id = UUID.randomUUID();
        this.value = value;
    }
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("key", key)
                .add("value", value)
                .toString();
    }
}