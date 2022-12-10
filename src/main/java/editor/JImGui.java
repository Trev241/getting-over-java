package editor;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import imgui.type.ImString;

public class JImGui {
	private static final float DEFAULT_COLUMN_WIDTH= 96.0f;
	
	public static void drawVec2Control(String label, Vector2f values) {
		drawVec2Control(label, values, 0.0f, DEFAULT_COLUMN_WIDTH);
	}
	
	public static void drawVec2Control(String label, Vector2f values, float resetValue) {
		drawVec2Control(label, values, resetValue, DEFAULT_COLUMN_WIDTH);
	}
	
	public static void drawVec2Control(String label, Vector2f values, float resetValue, float columnWidth) {
		ImGui.pushID(label);
		
		ImGui.columns(2);
		ImGui.setColumnWidth(0, columnWidth);
		ImGui.text(label);
		ImGui.nextColumn(); 
		
		ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0, 0);
		
		float lineHeight = ImGui.getFontSize() + ImGui.getStyle().getFramePaddingY() * 2.0f;
		Vector2f buttonSize = new Vector2f(lineHeight + 3.0f, lineHeight);
		float widthEach = (ImGui.calcItemWidth() - buttonSize.x * 2.0f) / 2.0f;
		
		ImGui.pushItemWidth(widthEach);
		ImGui.pushStyleColor(ImGuiCol.Button, .8f, .1f, .15f, 1.0f);
		ImGui.pushStyleColor(ImGuiCol.ButtonHovered, .9f, .2f, .2f, 1.0f);
		ImGui.pushStyleColor(ImGuiCol.ButtonActive, .8f, .1f, .15f, 1.0f);
		if (ImGui.button("x", buttonSize.x, buttonSize.y)) {
			values.x = resetValue;
		}
		ImGui.popStyleColor(3);
		 
		ImGui.sameLine();
		float[] vecValuesX = { values.x };
		ImGui.dragFloat("##x", vecValuesX, .1f);
		ImGui.popItemWidth();
		ImGui.sameLine(); 
		
//		ImGui.nextColumn();
		
		ImGui.pushItemWidth(widthEach);
		ImGui.pushStyleColor(ImGuiCol.Button, .2f, .7f, .2f, 1.0f);
		ImGui.pushStyleColor(ImGuiCol.ButtonHovered, .3f, .8f, .3f, 1.0f);
		ImGui.pushStyleColor(ImGuiCol.ButtonActive, .2f, .7f, .2f, 1.0f);
		if (ImGui.button("y", buttonSize.x, buttonSize.y)) {
			values.y = resetValue;
		}
		ImGui.popStyleColor(3);
		
		ImGui.sameLine();
		float[] vecValuesY = { values.y };
		ImGui.dragFloat("##y", vecValuesY, .1f);
		ImGui.popItemWidth();
		ImGui.sameLine();
		
		values.x = vecValuesX[0];
		values.y = vecValuesY[0];

		ImGui.popStyleVar();
		ImGui.nextColumn();
		
		// Reset Columns for other ImGui widgets that follow
		ImGui.columns(1); 
		
