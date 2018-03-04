package com.breadturtle;

import com.breadturtle.model.ChallengeResponse;
import com.breadturtle.model.SecretTree;

import javax.ws.rs.ClientErrorException;
import java.io.*;
import java.nio.file.Files;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class App {

    public static void main(String[] args) throws InterruptedException, IOException, ClassNotFoundException {
        SecretTree root;
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
        String firstId = "start";
        SecretTree root = new SecretTree();
        CurbsideClient client = new CurbsideClient();
        System.out.printf("Client created, session is: %s\n", client.getSessionId());

        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                30, 30, 1L, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

        class CurbsideSolver implements Runnable {
            private final String id;
            private SecretTree node;

            private CurbsideSolver(String id, SecretTree node) {
                this.id = id;
                this.node = node;
            }

            @Override
            public void run() {
                try {
                    ChallengeResponse challenge = client.getChallenge(this.id);
                    this.node.setSecret(challenge.getSecret());
                    for (String next : challenge.getNext()) {
                        SecretTree child = new SecretTree();
                        this.node.addChild(child);
                        threadPoolExecutor.execute(new CurbsideSolver(next, child));
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
        threadPoolExecutor.execute(new CurbsideSolver(firstId, root));
        while (threadPoolExecutor.getActiveCount() > 0) {
            Thread.sleep(1000);
        }
        threadPoolExecutor.shutdownNow();
        return root;
    }
}
