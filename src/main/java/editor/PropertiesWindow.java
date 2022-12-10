package editor;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector4f;

import components.SpriteRenderer;
import imgui.ImGui;
import jindow.GameObject;
import physics2d.components.Box2DCollider;
import physics2d.components.CircleCollider;
import physics2d.components.PillboxCollider;
import physics2d.components.Rigidbody2D;
import renderer.PickingTexture;

public class PropertiesWindow {
	private List<GameObject> activeGameObjects;
	private List<Vector4f> activeGameObjectsOgColors;
	private GameObject activeGameObject;
	private PickingTexture pickingTexture;
	
	public PropertiesWindow(PickingTexture pickingTexture) {
		this.activeGameObjects = new ArrayList<>();
		this.pickingTexture = pickingTexture;
		this.activeGameObjectsOgColors = new ArrayList<>();
	}
	
	public void imgui() {
		if (activeGameObjects.size() == 1 && activeGameObjects.get(0) != null) {
			activeGameObject = activeGameObjects.get(0);
			ImGui.begin("Properties");
			
			if (ImGui.beginPopupContextWindow("ComponentAdder")) {
				if (ImGui.menuItem("Add Rigidbody")) {
					if (activeGameObject.getComponent(Rigidbody2D.class) == null) {
						activeGameObject.addComponent(new Rigidbody2D());
					}
				}

				if (ImGui.menuItem("Add Box Collider")) {
					if (activeGameObject.getComponent(Box2DCollider.class) == null
							&& activeGameObject.getComponent(CircleCollider.class) == null) {
						activeGameObject.addComponent(new Box2DCollider());
					} 
				}

				if (ImGui.menuItem("Add Circle Collider")) {
					if (activeGameObject.getComponent(CircleCollider.class) == null 
							&& activeGameObject.getComponent(Box2DCollider.class) == null) {
						activeGameObject.addComponent(new CircleCollider());
					}
				}
				
				if (ImGui.menuItem("Add Pillbox Collider")) {
					activeGameObject.addComponent(new PillboxCollider());
				}

				ImGui.endPopup();
			}
			
			activeGameObject.imgui();
			ImGui.end();
		}
	}
	
	public GameObject getActiveGameObject() {
		return activeGameObjects.size() == 1 ? this.activeGameObjects.get(0) : null;		
	}
	
	public List<GameObject> getActiveGameObjects() {
		return this.activeGameObjects;
	}

	public void clearSelected() {
		if (activeGameObjectsOgColors.size() > 0) {
			int i = 0;
			for (GameObject obj : activeGameObjects) {
				SpriteRenderer spr = obj.getComponent(SpriteRenderer.class);
				if (spr != null) {
					spr.setColor(activeGameObjectsOgColors.get(i));
				}
				i++;
			}
		}
		
		this.activeGameObjects.clear();
		this.activeGameObjectsOgColors.clear();
	}
	
	public void setActiveGameObject(GameObject object) {
		if (object != null) {
			clearSelected();
			this.activeGameObjects.add(object);
		}
	}
	
	public void addActiveGameObject(GameObject obj) {
		SpriteRenderer spr = obj.getComponent(SpriteRenderer.class);
		if (spr != null) {
			this.activeGameObjectsOgColors.add(new Vector4f(spr.getColor()));
			spr.setColor(new Vector4f(.8f, .8f, .8f, .8f));
		} else {
			this.activeGameObjectsOgColors.add(new Vector4f());
		}
		this.activeGameObjects.add(obj);
	}

	public PickingTexture getPickingTexture() {
		return this.pickingTexture;
	}
}
