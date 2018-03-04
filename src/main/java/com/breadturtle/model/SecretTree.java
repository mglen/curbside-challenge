package com.breadturtle.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SecretTree implements Serializable {
    static final long serialVersionUID = -1575596474666040550L;

    private String secret;
    private Map<String, SecretTree> children = new ConcurrentHashMap<>();
    private List<String> orderedChildren = new ArrayList<>();

    public SecretTree() {
    }

    public SecretTree(String id) {
        orderedChildren.add(id);
    }

    public static SecretTree fromChallengeResponse(ChallengeResponse r) {
        SecretTree tree = new SecretTree();
        tree.secret = r.getSecret();
        tree.orderedChildren = r.getNext();
        return tree;
    }

    public void addChild(String id, SecretTree child) {
        this.children.put(id, child);
    }

    String getSecret() {
        return secret == null ? "" : secret;
    }

    public String walkSecrets() {
        StringBuilder answer = new StringBuilder().append(getSecret());
        orderedChildren.forEach(child -> {
            if (children.containsKey(child)) {
                answer.append(children.get(child).walkSecrets());
            }
        });
        return answer.toString();
    }
}
