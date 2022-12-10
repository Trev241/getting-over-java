package jindow;

import org.joml.Vector2f;
import org.joml.Vector3f;

import components.Component;
import editor.JImGui;

public class Transform extends Component {
	public Vector2f position;
	public Vector2f offsetPivot;
	public Vector2f scale;
	
	public int zIndex;
	
	public float rotation;
	
	public Transform() {
		init(new Vector2f(), new Vector2f(), new Vector2f());
	}
	
	public Transform(Vector2f position) {
		init(position, new Vector2f(), new Vector2f());
	}

	public Transform(Vector2f position, Vector2f scale) {
		init(position, scale, new Vector2f());
	}
	
	public void init(Vector2f position, Vector2f scale, Vector2f offsetPivot) {
		this.position = position;
		this.scale = scale;
		this.offsetPivot = offsetPivot;
	}
	
	public Transform copy() {
		return new Transform(new Vector2f(this.position), new Vector2f(this.scale));
	}
	
	@Override
	public void imgui() {
		gameObject.name = JImGui.inputText("Name: ", gameObject.name);
		JImGui.drawVec2Control("Position", this.position);
		JImGui.drawVec2Control("Scale", this.scale, 32.0f);
		this.rotation = JImGui.drawFloat("Rotation", this.rotation);
		this.zIndex = JImGui.drawInt("z", this.zIndex);
	}
	
	public void copy(Transform t) {
		t.position.set(this.position);
		t.scale.set(this.scale);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof Transform)) return false;
		
		Transform t = (Transform) o;
		return t.position.equals(this.position) && t.scale.equals(this.scale)
				&& t.rotation == this.rotation;
	}
	
	public int getZIndex() {
		return zIndex;
	}
}
