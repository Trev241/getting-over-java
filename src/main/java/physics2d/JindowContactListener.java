package physics2d;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.WorldManifold;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;

import components.Component;
import jindow.GameObject;

public class JindowContactListener implements ContactListener {

	@Override
	public void beginContact(Contact contact) {
		GameObject objA = (GameObject) contact.getFixtureA().getUserData();
		GameObject objB = (GameObject) contact.getFixtureB().getUserData();
		WorldManifold worldManifold = new WorldManifold();
		contact.getWorldManifold(worldManifold);
		Vector2f aNormal = new Vector2f(worldManifold.normal.x, worldManifold.normal.y);
		Vector2f bNormal = new Vector2f(aNormal.negate());
	
		for (Component c : objA.getAllComponent()) {
			c.beginCollision(objB, contact, aNormal);
		}
		
		for (Component c : objB.getAllComponent()) {
			c.beginCollision(objA, contact, bNormal);
		}
	}

	@Override
	public void endContact(Contact contact) {
		GameObject objA = (GameObject) contact.getFixtureA().getUserData();
		GameObject objB = (GameObject) contact.getFixtureB().getUserData();
		WorldManifold worldManifold = new WorldManifold();
		contact.getWorldManifold(worldManifold);
		Vector2f aNormal = new Vector2f(worldManifold.normal.x, worldManifold.normal.y);
		Vector2f bNormal = new Vector2f(aNormal.negate());
	
		for (Component c : objA.getAllComponent()) {
			c.endCollision(objB, contact, aNormal);
		}
		
		for (Component c : objB.getAllComponent()) {
			c.endCollision(objA, contact, bNormal);
		}
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse contactImpulse) {
		GameObject objA = (GameObject) contact.getFixtureA().getUserData();
		GameObject objB = (GameObject) contact.getFixtureB().getUserData();
		WorldManifold worldManifold = new WorldManifold();
		contact.getWorldManifold(worldManifold);
		Vector2f aNormal = new Vector2f(worldManifold.normal.x, worldManifold.normal.y);
		Vector2f bNormal = new Vector2f(aNormal.negate());
	
		for (Component c : objA.getAllComponent()) {
			c.postSolve(objB, contact, aNormal);
		}
		
		for (Component c : objB.getAllComponent()) {
			c.postSolve(objA, contact, bNormal);
		}	
		
	}

	@Override
	public void preSolve(Contact contact, Manifold contactImpulse) {
		GameObject objA = (GameObject) contact.getFixtureA().getUserData();
		GameObject objB = (GameObject) contact.getFixtureB().getUserData();
		WorldManifold worldManifold = new WorldManifold();
		contact.getWorldManifold(worldManifold);
		Vector2f aNormal = new Vector2f(worldManifold.normal.x, worldManifold.normal.y);
		Vector2f bNormal = new Vector2f(aNormal.negate());
	
		for (Component c : objA.getAllComponent()) {
			c.preSolve(objB, contact, aNormal);
		}
		
		for (Component c : objB.getAllComponent()) {
			c.preSolve(objA, contact, bNormal);
		}
	}
	
}
