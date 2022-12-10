package components;

import static org.lwjgl.glfw.GLFW.*;

import java.util.ArrayList;
import java.util.List;

import editor.PropertiesWindow;
import jindow.GameObject;
import jindow.KeyListener;
import jindow.Prefabs;
import jindow.Window;
import util.AssetPool;
import util.Settings;

public class KeyControls extends Component {
	
	private boolean playerSpawned;
	private float moveSensitivity = .5f;
	
	@Override
	public void update(float dt) {
		if (!playerSpawned && KeyListener.isKeyPressed(GLFW_KEY_SPACE)) {
			float playerSizeX = .5f;
			float playerSizeY = .85f;
			GameObject player = Prefabs.generatePlayerObject(
					new Sprite().setTexture(AssetPool.getTexture("assets/images/java-chan-body.png")),
					playerSizeX, playerSizeY
			);
//			GameObject pivot = Prefabs.generatePlayerPivot(
//					player, 
//					new Sprite().setTexture(AssetPool.getTexture("assets/images/blendImage2.png")),
//					playerSizeX * .25f, playerSizeX * .25f
//			);
			GameObject playerHead = Prefabs.generatePlayerHeadObject(
					player, 
					new Sprite().setTexture(AssetPool.getTexture("assets/images/java-chan-head.png")), 
					playerSizeX * 1.5f, playerSizeX * 1.5f
			);
			GameObject hammer = Prefabs.generateHammerObject(
					player, 
					new Sprite().setTexture(AssetPool.getTexture("assets/images/blendImage2.png")),
					playerSizeX * .25f, playerSizeX * .25f 
			);
			GameObject hammerVisual = Prefabs.generateHammerVisual(
					player,
					new Sprite().setTexture(AssetPool.getTexture("assets/images/hammer.png")), 
					playerSizeY * 1.55f, playerSizeX * .5f);
			
			Window.getScene().addGameObjectToScene(hammer);
			Window.getScene().addGameObjectToScene(playerHead);
			Window.getScene().addGameObjectToScene(player);
//			Window.getScene().addGameObjectToScene(pivot);
			Window.getScene().addGameObjectToScene(hammerVisual);

			Window.getImGuiLayer().getPropertiesWindow().setActiveGameObject(player);
			
			playerSpawned = true;
		}
	}
	
	@Override
	public void editorUpdate(float dt) {
		PropertiesWindow propertiesWindow = Window.getImGuiLayer().getPropertiesWindow();
		GameObject activeGameObject = propertiesWindow.getActiveGameObject();
		
		List<GameObject> activeGameObjects = propertiesWindow.getActiveGameObjects();
		
		if (KeyListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL) &&
				KeyListener.keyBeginPress(GLFW_KEY_D) && activeGameObject != null) {
			GameObject newObject = activeGameObject.copy();
			Window.getScene().addGameObjectToScene(newObject);
			newObject.transform.position.add(Settings.GRID_WIDTH, 0);
			propertiesWindow.setActiveGameObject(newObject);
		} else if (KeyListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL) &&
				KeyListener.keyBeginPress(GLFW_KEY_D) && activeGameObjects.size() > 1) {
			List<GameObject> gameObjects = new ArrayList<>(activeGameObjects);
			propertiesWindow.clearSelected();
			for (GameObject obj : gameObjects) {
				GameObject copy = obj.copy();
				Window.getScene().addGameObjectToScene(copy);
				propertiesWindow.addActiveGameObject(copy);
			}
		} else if (KeyListener.keyBeginPress(GLFW_KEY_DELETE)) {
			for (GameObject obj : activeGameObjects) {
				if (obj.getName().equals("Player")) {
					obj.getComponent(PlayerController.class).getHammer().destroy();
				}
				obj.destroy();
			}
			propertiesWindow.clearSelected();
		} else if (KeyListener.keyBeginPress(GLFW_KEY_UP)) {
			for (GameObject obj : activeGameObjects) {
				obj.transform.position.y += moveSensitivity;
			}
		} else if (KeyListener.keyBeginPress(GLFW_KEY_DOWN)) {
			for (GameObject obj : activeGameObjects) {
				obj.transform.position.y -= moveSensitivity;
			}
		} else if (KeyListener.keyBeginPress(GLFW_KEY_LEFT)) {
			for (GameObject obj : activeGameObjects) {
				obj.transform.position.x -= moveSensitivity;
			}
		} else if (KeyListener.keyBeginPress(GLFW_KEY_RIGHT)) {
			for (GameObject obj : activeGameObjects) {
				obj.transform.position.x += moveSensitivity;
			}
		}
	}
}
