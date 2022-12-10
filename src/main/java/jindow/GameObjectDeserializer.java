package jindow;

import java.lang.reflect.Type;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import components.Component;

public class GameObjectDeserializer implements JsonDeserializer<GameObject> {

	@Override
	public GameObject deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
		JsonObject jsonObject = json.getAsJsonObject();
		String name = jsonObject.get("name").getAsString();
		JsonArray components = jsonObject.getAsJsonArray("components");
		
		GameObject gameObject = new GameObject(name);
		for (JsonElement e : components) {
			Component c = context.deserialize(e, Component.class);
			gameObject.addComponent(c);
		}
		gameObject.transform = gameObject.getComponent(Transform.class);
		
		return gameObject;
	}
	
}
