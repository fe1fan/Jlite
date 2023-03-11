package io.xka.jlite.web.serv.control.ws;

import io.xka.jlite.web.serv.WorkerExecutors;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class WSControl {

    private Session session;

    protected LinkedTransferQueue<String> textQueue = new LinkedTransferQueue<>();

    protected LinkedTransferQueue<byte[]> binaryQueue = new LinkedTransferQueue<>();

    public WSControl(Session session) {
        this.session = session;
    }

    public void textListener(Consumer<String> consumer) {
        Runnable runnable = () -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    String message = textQueue.take();
                    consumer.accept(message);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };
        WorkerExecutors.submit(runnable);
    }

    public void binaryListener(Consumer<byte[]> consumer) {
        Runnable runnable = () -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    byte[] message = binaryQueue.take();
                    consumer.accept(message);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };
        WorkerExecutors.submit(runnable);
    }


    public void send(String message) {
        try {
            session.getRemote().sendString(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void send(byte[] bytes) {
        try {
            session.getRemote().sendBytes(ByteBuffer.wrap(bytes));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayBlockingQueue<String> async(int capacity, Duration timeout) {
        ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<>(capacity);
        Runnable runnable = () -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    String message = queue.poll(timeout.toMillis(), TimeUnit.MILLISECONDS);
                    if (message != null) {
                        session.getRemote().sendString(message);
                    }
                }
            } catch (IOException | InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };
        WorkerExecutors.submit(runnable);
        return queue;
    }

    public void close() {
        session.close();
    }
}
