package editor;

import org.joml.Vector2f;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;
import jindow.MouseListener;
import jindow.Window;
import observers.EventSystem;
import observers.events.Event;
import observers.events.EventType;

public class GameViewWindow {
	
	private float leftX;
	private float rightX;
	private float topY;
	private float bottomY;
	
	private boolean isPlaying;
	
	public void imgui() {
		ImGui.begin("Game Viewport", ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse
				| ImGuiWindowFlags.MenuBar);
		
		ImGui.beginMenuBar();
		if (ImGui.menuItem("Play", "", isPlaying, !isPlaying)) {
			isPlaying = true;
			EventSystem.notify(null, new Event(EventType.GameEngineStartPlay));
		}
		if (ImGui.menuItem("Stop", "", !isPlaying, isPlaying)) {
			isPlaying = false;
			EventSystem.notify(null, new Event(EventType.GameEngineStopPlay));
		}
		ImGui.endMenuBar();
		
		ImGui.setCursorPos(ImGui.getCursorPosX(), ImGui.getCursorPosY());
		ImVec2 windowSize = getLargestSizeForViewport();
		ImVec2 windowPos = getCenteredPositionForViewport(windowSize);
//		System.out.println(this.getClass().descriptorString() + " windowPosX = " + windowPos.x + " windowPosY = " + windowPos.y);
		ImGui.setCursorPos(windowPos.x, windowPos.y);
		
//		System.out.println(this.getClass().descriptorString() + " sizeX = " + windowSize.x + " sizeY = " + windowSize.y);
		
		leftX = windowPos.x;
		rightX = windowPos.x + windowSize.x;
		bottomY = windowPos.y;
		topY = windowPos.y + windowSize.y;
		
//		System.out.println(GameViewWindow.class.descriptorString() + " " + leftX + " " + rightX + " " + topY + " " + bottomY);
		
		int textureID = Window.getFramebuffer().getTextureID();
		ImGui.image(textureID, windowSize.x, windowSize.y, 0, 1, 1, 0);
	
		MouseListener.setGameViewportPos(new Vector2f(windowPos.x, windowPos.y));
		MouseListener.setGameViewportSize(new Vector2f(windowSize.x, windowSize.y));
		
		ImGui.end();
	}
	
	private static ImVec2 getLargestSizeForViewport() {
		ImVec2 windowSize = new ImVec2();
		ImGui.getContentRegionAvail(windowSize); 
		
//		System.out.println(GameViewWindow.class.descriptorString() + " windowSizeX = " + windowSize.x + " windowSizeY = " + windowSize.y);
		
		float aspectWidth = windowSize.x;
		float aspectHeight = aspectWidth / Window.getTargetAspectRatio();
		if (aspectHeight > windowSize.y) {
			// Switch to pillar box mode
			aspectHeight = windowSize.y;
			aspectWidth = aspectHeight * Window.getTargetAspectRatio();
		}
		
//		System.out.println(GameViewWindow.class.descriptorString() + " finalWindowSizeX =" + aspectWidth + " finalWindowSizeY = " + aspectHeight);
		
		return new ImVec2(aspectWidth, aspectHeight);
	}
	
	private ImVec2 getCenteredPositionForViewport(ImVec2 aspectSize) {
		ImVec2 windowSize = new ImVec2();
		ImGui.getContentRegionAvail(windowSize);
		
//		System.out.println(this.getClass().descriptorString() + " availWindowSizeX = " + windowSize.x + " availWindowSizeY = " + windowSize.y);
//		System.out.println(this.getClass().descriptorString() + " actualWindowSizeX = " + aspectSize.x + " actualWindowSizeY = " + aspectSize.y);
		
		float viewportX = (windowSize.x / 2.0f) - (aspectSize.x /  2.0f);
		float viewportY = (windowSize.y / 2.0f) - (aspectSize.y /  2.0f);
		
//		System.out.println(this.getClass().descriptorString() + " ImGuiCursorX = " + ImGui.getCursorPosX() + " ImGuiCursorY = " + ImGui.getCursorPosY());
		
		return new ImVec2(viewportX + ImGui.getCursorPosX(),
				viewportY + ImGui.getCursorPosY());
	}

	public boolean getWantCaptureMouse() {
//		System.out.println("X: " + MouseListener.getX() + ""
//				+ "\nY: " + MouseListener.getY());
//		System.out.println("Left X: " + leftX + ""
//				+ "\nRight X: " + rightX + ""
//						+ "\nBottom Y: " + bottomY + ""
//								+ "\nTop Y: " + topY);
		return MouseListener.getX() >= leftX && MouseListener.getX() <= rightX 
				&& MouseListener.getY() >= bottomY && MouseListener.getY() <= topY;
 
	}
	
}
