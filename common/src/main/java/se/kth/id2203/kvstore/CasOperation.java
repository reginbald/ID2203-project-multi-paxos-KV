package se.kth.id2203.kvstore;

import com.google.common.base.MoreObjects;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;
import java.util.UUID;

public class CasOperation  implements KompicsEvent, Serializable {
    private static final long serialVersionUID = 2525600659083087179L; //Todo: what is this?
    public final String key;
    public final String referenceValue;
    public final String newValue;
    public final UUID id;

    public CasOperation(String key, String referenceValue, String newValue) {
        this.key = key;
        this.id = UUID.randomUUID();
        this.referenceValue = referenceValue;
        this.newValue = newValue;
    }
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("key", key)
                .add("referenceValue", referenceValue)
                .add("newValue", referenceValue)
                .toString();
    }
}