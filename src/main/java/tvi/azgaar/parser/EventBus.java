package tvi.azgaar.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EventBus {
    private final List<Consumer<String>> subscribers = new ArrayList<>();

    // 🔌 Subscribe a UI panel or logger file to listen to events
    public synchronized void subscribe(Consumer<String> subscriber) {
        if (subscriber != null) {
            subscribers.add(subscriber);
        }
    }

    // 📢 Broadcast a micro-log event straight down the line
    public synchronized void publish(String message) {
        for (Consumer<String> subscriber : subscribers) {
            subscriber.accept(message);
        }
    }
}
