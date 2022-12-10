package jindow;

import static org.lwjgl.glfw.GLFW.*;

import java.util.Arrays;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class MouseListener {
	private static MouseListener instance;
	private boolean mouseButtonsPressed[] = new boolean[GLFW_MOUSE_BUTTON_LAST];
	
	private double scrollX;
	private double scrollY;
	private double xPos;
	private double yPos;
	private double worldX;
	private double worldY;
	private double lastX;
	private double lastY;
	private double lastWorldX;
	private double lastWorldY;
		
	private int mouseButtonsDown = 0;
	
	private boolean isDragging;
	
	private Vector2f gameViewportPos = new Vector2f();
	private Vector2f gameViewportSize = new Vector2f();
	
	private MouseListener() {
		this.scrollX = 0.0;
		this.scrollX = 0.0;

		this.xPos = 0.0;
		this.yPos = 0.0;
		
		this.lastX = 0.0;
		this.lastY = 0.0; 
	}
	
	public static void endFrame() {
		get().scrollY = 0.0;
		get().scrollX = 0.0;
		
		get().lastX = get().xPos;
		get().lastY = get().yPos;
		
		get().lastWorldX = get().worldX;
		get().lastWorldY = get().worldY;
	}
	
	public static void clear() {
		get().scrollX = 0.0;
		get().scrollY = 0.0;
		
		get().xPos = 0.0;
		get().yPos = 0.0;
		
		get().lastX = 0.0;
		get().lastY = 0.0; 
		
		get().mouseButtonsDown = 0;
		get().isDragging = false;
		Arrays.fill(get().mouseButtonsPressed, false);
	}
	
	public static MouseListener get() {
		if (MouseListener.instance == null)
			MouseListener.instance = new MouseListener();
		return MouseListener.instance;
	}
	
	public static void mousePosCallback(long window, double xPos, double yPos) {
		if (!Window.getImGuiLayer().getGameViewWindow().getWantCaptureMouse()) {
			clear();	
		}
		
		if (get().mouseButtonsDown > 0) {
			get().isDragging = true;
		}
		
		// Save coordinates of last frame	
		get().lastX = get().xPos;
		get().lastY = get().yPos;
		// Save world coordinates of last frame
		// We must do this here, as converting between screen and world is expensive		
		get().lastWorldX = get().worldX;
		get().lastWorldY = get().worldY;
		
		// Set xPos and yPos to new location
		get().xPos = xPos;
		get().yPos = yPos;
		
		Vector2f worldPos = getWorld();
		get().worldX = worldPos.x;
		get().worldY = worldPos.y;
		
//		System.out.println("Last\n" + get().lastX + " " + get().lastY + "\nCurrent\n" + get().xPos + " " + get().yPos);
	}
	
	public static void mouseButtonCallback(long window, int button, int action, int modifiers) {
		if (action == GLFW_PRESS) {
			get().mouseButtonsDown++;
			
			if (button < get().mouseButtonsPressed.length)
				get().mouseButtonsPressed[button] = true;
		} else if (action == GLFW_RELEASE) {
			get().mouseButtonsDown--;
			
			if (button < get().mouseButtonsPressed.length) {
				get().mouseButtonsPressed[button] = false;
				get().isDragging = false;
			}
		}
	}
	
	public static void mouseScrollCallback(long window, double xOffset, double yOffset) {
		get().scrollX = xOffset;
		get().scrollY = yOffset;
	}
	
	public static float getX() {
//		System.out.println(MouseListener.class.descriptorString() + " x = " + get().xPos);
		return (float) get().xPos;
	}
	
	public static float getY() {
//		System.out.println(MouseListener.class.descriptorString() + " y = " + get().yPos);
		return (float) get().yPos;
	}
	
	public static float getDx() {
		// If lastX happens to be the same as the current position, this would imply that the mouse
		// has stopped moving.
		// Suppose that the mouse has not moved for one frame, this would cause lastX to be set to
		// xPos while xPos remains the same (since there was no new movement registered). As a resut,
		// both values will be equal and hence we can assume dX is 0
		return (float) (get().xPos - get().lastX);
	}

	public static float getDy() {
		return (float) (get().yPos - get().lastY);
	}
	
	public static float getWorldDx() {
		return (float) (get().worldX - get().lastWorldX);
			
	}
	
	public static float getWorldDy() {
		return (float) (get().worldY - get().lastWorldY);
	}
	
	public static float getScrollX() {
		return (float) get().scrollX;
	}
	
	public static float getScrollY() {
		return (float) get().scrollY;
	}
	
	public static boolean isDragging() {
		return get().isDragging;
	}
	
	public static boolean mouseButtonDown(int button) {
		if (button < get().mouseButtonsPressed.length)
			return get().mouseButtonsPressed[button];
		else 
			return false;
	}	

	public static float getScreenX() {
		return getScreen().x;
	}
	
	public static float getScreenY() {
		return getScreen().y;
	}
	
	public static float getWorldX() {
		return getWorld().x;
	}
	
	public static float getWorldY() {
		return getWorld().y;
	}
	
	public static Vector2f getWorld() {
		// Normalized Device Coordinates are defined between -1.0 to +1.0.
		// In order to convert screen coordinates into NDC (Normalized Device Coordinates), 
		// we must:
		//	1. Convert screen coordinates into a normalized form that ranges between
		//	   0 to 1. This can be done by dividing the current x position
		// 	   by the window's width or height respectively
		//	2. Multiply the result into 2. This is so that we can produce a larger range
		//	   i.e. 0 to 2
		// 	3. Since the NDC's range lies between -1.0 to +1.0, we must offset our result 
		// 	   accordingly to correctly match it. For this, we subtract 1
		//	In summary,
		//		WCoords = (ScreenCoords / WindowDimensions) * 2 - 1;		
		float currentX = getX() - get().gameViewportPos.x;
		currentX = (2.0f * (currentX / get().gameViewportSize.x)) - 1.0f;
//		System.out.println(MouseListener.class.descriptorString() + " x = " + currentX);
//		System.out.println("We are using width" + Window.getWidth());
		float currentY = getY() - get().gameViewportPos.y;
		currentY = (2.0f * (1.0f - (currentY / get().gameViewportSize.y))) - 1.0f;

		Camera camera = Window.getScene().camera();
		Vector4f temp = new Vector4f(currentX, currentY, 0, 1);
		Matrix4f inverseView = new Matrix4f(camera.getInverseView());
		Matrix4f inverseProjection = new Matrix4f(camera.getInverseProjection());
		temp.mul(inverseView.mul(inverseProjection)); 

		return new Vector2f(temp.x, temp.y);
	}
	
	public static Vector2f screenToWorld(Vector2f screenCoords) {
		Vector2f normalizedScreenCoords = new Vector2f(
				screenCoords.x / Window.getWidth(),
				screenCoords.y / Window.getHeight()
		);
		normalizedScreenCoords.mul(2.0f).sub(new Vector2f(1.0f, 1.0f));
		Camera camera = Window.getScene().camera();
		Vector4f temp = new Vector4f(normalizedScreenCoords.x, normalizedScreenCoords.y, 0, 1);
		Matrix4f inverseView = new Matrix4f(camera.getInverseView());
		Matrix4f inverseProjection = new Matrix4f(camera.getInverseProjection());
		temp.mul(inverseView.mul(inverseProjection));
		
		return new Vector2f(temp.x, temp.y);
	}
	
	public static Vector2f worldToScreen(Vector2f worldCoords) {
		Camera camera = Window.getScene().camera();
		Vector4f ndcSpacePos = new Vector4f(worldCoords.x, worldCoords.y, 0, 1);
		Matrix4f view = new Matrix4f(camera.getViewMatrix());
		Matrix4f projection = new Matrix4f(camera.getProjectionMatrix());
		ndcSpacePos.mul(projection.mul(view));
		Vector2f windowSpace = new Vector2f(ndcSpacePos.x, ndcSpacePos.y).mul(1.0f / ndcSpacePos.w);
		windowSpace.add(new Vector2f(1.0f, 1.0f)).mul(0.5f);
		windowSpace.mul(new Vector2f(Window.getWidth(), Window.getHeight()));
		
		return windowSpace;
	}
	
	public static Vector2f getScreen() {
		float currentX = getX() - get().gameViewportPos.x;
		currentX = (currentX / (float) get().gameViewportSize.x) * 1920.0f;
		
		float currentY = getY() - get().gameViewportPos.y;
		currentY = (1.0f - (currentY / get().gameViewportSize.y)) * 1080.0f;
		
//		System.out.println(MouseListener.class.descriptorString() + " screenX = " + currentX + " screenY = " + currentY);
		
		return new Vector2f(currentX, currentY);
	}

	public static void setGameViewportPos(Vector2f gameViewportPos) {
		get().gameViewportPos.set(gameViewportPos);
	}

	public static void setGameViewportSize(Vector2f gameViewportSize) {
		get().gameViewportSize.set(gameViewportSize);
	}
}
