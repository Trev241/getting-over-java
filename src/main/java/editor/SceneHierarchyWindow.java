package editor;

import java.util.List;

import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;
import jindow.GameObject;
import jindow.Window;

public class SceneHierarchyWindow {
	private static String payloadDragDropType= "SceneHierarchy";
	
	public void imgui() {
		if (Window.isRuntimePlaying()) return;
		
		ImGui.begin("Scene Hierarchy");
		
		List<GameObject> gameObjects = Window.getScene().getGameObjects();
		int index = 0;
		for (GameObject obj : gameObjects) {
			if (!obj.isSerializable()) {
				continue; 
			}
			
			boolean treeNodeOpen = doTreeNode(obj, index);
			
			if (treeNodeOpen) {
				ImGui.treePop();
			}
			
			index++;
		}
		
		ImGui.end();
	}
	
	private boolean doTreeNode(GameObject obj, int index) {
		ImGui.pushID(index);
		boolean treeNodeOpen = ImGui.treeNodeEx(
				obj.getName(),
				ImGuiTreeNodeFlags.DefaultOpen | ImGuiTreeNodeFlags.FramePadding |
				ImGuiTreeNodeFlags.OpenOnArrow | ImGuiTreeNodeFlags.SpanAvailWidth,
				obj.getName());
		

		ImGui.popID();
		
		if (ImGui.beginDragDropSource()) {
			ImGui.setDragDropPayload(payloadDragDropType, obj);
			ImGui.text(obj.name);	
			ImGui.endDragDropSource();
		}
		
		if (ImGui.beginDragDropTarget()) {
			Object payloadObj = ImGui.acceptDragDropPayload(payloadDragDropType);
			if (payloadObj != null) {
				if (payloadObj.getClass().isAssignableFrom(GameObject.class)) {
					GameObject playerGameObject = (GameObject) payloadObj;
					System.out.println("[SCENE HIERARCHY]: Payload accepted '" + playerGameObject.name + "'");
				}
			}
			ImGui.endDragDropTarget();
		}
		
		return treeNodeOpen;
	}
}
