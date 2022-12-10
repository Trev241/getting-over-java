package components;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

import java.util.HashSet;
import java.util.Set;

import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4f;

import editor.PropertiesWindow;
import jindow.GameObject;
import jindow.KeyListener;
import jindow.MouseListener;
import jindow.Window;
import renderer.DebugDraw;
import renderer.PickingTexture;
import scenes.Scene;
import util.Settings;

public class MouseControls extends Component {
	GameObject holdingObject = null;
	
	private float debounceTime = .2f;
	private float debounce = debounceTime;

	private boolean boxSelectSet;
	
	private Vector2f boxSelectStart = new Vector2f();
	private Vector2f boxSelectEnd = new Vector2f();
	
	public void pickupObject(GameObject gameObject) {
		if (this.holdingObject != null) {
			this.holdingObject.destroy();
		}
		
		this.holdingObject = gameObject;
		this.holdingObject.getComponent(SpriteRenderer.class).setColor(new Vector4f(.8f, .8f, .8f, .5f));
		this.holdingObject.addComponent(new NonPickable());
		Window.getScene().addGameObjectToScene(gameObject);
	}
	
	public void place() {
		GameObject newObj = this.holdingObject.copy();
		newObj.getComponent(SpriteRenderer.class).setColor(new Vector4f(1f, 1f, 1f, 1f));
		newObj.removeComponent(NonPickable.class);
		Window.getScene().addGameObjectToScene(newObj);
	}
	
	@Override
	public void editorUpdate(float dt) {
//		System.out.println(MouseControls.class.descriptorString() + ": x = " + MouseListener.getWorldX() + " y = " + MouseListener.getWorldY());
		debounce -= dt;
		
		PickingTexture pickingTexture = Window.getImGuiLayer().getPropertiesWindow().getPickingTexture();
		Scene currentScene = Window.getScene();
		
		if (holdingObject != null) {
			float x = MouseListener.getWorldX();
			float y = MouseListener.getWorldY();
			
			holdingObject.transform.position.x = ((int) Math.floor(x / Settings.GRID_WIDTH) * Settings.GRID_WIDTH) + Settings.GRID_WIDTH / 2.0f;
			holdingObject.transform.position.y = ((int) Math.floor(y / Settings.GRID_HEIGHT) * Settings.GRID_HEIGHT) + Settings.GRID_HEIGHT / 2.0f;
			
			if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
				float halfWidth = Settings.GRID_WIDTH / 2.0f;
				float halfHeight = Settings.GRID_HEIGHT / 2.0f;
				if (MouseListener.isDragging() &&
						!blockInSquare(holdingObject.transform.position.x - halfWidth, 
								holdingObject.transform.position.y - halfHeight)) {
					place();
				} else if (!MouseListener.isDragging() && debounce < 0) {
					place();
					debounce = debounceTime;
				}
			}
			
			if (KeyListener.isKeyPressed(GLFW_KEY_ESCAPE)) {
				holdingObject.destroy();
				holdingObject = null;
			}
		} else if (!MouseListener.isDragging() && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT) && debounce < 0) {
			int x = (int) MouseListener.getScreenX();
			int y = (int) MouseListener.getScreenY();
			
			int gameObjectID = pickingTexture.readPixel(x, y);
			GameObject pickedObject = currentScene.getGameObject(gameObjectID);
//			System.out.println("[PROPERTIES WINDOW]: Picked up object: " + pickedObject.getName());
			if (pickedObject != null && pickedObject.getComponent(NonPickable.class) == null) {
				Window.getImGuiLayer().getPropertiesWindow().setActiveGameObject(pickedObject);
			} else if (pickedObject == null && !MouseListener.isDragging()) {
				Window.getImGuiLayer().getPropertiesWindow().clearSelected();
			}
			
			this.debounce = 0f;
		} else if (MouseListener.isDragging() && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
			if (!boxSelectSet) {
				Window.getImGuiLayer().getPropertiesWindow().clearSelected();
				boxSelectStart = MouseListener.getScreen();
				boxSelectSet = true;
			}
			boxSelectEnd = MouseListener.getScreen();
			Vector2f boxSelectStartWorld = MouseListener.screenToWorld(boxSelectStart);
			Vector2f boxSelectEndWorld = MouseListener.screenToWorld(boxSelectEnd);
			Vector2f halfSize = (new Vector2f(boxSelectEndWorld).sub(boxSelectStartWorld)).mul(.5f);
			
			DebugDraw.addBox2D((new Vector2f(boxSelectStartWorld)).add(halfSize), new Vector2f(halfSize).mul(2.0f), 0);
		} else if (boxSelectSet) {
			boxSelectSet = false;
			int screenStartX = (int) boxSelectStart.x;
			int screenStartY = (int) boxSelectStart.y;
			int screenEndX = (int) boxSelectEnd.x;
			int screenEndY = (int) boxSelectEnd.y;
			boxSelectStart.zero();
			boxSelectEnd.zero();
		 
			if (screenEndX < screenStartX) {
				int tmp = screenStartX;
				screenStartX = screenEndX;
				screenEndX = tmp;
			}
			
			if (screenEndY < screenStartY) {
				int tmp = screenStartY;
				screenStartY = screenEndY;
				screenEndY = tmp;
			}
			
			float[] gameObjectIDs = pickingTexture.readPixels(
					new Vector2i(screenStartX, screenStartY), 
					new Vector2i(screenEndX, screenEndY)
			);
			Set<Integer> uniqueGameObjectIDs = new HashSet<>();
			for (float objID : gameObjectIDs) {
				uniqueGameObjectIDs.add((int) objID);
			}
			
			for (Integer gameObjectID : uniqueGameObjectIDs) {
				GameObject pickedObject = Window.getScene().getGameObject(gameObjectID);
				if (pickedObject != null && pickedObject.getComponent(NonPickable.class) == null) {
					Window.getImGuiLayer().getPropertiesWindow().addActiveGameObject(pickedObject);
				}
			}
			
			
		}
		
	}

	private boolean blockInSquare(float x, float y) {
		PropertiesWindow propertiesWindow = Window.getImGuiLayer().getPropertiesWindow();
		Vector2f start = new Vector2f(x, y);
		Vector2f end = new Vector2f(start).add(new Vector2f(Settings.GRID_WIDTH, Settings.GRID_HEIGHT));
		Vector2f startScreenf = MouseListener.worldToScreen(start);
		Vector2f endScreenf = MouseListener.worldToScreen(end);
		Vector2i startScreen = new Vector2i((int) startScreenf.x + 2, (int) startScreenf.y + 2);
		Vector2i endScreen = new Vector2i((int) endScreenf.x - 2, (int) endScreenf.y - 2);
		float[] gameObjectIDs = propertiesWindow.getPickingTexture().readPixels(startScreen, endScreen);
		
		for (int i = 0; i < gameObjectIDs.length; i++) {
			if (gameObjectIDs[i] >= 0) {
				GameObject pickedObj = Window.getScene().getGameObject((int) gameObjectIDs[i]);
				if (pickedObj.getComponent(NonPickable.class) == null) {
					return true;
				}
			}
		}
		return false;
	}
}
