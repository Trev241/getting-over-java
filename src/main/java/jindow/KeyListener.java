package jindow;

import static org.lwjgl.glfw.GLFW.*;

import java.util.Arrays;

public class KeyListener {
	private static KeyListener instance;
	
	private boolean keyPressed[] = new boolean[GLFW_KEY_LAST];
	private boolean keyBeginPressed[] = new boolean[GLFW_KEY_LAST];
	
	private KeyListener() {
		
	}
	
	public static void endFrame() {
		Arrays.fill(get().keyBeginPressed, false);
	}
	
	public static KeyListener get() {
		if (KeyListener.instance == null) 
			KeyListener.instance = new KeyListener();
		return KeyListener.instance; 
	}
	
	public static void keyCallback(long window, int key, int scancode, int action, int modifiers) {
		if (key == GLFW_KEY_UNKNOWN) return;
		
//		System.out.println(key);
		
		if (action == GLFW_PRESS) {
			get().keyPressed[key] = true;
			get().keyBeginPressed[key] = true;
		} else if (action == GLFW_RELEASE) {
			get().keyPressed[key] = false;
			get().keyBeginPressed[key] = false;
		}
	}
	
	public static boolean isKeyPressed(int keyCode) {
		return get().keyPressed[keyCode];
	}

	public static boolean keyBeginPress(int keyCode) {
		return get().keyBeginPressed[keyCode];
	}
}
