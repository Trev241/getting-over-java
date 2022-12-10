package components;

import org.joml.Vector2f;
import org.joml.Vector4f;

import editor.JImGui;
import jindow.Transform;
import renderer.Texture;

public class SpriteRenderer extends Component {
	private Vector4f color = new Vector4f(1, 1, 1, 1);
	private Sprite sprite = new Sprite();
	private transient Transform lastTransform;
	
	private transient boolean isDirty = true;
	
	@Override
	public void start() {
		this.lastTransform = gameObject.transform.copy();
	}
	
	@Override
	public void update(float dt) {
		if (!this.lastTransform.equals(this.gameObject.transform)) {
			this.gameObject.transform.copy(this.lastTransform);
			isDirty = true;
		}
	}
	
	@Override
	public void editorUpdate(float dt) {
		if (!this.lastTransform.equals(this.gameObject.transform)) {
			this.gameObject.transform.copy(this.lastTransform);
			isDirty = true;
		}
	}
	
	@Override
	public void imgui() {
		if (JImGui.colorPicker4("Color Picker: ", this.color)) {
			isDirty = true;
		}
	}
	
	public Vector4f getColor() {
		return this.color;
	}
	
	public Texture getTexture() {
		return sprite.getTexture(); 
	}
	
	public Vector2f[] getTexCoords() {
		return sprite.getTexCoords();
	}
	
	// TODO make setters return current instance so that object creating can be chained
	// for sake of brevity
	// For instance,
	//	new SpriteRenderer().setColor(new Vector4f(...)).setSprite(...);
	
	public SpriteRenderer setSprite(Sprite sprite) {
		this.sprite = sprite;
		this.isDirty = true;
		
		return this;
	}
	
	public SpriteRenderer setColor(Vector4f color) {
		if (!this.color.equals(color)) {
			this.isDirty = true;
			this.color.set(color);
		}
		
		return this;
	}

	public boolean isDirty() {
		return this.isDirty;
	}
	
	public SpriteRenderer setClean() {
		this.isDirty = true;
		return this;
	}
	
	public SpriteRenderer setTexture(Texture texture) {
		this.sprite.setTexture(texture);
		return this;
	}

	public SpriteRenderer setDirty() {
		this.isDirty = true;
		return this;
	}
}
