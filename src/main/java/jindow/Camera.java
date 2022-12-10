package jindow;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Camera {
	private float zoom = 1.0f;
	private float projectionWidth = 9;
	private float projectionHeight = 4.5f;
	
	private Matrix4f projectionMatrix;
	private Matrix4f viewMatrix;
	private Matrix4f inverseProjection;
	private Matrix4f inverseView;
	private Vector2f projectionSize = new Vector2f(projectionWidth, projectionHeight);
	
	public Vector2f position;
	
	public Camera(Vector2f position) {
		this.position = position;
		this.projectionMatrix = new Matrix4f();
		this.viewMatrix = new Matrix4f();
		this.inverseProjection = new Matrix4f();
		this.inverseView = new Matrix4f();
		
		adjustProjection();
	}
	
	public void adjustProjection() {
		projectionMatrix.identity();
		projectionMatrix.ortho(0.0f, projectionSize.x * this.zoom, 0.0f, projectionSize.y * this.zoom, 0.0f, 100.0f);
		projectionMatrix.invert(inverseProjection);
	}
	
	public Matrix4f getViewMatrix() {
		// Looking at negative -1 along the Z axis
		Vector3f cameraFront = new Vector3f(0.0f, 0.0f, -1.0f);
		Vector3f cameraUp = new Vector3f(0.0f, 1.0f, 0.0f);
		
		this.viewMatrix.identity();
		// Arguments are respectively - position, center, 
		viewMatrix.lookAt(new Vector3f(position.x, position.y, 20.0f),
				cameraFront.add(position.x, position.y, 0.0f),
				cameraUp);
	 	this.viewMatrix.invert(inverseView);
	 	
		return this.viewMatrix;
	}
	
	public Matrix4f getProjectionMatrix() { 
		return projectionMatrix;
	}
	
	public Matrix4f getInverseProjection() {
		return this.inverseProjection;
	}
	
	public Matrix4f getInverseView() {
		return this.inverseView;
	}
	
	public Vector2f getProjectionSize() {
		return this.projectionSize;
	}
	
	public float getZoom() {
		return this.zoom;
	}
	
	public void setZoom(float zoom) {
		this.zoom = zoom;
	}
	
	public void addZoom(float value) {
		this.zoom += value;
	}
}
