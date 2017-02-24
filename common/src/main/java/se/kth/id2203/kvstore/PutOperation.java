package se.kth.id2203.kvstore;

import com.google.common.base.MoreObjects;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;
import java.util.UUID;

public class PutOperation extends Operation implements KompicsEvent, Serializable {
    private static final long serialVersionUID = 2525600659083087179L; //Todo: what is this?
    public final String key;
    public final String value;

    public PutOperation(String key, String value) {
        super(UUID.randomUUID());
        this.key = key;
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