package components;

import static org.lwjgl.glfw.GLFW.*;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import editor.PropertiesWindow;
import jindow.GameObject;
import jindow.KeyListener;
import jindow.MouseListener;
import jindow.Prefabs;
import jindow.Window;

public class Gizmo extends Component {
	private Vector4f xAxisColor = new Vector4f(1, .3f, .3f, 1);
	private Vector4f xAxisColorHover = new Vector4f(1, 0, 0, 1);
	private Vector4f yAxisColor = new Vector4f(.3f, .3f, 1, 1);
	private Vector4f yAxisColorHover = new Vector4f(0, 0, 1, 1);
	
	private Vector2f xAxisOffset = new Vector2f(24f / 80f, -6f / 80f);
	private Vector2f yAxisOffset = new Vector2f(-7f / 80f, 21f / 80f);
	
	private float gizmoWidth = 16f / 80f;
	private float gizmoHeight = 48f / 80f;
	
	private boolean using;
	
	protected boolean xAxisActive;
	protected boolean yAxisActive;
	
	private GameObject xAxisObject;
	private GameObject yAxisObject;
	
	private SpriteRenderer xAxisSprite;
	private SpriteRenderer yAxisSprite;
	
	private PropertiesWindow propertiesWindow;
	
	protected GameObject activeGameObject;
	
	public Gizmo(Sprite arrowSprite, PropertiesWindow propertiesWindow) {
		this.xAxisObject = Prefabs.generateSpriteObject(arrowSprite, gizmoWidth, gizmoHeight);
		this.yAxisObject = Prefabs.generateSpriteObject(arrowSprite, gizmoWidth, gizmoHeight);
		
		this.xAxisSprite = this.xAxisObject.getComponent(SpriteRenderer.class);
		this.yAxisSprite = this.yAxisObject.getComponent(SpriteRenderer.class);
	
		this.propertiesWindow = propertiesWindow;
		
		this.xAxisObject.addComponent(new NonPickable());
		this.yAxisObject.addComponent(new NonPickable());
		
		Window.getScene().addGameObjectToScene(this.xAxisObject);
		Window.getScene().addGameObjectToScene(this.yAxisObject);
	}
	
	@Override
	public void start() {
		this.xAxisObject.transform.rotation = 90f;
		this.yAxisObject.transform.rotation = 180f;
		this.xAxisObject.transform.zIndex = 100;
		this.yAxisObject.transform.zIndex = 100;
		this.xAxisObject.setSerializable(false);
		this.yAxisObject.setSerializable(false);
	}
	
	@Override
	public void update(float dt) {
		if (using) {
			this.setInactive();
		}
		
		xAxisObject.getComponent(SpriteRenderer.class).setColor(new Vector4f(0, 0, 0, 0));
		yAxisObject.getComponent(SpriteRenderer.class).setColor(new Vector4f(0, 0, 0, 0));
	}
	
	@Override
	public void editorUpdate(float dt) {		
		if (!using) return;
		
		this.activeGameObject = this.propertiesWindow.getActiveGameObject();
		if (this.activeGameObject != null) {
			this.setActive();	// Activate gizmos
		} else {
			this.setInactive();
			return;
		}
		
		boolean xAxisHot = checkXHoverState();
		boolean yAxisHot = checkYHoverState();
		
		if ((xAxisHot || xAxisActive) && MouseListener.isDragging() && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
			xAxisActive = true;
			yAxisActive = false;
		} else if ((yAxisHot || yAxisActive) && MouseListener.isDragging() && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
			xAxisActive = false;
			yAxisActive = true;
		} else {
			xAxisActive = yAxisActive = false;
		}
		
		if (this.activeGameObject != null) {
			this.xAxisObject.transform.position.set(this.activeGameObject.transform.position);
			this.yAxisObject.transform.position.set(this.activeGameObject.transform.position);
			
			this.xAxisObject.transform.position.add(new Vector2f(this.xAxisOffset.x, this.xAxisOffset.y));
			this.yAxisObject.transform.position.add(new Vector2f(this.yAxisOffset.x, this.yAxisOffset.y));
		}
	}
	
	private boolean checkXHoverState() {
		Vector2f mousePos = MouseListener.getWorld();
		if (mousePos.x <= xAxisObject.transform.position.x + (gizmoHeight / 2.0f) 
				&& mousePos.x >= xAxisObject.transform.position.x - (gizmoWidth / 2.0f)
				&& mousePos.y >= xAxisObject.transform.position.y - (gizmoHeight / 2.0f) 
				&& mousePos.y <= xAxisObject.transform.position.y + (gizmoWidth / 2.0f)) {
			xAxisSprite.setColor(xAxisColorHover);
			return true;
		}
		
		xAxisSprite.setColor(xAxisColor);
		return false;
	}

	private boolean checkYHoverState() {
		Vector2f mousePos = MouseListener.getWorld();
		if (mousePos.x <= yAxisObject.transform.position.x + (gizmoWidth / 2.0f)
				&& mousePos.x >= yAxisObject.transform.position.x - gizmoWidth - (gizmoWidth / 2.0f)
				&& mousePos.y <= yAxisObject.transform.position.y + (gizmoHeight / 2.0f)
				&& mousePos.y >= yAxisObject.transform.position.y - (gizmoHeight / 2.0f)) {
			yAxisSprite.setColor(yAxisColorHover);
			return true;
		}
		
		yAxisSprite.setColor(yAxisColor);
		return false;
	}

	private void setActive() {
		this.xAxisSprite.setColor(xAxisColor);
		this.yAxisSprite.setColor(yAxisColor);
	}
	
	private void setInactive() {
		this.activeGameObject = null;
		this.xAxisSprite.setColor(new Vector4f(0, 0, 0, 0));
		this.yAxisSprite.setColor(new Vector4f(0, 0, 0, 0));
	}
	
	public boolean isUsing() {
		return this.using;
	}
	
	public void setUsing(boolean value) {
		this.using = value;
		if (!value) this.setInactive();
	}
}
