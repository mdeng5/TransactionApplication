package dev.codescreen.store;

import dev.codescreen.store.entity.UnprocessedMessage;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.Queue;

@Component
public class UnprocessedMessageQueue {
    private final Queue<UnprocessedMessage> unprocessedMessageQueue = new LinkedList<>();

    public synchronized void enqueue(UnprocessedMessage unprocessedMessage) {
        unprocessedMessageQueue.offer(unprocessedMessage);
    }

    public synchronized UnprocessedMessage dequeue() {
        return unprocessedMessageQueue.poll();
    }

    public synchronized boolean isEmpty() {
        return unprocessedMessageQueue.isEmpty();
    }
}
