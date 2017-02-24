package se.kth.id2203.kvstore;

import com.google.common.base.MoreObjects;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;
import java.util.UUID;

public class CasOperation extends Operation implements KompicsEvent, Serializable {
    private static final long serialVersionUID = 2525600659083087179L;
    public final String key;
    public final String referenceValue;
    public final String newValue;

    public CasOperation(String key, String referenceValue, String newValue) {
        super(UUID.randomUUID());
        this.key = key;
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