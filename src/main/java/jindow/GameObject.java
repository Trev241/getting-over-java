package jindow;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import components.Component;
import components.ComponentDeserializer;
import components.SpriteRenderer;
import imgui.ImGui;
import util.AssetPool;

public class GameObject {
	private static int ID_COUNTER = 0;
	
	private int uid = -1;
	
	private boolean serializable = true;
	private boolean isDead;
	
	public String name;
	private List<Component> components;
	public transient Transform transform;
	
	
	public GameObject(String name) {
		this.name = name;
		this.components = new ArrayList<Component>();
		
		this.uid = ID_COUNTER++;
	}
	
	// The function return type has been designated as T. By specifying
	// "<T extends Component>" we are indicating that this Type is a subclass
	// of Component (or a subclass of a class that inherits Component and so on).
	// In the parameter list, such a component can be obtained by specifying the name of
	// the subclass itself which is a child of Component
	public <T extends Component> T getComponent(Class<T> componentClass) {
		for (Component c : components) {
			if (componentClass.isAssignableFrom(c.getClass()))
				try {
					return componentClass.cast(c);
				} catch (ClassCastException e) {
					e.printStackTrace();
					assert false : "ERROR: Casting Component";
				}
		}
		
		return null;
	}
	
	public <T extends Component> void removeComponent(Class<T> componentClass) {
		for (int i = 0; i < components.size(); i++) {
			Component c = components.get(i);
			if (componentClass.isAssignableFrom(c.getClass())) {
				components.remove(i);
				return;
			}
		}
	}
	
	public Component addComponent(Component component) {
		component.generateUid();
		this.components.add(component);
		component.gameObject = this;
		
		return component;
	}
	
	public void update(float dt) {
		for (int i = 0; i < components.size(); i++) 
			components.get(i).update(dt);
	}
	
	public void editorUpdate(float dt) {
		for (int i = 0; i < components.size(); i++) 
			components.get(i).editorUpdate(dt);
	}
	
	public void start() {
		for (int i = 0; i < components.size(); i++) 
			components.get(i).start();	
	}
	
	public void imgui() {
		for (Component c : components) {
			if (ImGui.collapsingHeader(c.getClass().getSimpleName()))
				c.imgui();
		}
	}

	public void destroy() {
		this.isDead = true;
		for (int i = 0; i < components.size(); i++)
			components.get(i).destroy();
	}
	
	public GameObject copy() {
		// TODO Auto-generated method stub
		Gson gson = new GsonBuilder()
				.registerTypeAdapter(Component.class, new ComponentDeserializer())
				.registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
				.create();
		String objAsJson = gson.toJson(this);
		
		GameObject dupObj = gson.fromJson(objAsJson, GameObject.class);
		
		dupObj.generateUid();
		for (Component c : dupObj.getAllComponent()) {
			c.generateUid();
		}
		SpriteRenderer sprite = dupObj.getComponent(SpriteRenderer.class);
		if (sprite != null && sprite.getTexture() != null) {
			sprite.setTexture(AssetPool.getTexture(sprite.getTexture().getFilepath()));
		}
		
		return dupObj;
	}
	
	public static void init(int maxUid) {
		ID_COUNTER = maxUid;
	}
	
	public int getUid() {
		return this.uid;
	}

	public List<Component> getAllComponent() {
		return this.components;
	}
	
	public boolean isSerializable() {
		return serializable;
	}

	public boolean isDead() {
		return this.isDead;
	}
	
	public void setSerializable(boolean serializable) {
		this.serializable = serializable;
	}
	
	public void generateUid() {
		this.uid = ID_COUNTER++;
	}

	public String getName() {
		return name;
	}

}
