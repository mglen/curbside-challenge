package com.breadturtle.model;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

public class ChallengeResponse {
    private Integer depth;
    private String id;
    private String secret;
    private String message;
    private List<String> next = new ArrayList<>();

    public ChallengeResponse() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public Integer getDepth() {
        return depth;
    }

    public void setDepth(Integer depth) {
        this.depth = depth;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getNext() {
        return next;
    }

    public void setNext(JsonNode nextValues) {
        if (nextValues.isArray()) {
            nextValues.forEach(n -> this.next.add(n.textValue()));
        } else {
            this.next.add(nextValues.textValue());
        }
    }
}
