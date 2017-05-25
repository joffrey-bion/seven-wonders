package org.luxons.sevenwonders.test.client;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.springframework.messaging.simp.stomp.StompSession.Subscription;

public class Channel<T> {

    private static final int DEFAULT_RECEPTION_TIMEOUT_IN_SECONDS = 10;

    private final BlockingQueue<T> messageQueue;

    private final Subscription subscription;

    Channel(Subscription subscription, BlockingQueue<T> messageQueue) {
        this.subscription = subscription;
        this.messageQueue = messageQueue;
    }

    public T next() throws InterruptedException {
        return messageQueue.poll(DEFAULT_RECEPTION_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
    }

    public T next(long timeout, TimeUnit unit) throws InterruptedException {
        return messageQueue.poll(timeout, unit);
    }

    public void unsubscribe() {
        subscription.unsubscribe();
    }
}
