/*
 * @author Ruslan Yalovenko (ruslan.yalovenko@gmail.com)
 */
package com.entity;

import org.apache.ignite.cache.query.annotations.QuerySqlField;

import java.io.Serializable;

public class Diff implements Serializable {

    @QuerySqlField(index = true)
    private boolean locked;
    private DiffResult item;

    public Diff() {
    }

    public Diff(boolean locked) {
        this.locked = locked;
    }

    public Diff(boolean locked, DiffResult item) {
        this.locked = locked;
        this.item = item;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public DiffResult getItem() {
        return item;
    }

    public void setItem(DiffResult item) {
        this.item = item;
    }
}
