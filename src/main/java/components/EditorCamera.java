package components;

import static org.lwjgl.glfw.GLFW.*;

import org.joml.Vector2f;

import jindow.Camera;
import jindow.KeyListener;
import jindow.MouseListener;

public class EditorCamera extends Component {
	private float dragDebounce = .032f;
	private float dragSensitivity = 30.0f;
	private float scrollSensitivity = .1f;
	private float lerpTime = .0f;
	
	private Camera levelEditorCamera;
	private Vector2f clickOrigin;
	private boolean reset;
	
	public EditorCamera(Camera levelEditorCamera) {
		this.levelEditorCamera = levelEditorCamera;
		this.clickOrigin = new Vector2f();
	}
	
	@Override
	public void editorUpdate(float dt) {
		if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE) && dragDebounce > 0) {
			this.clickOrigin = MouseListener.getWorld();
			dragDebounce -= dt;
			return;
		} else if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE)) {
			Vector2f mousePos = MouseListener.getWorld();
			Vector2f delta = new Vector2f(mousePos).sub(this.clickOrigin);
			levelEditorCamera.position.sub(delta.mul(dt).mul(dragSensitivity));
			this.clickOrigin.lerp(mousePos, dt);
		}
		
		if (dragDebounce <= .0f && !MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE)) {
			dragDebounce = .1f;
		}
		
		if (MouseListener.getScrollY() != .0f) {
			float addValue = (float) Math.pow(Math.abs(MouseListener.getScrollY() * scrollSensitivity),
					1 / levelEditorCamera.getZoom());
			addValue *= -Math.signum(MouseListener.getScrollY());
			levelEditorCamera.addZoom(addValue);
		}
		
		if (KeyListener.isKeyPressed(GLFW_KEY_PERIOD)) {
			reset = true;
		}
		
		if (reset) {
			levelEditorCamera.position.lerp(new Vector2f(), lerpTime);
			levelEditorCamera.setZoom(this.levelEditorCamera.getZoom() +
					((1f - levelEditorCamera.getZoom()) * lerpTime));
			this.lerpTime += .1f * dt;
			if (Math.abs(levelEditorCamera.position.x) <= .5f &&
					Math.abs(levelEditorCamera.position.y) <= .5f) {
				this.lerpTime = .0f;
				levelEditorCamera.position.set(0.0f, 0.0f);
				this.levelEditorCamera.setZoom(1f);
				reset = false;
			}
		}
	}
}
