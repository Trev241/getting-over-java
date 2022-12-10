package components;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import editor.JImGui;
import imgui.ImGui;
import imgui.type.ImInt;
import jindow.GameObject;

public abstract class Component {	
	private static int ID_COUNTER = 0;
	private int uid = -1;
	
	// WARNING!
	// gameObject must be transient in order to avoid StackOverflowError exceptions during
	// serialization. This is because of the cyclic composition relationship that components 
	// and objects have. In simpler terms, when trying to serialize the game object below, the
	// serializing process will also attempt to serialize gameObject's list of components. These
	// components themselves inherit the Component class which possesses the gameObject field once 
	// again. Thus, this generates a constant feedback loop.
	public transient GameObject gameObject = null;
	
	public void start() {
		
	}
	
	public void update(float dt) {
		
	}
	
	public void editorUpdate(float dt) {
		
	}
	
	public void beginCollision(GameObject collidingObject, Contact contact, Vector2f hitNormal) {
		
	}
	
	public void endCollision(GameObject collidingObject, Contact contact, Vector2f hitNormal) {
		
	}
	
	public void preSolve(GameObject collidingObject, Contact contact, Vector2f hitNormal) {
		
	}

	public void postSolve(GameObject collidingObject, Contact contact, Vector2f hitNormal) {
		
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void imgui() {
		try {
			// By specifying this.getClass() we can extend this functionality
			// to classes which inherit the Component class.
			// i.e. we can obtain their fields as well
			Field[] fields = this.getClass().getDeclaredFields();
			for (Field field : fields) {
				boolean isTransient = Modifier.isTransient(field.getModifiers());
				if (isTransient) continue;
				
				boolean isPrivate = Modifier.isPrivate(field.getModifiers());
				if (isPrivate) {
					field.setAccessible(true);
				}
				
				Class type = field.getType();
				Object value = field.get(this);		// Passing this reference because we want to know the value for the current component instance
				String name = field.getName();
				
				if (type == int.class) {
					int val = (int) value;
					field.set(this, JImGui.drawInt(name, val));
				} else if (type == float.class) {
					float val = (float) value;
					field.set(this, JImGui.drawFloat(name, val));
				} else if (type == boolean.class) {
					boolean val = (boolean) value;
					if (ImGui.checkbox(name + ": ", val)) {
						field.set(this, !val);
					}
				} else if (type == Vector2f.class) {
					Vector2f val = (Vector2f) value;
					JImGui.drawVec2Control(name, val);
				} else if (type == Vector3f.class) {
					Vector3f val = (Vector3f) value;
					JImGui.drawVec3Control(name, val);
				} else if (type == Vector4f.class) {
					Vector4f val = (Vector4f) value;
					float[] imVec = { val.x, val.y, val.z, val.w };
					if (ImGui.dragFloat4(name + ": ", imVec)) {
						val.set(imVec[0], imVec[1], imVec[2], imVec[4]);
					}
				} else if (type.isEnum()) {
					String[] enumValues = getEnumValues(type);
					String enumType = ((Enum) value).name();
					ImInt index = new ImInt(indexOf(enumType, enumValues));
					if (ImGui.combo(field.getName(), index, enumValues, enumValues.length)) {
						field.set(this, type.getEnumConstants()[index.get()]);
					}
				}
				
				if (isPrivate) {
					field.setAccessible(false);
				}
				
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	public void generateUid() {
		if (this.uid == -1) 
			this.uid = ID_COUNTER++;
	}
	
	public int getUid() {
		return this.uid;
	}
	
	// Upon saving and exiting, some previous objects may have reserved some ids already.
	// Hence, we remember the highest id from the last save in order to know where
	// to continue from
	public static void init(int maxUid) {
		ID_COUNTER = maxUid;
	}

	private <T extends Enum<T>> String[] getEnumValues(Class<T> enumType) {
		String[] enumValues = new String[enumType.getEnumConstants().length];
		int i = 0;
		for (T enumIntegerValue : enumType.getEnumConstants()) {
			enumValues[i++] = enumIntegerValue.name();
		}
		
		return enumValues;
	}
	
	private int indexOf(String str, String[] arr) {
		for (int i = 0; i < arr.length; i++) {
			if (str.equals(arr[i]))
				return i;
		}
		return -1;
	}
	
	public void destroy() {
		
	}
}
