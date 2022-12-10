package physics2d.components;

import org.joml.Vector2f;

import components.Component;
import renderer.DebugDraw;

public class CircleCollider extends Component {
	private Vector2f offset = new Vector2f();
	private float radius = 1f;
	
	@Override
	public void editorUpdate(float dt) {
		DebugDraw.addCircle(this.gameObject.transform.position, radius);
	}
	
	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}
	
	public Vector2f getOffset() {
		return this.offset;
	}

	public void setOffset(Vector2f newOffset) {
		this.offset.set(newOffset);
	}
}
