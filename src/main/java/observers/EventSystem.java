package observers;

import java.util.ArrayList;
import java.util.List;

import jindow.GameObject;
import observers.events.Event;

public class EventSystem {
	private static List<Observer> observers = new ArrayList<>();
	
	public static void addObserver(Observer observer) {
		observers.add(observer);
	}
	
	public static void notify(GameObject object, Event event) {
		for (Observer observer : observers)
			observer.onNotify(object, event);
	}
}
