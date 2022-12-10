package observers.events;

public class Event {
	public EventType type;
	
	public Event() {
		this.type = EventType.UserEvent;
	}
	
	public Event(EventType type) {
		this.type = type;
	}
}
