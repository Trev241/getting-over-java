package observers;

import jindow.GameObject;
import observers.events.Event;

public interface Observer {
	void onNotify(GameObject object, Event event);
}
