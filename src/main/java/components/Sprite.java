package components;

import org.joml.Vector2f;

import renderer.Texture;

public class Sprite {
	private Texture texture = null;
	private Vector2f[] texCoords = {
			new Vector2f(1.0f, 1.0f),
			new Vector2f(1.0f, 0.0f),
			new Vector2f(0.0f, 0.0f),
			new Vector2f(0.0f, 1.0f)	
	};
	
	private float width;
	private float height;
	
	public Texture getTexture() {
		return this.texture;
	}
	
	public Vector2f[] getTexCoords() {
		return this.texCoords;
	}

	public Sprite setTexture(Texture texture) {
		this.texture = texture;
		return this;
	}
	
	public Sprite setTexCoords(Vector2f[] texCoords) {
		this.texCoords = texCoords;
		return this;
	}

	public float getWidth() {
		return this.width;
	}
	
	public Sprite setWidth(float width) {
		this.width = width;
		return this;
	}
	
	public float getHeight() {
		return this.height;
	}
	
	public Sprite setHeight(float height) {
		this.height = height;
		return this;
	}

	public int getTextureID() {
		return texture == null ? -1 : texture.getID();
	}
}
