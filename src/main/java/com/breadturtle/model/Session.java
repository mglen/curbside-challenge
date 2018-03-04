package com.breadturtle.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class Session {
    private String id;
    private LocalDateTime expireAt;

    public Session() { }

    public String getId() {
        return id;
    }

    @JsonProperty("session")
    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getExpireAt() {
        return expireAt;
    }

    @JsonProperty("expire_at")
    public void setExpireAt(String expireAt) {
        this.expireAt = LocalDateTime.parse(expireAt.substring(0, expireAt.length() - 1));
    }
}
