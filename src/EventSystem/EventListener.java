package EventSystem;

public interface EventListener<Event> {
    void handle(Event event);
}
