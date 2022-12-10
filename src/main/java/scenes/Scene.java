package scenes;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.joml.Vector2f;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import components.Component;
import components.ComponentDeserializer;
import jindow.Camera;
import jindow.GameObject;
import jindow.GameObjectDeserializer;
import jindow.Transform;
import physics2d.Physics2D;
import renderer.Renderer;

public class Scene {
	private Camera camera;
	private List<GameObject> gameObjects;
	private Renderer renderer = new Renderer();
	private SceneInitializer sceneInitializer;
	private Physics2D physics2D;
	
	private boolean isRunning = false;
	
	public Scene(SceneInitializer sceneInitializer) {
		this.sceneInitializer = sceneInitializer;
		this.physics2D = new Physics2D();
		this.renderer = new Renderer();
		this.gameObjects = new ArrayList<>();
		this.isRunning = false;
	}
	
	public Physics2D getPhysics() {
		return this.physics2D;
	}
	
	public void init() {		
		this.camera = new Camera(new Vector2f(-2.0f, -2.0f));	

		this.sceneInitializer.loadResources(this);
		this.sceneInitializer.init(this);
	}
	
	public void start() {
		for (int i = 0; i < gameObjects.size(); i++) {
			GameObject currentGameObject = gameObjects.get(i);
			currentGameObject.start();
			
			this.renderer.add(currentGameObject);
			this.physics2D.add(currentGameObject);
		}
		isRunning = true;
	}
	
	public void addGameObjectToScene(GameObject gameObject) {
		if (!isRunning) {
			gameObjects.add(gameObject);
		} else {
			gameObjects.add(gameObject);
			gameObject.start();
			
			this.renderer.add(gameObject); 
			this.physics2D.add(gameObject);
		}
	}
	
	public void destroy() {
		for (GameObject gameObject : gameObjects)
			gameObject.destroy();
	}
	
	public List<GameObject> getGameObjects() {
		return this.gameObjects;
	}
	
	public GameObject getGameObject(int gameObjectID) {
		Optional<GameObject> result = this.gameObjects.stream()
				.filter(gameObject -> gameObject.getUid() == gameObjectID)
				.findFirst();
		
		return result.orElse(null);
	}
	
	public void update(float dt) {
		this.camera.adjustProjection();
		this.physics2D.update(dt);
		
		for (int i = 0; i < gameObjects.size(); i++) {
			GameObject gameObject = gameObjects.get(i);
			gameObject.update(dt);
			
			if (gameObject.isDead()) {
				gameObjects.remove(i);
				this.renderer.destroyGameObject(gameObject);
				this.physics2D.destroyGameObject(gameObject);
				i--;
			}
		}
	}
	
	public void editorUpdate(float dt) {
		this.camera.adjustProjection();
		
		for (int i = 0; i < gameObjects.size(); i++) {
			GameObject gameObject = gameObjects.get(i);
			gameObject.editorUpdate(dt);
			
			if (gameObject.isDead()) {
				gameObjects.remove(i);
				this.renderer.destroyGameObject(gameObject);
				this.physics2D.destroyGameObject(gameObject);
				i--;
			}
		}
	}
	
	public void render() {
		this.renderer.render();
	}
	
	public Camera camera() {
		return this.camera;
	}
	
	public void imgui() {
		this.sceneInitializer.imgui();
	}
	
	public GameObject createGameObject(String name) {
		GameObject gameObject = new GameObject(name);
		gameObject.addComponent(new Transform());
		gameObject.transform = gameObject.getComponent(Transform.class);
		
		return gameObject;
	}
	
	public void save() {
		Gson gson = new GsonBuilder()
				.setPrettyPrinting()
				.registerTypeAdapter(Component.class, new ComponentDeserializer())
				.registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
				.create();
		
		try {
			FileWriter writer = new FileWriter("level.txt");
			
			List<GameObject> serializableObjects = new ArrayList<>();
			for (GameObject gameObject : this.gameObjects)
				if (gameObject.isSerializable())
					serializableObjects.add(gameObject);
			
			writer.write(gson.toJson(serializableObjects));
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void load() {
		Gson gson = new GsonBuilder()
				.setPrettyPrinting()
				.registerTypeAdapter(Component.class, new ComponentDeserializer())
				.registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
				.create();
		
		String inFile = "";
		try {
			inFile = new String(Files.readAllBytes(Paths.get("level.txt")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (!inFile.equals("")) {
			int maxGoID = -1;
			int maxCompID = -1;
			GameObject[] gameObjects = gson.fromJson(inFile, GameObject[].class);
			for (int i = 0; i < gameObjects.length; i++) {
				addGameObjectToScene(gameObjects[i]);
				
				for (Component c : gameObjects[i].getAllComponent()) {
					maxCompID = Math.max(maxCompID, c.getUid());	
				}
				maxGoID = Math.max(maxGoID, gameObjects[i].getUid());
			}
			
			GameObject.init(++maxGoID);
			Component.init(++maxCompID);
			
//			System.out.println(maxGoID + " " + maxCompID); 
		}
	}
}
