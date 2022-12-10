package physics2d.components;

import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;

import components.Component;
import jindow.GameObject;
import renderer.DebugDraw;

public class Box2DCollider extends Component {
	private Vector2f halfSize = new Vector2f();
	private Vector2f origin = new Vector2f();
	private Vector2f offset = new Vector2f();
	
	@Override
	public void beginCollision(GameObject collidingObject, Contact contact, Vector2f hitNormal) {

	}
	
	@Override
	public void editorUpdate(float dt) {
		Vector2f center = new Vector2f(this.gameObject.transform.position.x, this.gameObject.transform.position.y)
				.add(this.offset);
		DebugDraw.addBox2D(center, this.halfSize, this.gameObject.transform.rotation);
	}
	
	@Override
	public void update(float dt) {
		Vector2f center = new Vector2f(this.gameObject.transform.position.x, this.gameObject.transform.position.y)
				.add(this.offset);
		DebugDraw.addBox2D(center, this.halfSize, this.gameObject.transform.rotation);
	}
	
	public Vector2f getHalfSize() {
		return halfSize;
	}

	public void setHalfSize(Vector2f halfSize) {
		this.halfSize = halfSize;
	}

	public Vector2f getOrigin() {
		return origin;
	}
	
	public Vector2f getOffset() {
		return this.offset;
	}

	public void setOffset(Vector2f newOffset) {
		this.offset.set(newOffset);
	}

}
