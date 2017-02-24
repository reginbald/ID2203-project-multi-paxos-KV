package se.kth.id2203.kvstore;

import java.io.Serializable;
import java.util.UUID;

public class Operation implements Serializable {
    private static final long serialVersionUID = 25256006522222179L;
    public final UUID id;

    public Operation(UUID id) {
        this.id = id;
    }
}
