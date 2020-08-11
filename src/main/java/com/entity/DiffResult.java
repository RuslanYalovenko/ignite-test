/*
 * @author Ruslan Yalovenko (ruslan.yalovenko@gmail.com)
 */
package com.entity;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DiffResult {

    private String status;
    private Map<Integer, Integer> offset;

    public DiffResult() {
    }

    public DiffResult(String status) {
        this.status = status;
    }

    public DiffResult(String status, Map<Integer, Integer> offset) {
        this.status = status;
        this.offset = offset;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Map<Integer, Integer> getOffset() {
        return offset;
    }

    public void setOffset(Map<Integer, Integer> offset) {
        this.offset = offset;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DiffResult)) return false;
        DiffResult that = (DiffResult) o;
        return Objects.equals(status, that.status) &&
                Objects.equals(offset, that.offset);
    }

    @Override
    public int hashCode() {

        return Objects.hash(status, offset);
    }
}
