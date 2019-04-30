package EventSystem;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class EventDispatcher {

    private Map<String, ArrayList<EventListener<Event>>> listeners = new LinkedHashMap<>();

    public void addListener(String eventName, EventListener<Event> eventListener) {
        if(!listeners.containsKey(eventName)) listeners.put(eventName, new ArrayList<EventListener<Event>>());
        listeners.get(eventName).add(eventListener);
    }

    public void dispatch(Event event) {
        for(EventListener<Event> listener: listeners.getOrDefault(event.getName(), new ArrayList<>())) {
            listener.handle(event);
        }
    }

    public static EventDispatcher create() {
        return new EventDispatcher();
    }

}
