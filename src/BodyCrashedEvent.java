import EventSystem.Event;

public class BodyCrashedEvent implements Event {

    private Body crashedBody;

    BodyCrashedEvent(Body crashedBody) {
        this.crashedBody = crashedBody;
    }

    public String getName() {
        return "body crashed";
    }

    Body getCrashedBody() {
        return crashedBody;
    }

}
