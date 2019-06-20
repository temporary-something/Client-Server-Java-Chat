package model;

import java.io.Serializable;

public abstract class BasePojo implements Serializable {

    private static final long serialVersionUID = -8671841846544199710L;

    private static long count = 0;

    protected BasePojo() {
        this.id = ++count;
    }

    private Long id;

    public Long getId() {
        return id;
    }
}
