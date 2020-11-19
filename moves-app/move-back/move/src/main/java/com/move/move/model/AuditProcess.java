package com.move.move.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
public class AuditProcess implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private long executorId;

    @Column(nullable = false)
    private Date executionDate;

    public AuditProcess() {
    }

    public AuditProcess(long executorId, Date executionDate) {
        this.executorId = executorId;
        this.executionDate = executionDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getExecutorId() {
        return executorId;
    }

    public void setExecutorId(long executorId) {
        this.executorId = executorId;
    }

    public Date getExecutionDate() {
        return executionDate;
    }

    public void setExecutionDate(Date executionDate) {
        this.executionDate = executionDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuditProcess that = (AuditProcess) o;
        return id == that.id &&
                executorId == that.executorId &&
                executionDate.equals(that.executionDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, executorId, executionDate);
    }
}
