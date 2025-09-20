package com.example.optimisticlock.entity;

import java.time.LocalDateTime;

public abstract class BaseEntity {

    private LocalDateTime zzcmnFdate;
    private Integer version;

    public LocalDateTime getZzcmnFdate() {
        return zzcmnFdate;
    }

    public void setZzcmnFdate(LocalDateTime zzcmnFdate) {
        this.zzcmnFdate = zzcmnFdate;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}