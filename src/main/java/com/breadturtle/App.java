package com.breadturtle;

import com.breadturtle.model.ChallengeResponse;
import com.breadturtle.model.SecretTree;

import javax.ws.rs.ClientErrorException;
import java.io.*;
import java.nio.file.Files;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class App {

    public static void main(String[] args) throws InterruptedException, IOException, ClassNotFoundException {
        SecretTree root = null;
        File cache = new File("cached-challenge");

        if (cache.exists()) {
            InputStream inputStream = Files.newInputStream(cache.toPath());
            root = (SecretTree) new ObjectInputStream(inputStream).readObject();
        } else {
            root = getSecretTree();
            try (OutputStream outputStream = Files.newOutputStream(cache.toPath())) {
                try (ObjectOutputStream out = new ObjectOutputStream(outputStream)) {
                    out.writeObject(root);
                }
            }
        }

        System.out.printf("Answer is: %s\n", root.walkSecrets());
    }

    private static SecretTree getSecretTree() throws InterruptedException {
        SecretTree root = new SecretTree("start");
        CurbsideClient client = new CurbsideClient();
        System.out.printf("Client created, session is: %s\n", client.getSessionId());

        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                33, 33, 1L, TimeUnit.SECONDS, new LinkedBlockingDeque<>());

        class CurbsideSolver implements Runnable {
            private final String id;
            private SecretTree parent;

            private CurbsideSolver(String id, SecretTree parent) {
                this.id = id;
                this.parent = parent;
            }

            @Override
            public void run() {
                try {
                    ChallengeResponse challenge = client.getChallenge(this.id);
                    if (challenge.getMessage() != null) {
                        System.out.printf("Got message: %s\n", challenge.getMessage());
                    }
                    SecretTree node = SecretTree.fromChallengeResponse(challenge);
                    parent.addChild(this.id, node);
                    for (String s : challenge.getNext()) {
                        threadPoolExecutor.execute(new CurbsideSolver(s, node));
                    }
                } catch (ClientErrorException e) {
                    if (e.getResponse().getStatus() == 429) {
                        System.out.println("Going too fast");
                    } else {
                        throw e;
                    }
                }
            }
        }
        threadPoolExecutor.execute(new CurbsideSolver("start", root));

        while (threadPoolExecutor.getQueue().size() > 0) {
            Thread.sleep(1000);
        }
        threadPoolExecutor.shutdownNow();
        return root;
    }
}
