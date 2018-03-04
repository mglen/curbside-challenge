package com.breadturtle.model;

import java.io.Serializable;
import java.util.*;

public class SecretTree implements Serializable {
    static final long serialVersionUID = -1575596474666040550L;

    private String secret;
    private final List<SecretTree> orderedChildren = Collections.synchronizedList(new ArrayList<>());

    public SecretTree() {
    }

    public void addChild(SecretTree child) {
        this.orderedChildren.add(child);
    }

    String getSecret() {
        return secret == null ? "" : secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String walkSecrets() {
        StringBuilder answer = new StringBuilder().append(getSecret());
        orderedChildren.forEach(child -> answer.append(child.walkSecrets()));
        return answer.toString();
    }
}