		ImGui.popID();
	}
	
	public static void drawVec3Control(String label, Vector3f values) {
		drawVec3Control(label, values, 0.0f, DEFAULT_COLUMN_WIDTH);
	}
	
	public static void drawVec3Control(String label, Vector3f values, float resetValue) {
		drawVec3Control(label, values, resetValue, DEFAULT_COLUMN_WIDTH);
	}
	
	public static void drawVec3Control(String label, Vector3f values, float resetValue, float columnWidth) {
		ImGui.pushID(label);
		
		ImGui.columns(2);
		ImGui.setColumnWidth(0, columnWidth);
		ImGui.text(label);
		ImGui.nextColumn(); 
		
		ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0, 0);
		
		float lineHeight = ImGui.getFontSize() + ImGui.getStyle().getFramePaddingY() * 2.0f;
		Vector2f buttonSize = new Vector2f(lineHeight + 3.0f, lineHeight);
		float widthEach = (ImGui.calcItemWidth() - buttonSize.x * 3.0f) / 3.0f;
		
		ImGui.pushItemWidth(widthEach);
		ImGui.pushStyleColor(ImGuiCol.Button, .8f, .1f, .15f, 1.0f);
		ImGui.pushStyleColor(ImGuiCol.ButtonHovered, .9f, .2f, .2f, 1.0f);
		ImGui.pushStyleColor(ImGuiCol.ButtonActive, .8f, .1f, .15f, 1.0f);
		if (ImGui.button("x", buttonSize.x, buttonSize.y)) {
			values.x = resetValue;
		}
		ImGui.popStyleColor(3);
		 
		ImGui.sameLine();
		float[] vecValuesX = { values.x };
		ImGui.dragFloat("##x", vecValuesX, .1f);
		ImGui.popItemWidth();
		ImGui.sameLine(); 
		
		ImGui.pushItemWidth(widthEach);
		ImGui.pushStyleColor(ImGuiCol.Button, .2f, .7f, .2f, 1.0f);
		ImGui.pushStyleColor(ImGuiCol.ButtonHovered, .3f, .8f, .3f, 1.0f);
		ImGui.pushStyleColor(ImGuiCol.ButtonActive, .2f, .7f, .2f, 1.0f);
		if (ImGui.button("##y", buttonSize.x, buttonSize.y)) {
			values.y = resetValue;
		}
		ImGui.popStyleColor(3);
		
		ImGui.sameLine();
		float[] vecValuesY = { values.y };
		ImGui.dragFloat("##y", vecValuesY, .1f);
		ImGui.popItemWidth();
		ImGui.sameLine();
		
		ImGui.pushItemWidth(widthEach);
		ImGui.pushStyleColor(ImGuiCol.Button, .15f, .1f, .8f, 1.0f);
		ImGui.pushStyleColor(ImGuiCol.ButtonHovered, .2f, .2f, .9f, 1.0f);
		ImGui.pushStyleColor(ImGuiCol.ButtonActive, .15f, .1f, .8f, 1.0f);
		if (ImGui.button("##z", buttonSize.x, buttonSize.y)) {
			values.z = resetValue;
		}
		ImGui.popStyleColor(3);
		
		ImGui.sameLine();
		float[] vecValuesZ = { values.z };
		ImGui.dragFloat("##z", vecValuesZ, .1f);
		ImGui.popItemWidth();
		ImGui.sameLine();
		
		values.x = vecValuesX[0];
		values.y = vecValuesY[0];
		values.z = (int) vecValuesZ[0];
		
		ImGui.popStyleVar();
		ImGui.nextColumn();
		
		// Reset Columns for other ImGui widgets that follow
		ImGui.columns(1); 
		
		ImGui.popID();
	}
	
	public static float drawFloat(String label, float value) {
		return drawFloat(label, value, DEFAULT_COLUMN_WIDTH);
	}
	
	public static float drawFloat(String label, float value, float columnWidth) {
		ImGui.pushID(label);
		
		ImGui.columns(2);
		ImGui.setColumnWidth(0, columnWidth);
		ImGui.text(label);
		ImGui.nextColumn(); 
		
		float[] valArr = { value };
		ImGui.dragFloat("##dragFloat", valArr, .1f);
		
		// Reset Columns for other ImGui widgets that follow
		ImGui.columns(1); 
		ImGui.popID();
		
		return valArr[0];
	}
	
	public static int drawInt(String label, int value) {
		return drawInt(label, value, DEFAULT_COLUMN_WIDTH);
	}
	
	public static int drawInt(String label, int value, float columnWidth) {
		ImGui.pushID(label);
		
		ImGui.columns(2);
		ImGui.setColumnWidth(0, columnWidth);
		ImGui.text(label);
		ImGui.nextColumn(); 
		
		int[] valArr = { value };
		ImGui.dragInt("##dragInt", valArr, .1f);
		
		// Reset Columns for other ImGui widgets that follow
		ImGui.columns(1); 
		ImGui.popID();
		
		return valArr[0];
	}
	
	public static boolean colorPicker4(String label, Vector4f color) {
		return colorPicker4(label, color, DEFAULT_COLUMN_WIDTH);
	}
	
	public static boolean colorPicker4(String label, Vector4f color, float columnWidth) {
		boolean res = false;
		ImGui.pushID(label);
		
		ImGui.columns(2);
		ImGui.setColumnWidth(0, columnWidth);
		ImGui.text(label);
		ImGui.nextColumn(); 
		
		float[] ImColor = { color.x, color.y, color.z, color.w };
		if (ImGui.colorEdit4("##colorPicker", ImColor)) {
			color.set(ImColor[0], ImColor[1], ImColor[2], ImColor[3]);
			res = true;
		}
		
		// Reset Columns for other ImGui widgets that follow
		ImGui.columns(1); 
		ImGui.popID();
		
		return res;
	}
	
	public static String inputText(String label, String text) {
		ImGui.pushID(label);
		
		ImGui.columns(2);
		ImGui.setColumnWidth(0, DEFAULT_COLUMN_WIDTH);
		ImGui.text(label);
		ImGui.nextColumn(); 
		
		ImString outString = new ImString(text, 256);
		if (ImGui.inputText("##" + label, outString)) {
			ImGui.columns(1);
			ImGui.popID();
			
			return outString.get();
		}
		
		// Reset Columns for other ImGui widgets that follow
		ImGui.columns(1); 
		ImGui.popID();
		
		return text;
	}
}
