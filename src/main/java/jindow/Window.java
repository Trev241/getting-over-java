package jindow;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_MAXIMIZED;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwMaximizeWindow;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.openal.ALC10.ALC_DEFAULT_DEVICE_SPECIFIER;
import static org.lwjgl.openal.ALC10.alcCloseDevice;
import static org.lwjgl.openal.ALC10.alcCreateContext;
import static org.lwjgl.openal.ALC10.alcDestroyContext;
import static org.lwjgl.openal.ALC10.alcGetString;
import static org.lwjgl.openal.ALC10.alcMakeContextCurrent;
import static org.lwjgl.openal.ALC10.alcOpenDevice;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.system.MemoryUtil.NULL;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.opengl.GL;

import observers.EventSystem;
import observers.Observer;
import observers.events.Event;
import physics2d.Physics2D;
import renderer.DebugDraw;
import renderer.FrameBuffer;
import renderer.PickingTexture;
import renderer.Renderer;
import renderer.Shader;
import scenes.LevelEditorSceneInitializer;
import scenes.Scene;
import scenes.SceneInitializer;
import util.AssetPool;

public class Window implements Observer {
	private int width;
	private int height;
	
	private long glfwWindow;
	private long audioContext;
	private long audioDevice;
	
	private boolean runtimePlaying;
	
	private String title;
	private ImGuiLayer imGuiLayer;
	private FrameBuffer framebuffer;
	private PickingTexture pickingTexture;
	
	private static Window window;
	private static Scene currentScene;
	
	public Window() {
		title = "Getting Over Java";
		width = 1280;
		height = 720;
		
		EventSystem.addObserver(this);
	}
	
	public static void changeScene(SceneInitializer sceneInitializer) {
		if (currentScene != null) {
			currentScene.destroy();
		}
		
		getImGuiLayer().getPropertiesWindow().setActiveGameObject(null);
		currentScene = new Scene(sceneInitializer);
		currentScene.load();	
		currentScene.init();
		currentScene.start();
	}
	
	@SuppressWarnings("unused")
	private Window(String title, int width, int height) {
		this.title = title;
		this.width = width;
		this.height = height;
	}
	
	public static Window get() {
		if (Window.window == null)
			Window.window = new Window();
		return Window.window;
	}
	
	public static Physics2D getPhysics() {
		return currentScene.getPhysics();
	}
	
	
	
	public static Scene getScene() {
		return currentScene;
	}
	
	public void run() {
		System.out.println("Hello LWJGL " + Version.getVersion() + "!");
		
		init();
		loop();

		alcDestroyContext(audioContext);
		alcCloseDevice(audioDevice);
		
		glfwFreeCallbacks(glfwWindow);
		glfwDestroyWindow(glfwWindow);
		
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}
	
	public void init() {
		GLFWErrorCallback.createPrint(System.err).set();
		
		if (!glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");

		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
		glfwWindowHint(GLFW_MAXIMIZED, GLFW_FALSE);
		// BY ALL MEANS DO NOT SET THE WINDOW TO MAXIMIZED IN THE WINDOW HINT FUNCTION CALL
		// This is because the window size callbacks have not yet been specified. As a result,
		// the window will enter maximized mode without notifying the necessary stakeholders.
		// This causes a mismatch between the window's dimension values and its true dimensions.
		
		glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
		if (glfwWindow == NULL) 
			throw new IllegalStateException("Failed to create GLFW window.");
		
		glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
		glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
		glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
		glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);
		glfwSetWindowSizeCallback(glfwWindow, (w, newWidth, newHeight) -> {
//			System.out.println("Window resized");
			Window.setWidth(newWidth);
			Window.setHeight(newHeight);
		});
		// Now we can safely enter maximized mode since the callback has been defined.
		// The window will now be properly alerted when it is resized thus avoiding any
		// unexpected behaviour
		glfwMaximizeWindow(glfwWindow);
		
		glfwMakeContextCurrent(glfwWindow);
		// Enable v-sync
		glfwSwapInterval(1);
		
		glfwShowWindow(glfwWindow);
		
		// Initialize audio
		String defaultDeviceName = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
		audioDevice = alcOpenDevice(defaultDeviceName);
		
		int[] attributes = {0};
		audioContext = alcCreateContext(audioDevice, attributes);
		alcMakeContextCurrent(audioContext);
		
		ALCCapabilities alcCapabilities = ALC.createCapabilities(audioDevice);
		ALCapabilities alCapabilities = AL.createCapabilities(alcCapabilities);
		
		if (!alCapabilities.OpenAL10) {
			assert false : "ERROR [WINDOW]: Audio library not supported";
		}
		
		// This line is critical for LWJGL's implementation of GLFW's 
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();
		
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		this.framebuffer = new FrameBuffer(1920, 1080); 
		this.pickingTexture = new PickingTexture(1920, 1080);
		glViewport(0, 0, 1920, 1080);
		
		this.imGuiLayer = new ImGuiLayer(glfwWindow, this.pickingTexture);
		this.imGuiLayer.initImGui();
		
		Window.changeScene(new LevelEditorSceneInitializer());
	}

	public void loop() {
		float beginTime = (float) glfwGetTime();
		float endTime;
		float dt = -1.0f;
		
		Shader defaultShader = AssetPool.getShader("assets/shaders/default.glsl");
		Shader pickingShader = AssetPool.getShader("assets/shaders/pickingShader.glsl");
		
		while (!glfwWindowShouldClose(glfwWindow)) {
			glfwPollEvents();
			
			// Render pass 1: Render to picking texture
			glDisable(GL_BLEND);
			pickingTexture.enableWriting();
			
			glViewport(0, 0, 1920, 1080);
			glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			
			Renderer.bindShader(pickingShader);
			currentScene.render();
			
			pickingTexture.disableWriting();
			glEnable(GL_BLEND);
			
			// Render pass 2: Render actual game
			DebugDraw.beginFrame();

			this.framebuffer.bind();
			glClearColor(1, 1, 1, 1); 
			glClear(GL_COLOR_BUFFER_BIT);
			
			if (dt >= 0) {
				Renderer.bindShader(defaultShader);
				if (runtimePlaying)
					currentScene.update(dt);
				else 
					currentScene.editorUpdate(dt);
				currentScene.render();
				DebugDraw.draw();
			}
			this.framebuffer.unbind();
			
			this.imGuiLayer.update(dt, currentScene);

			KeyListener.endFrame();
			MouseListener.endFrame();
			glfwSwapBuffers(glfwWindow);
			
			endTime = (float) glfwGetTime();
			dt = endTime - beginTime;
			beginTime = endTime;
		}
	}

	public static int getWidth() {
		return 1920; //get().width;
	}
	
	public static int getHeight() {
		return 1080; //get().height;
	}

	public static void setWidth(int newWidth) {
		get().width = newWidth;
//		System.out.println("[WINDOW]: Width is " + get().width);
	}
	
	public static void setHeight(int newHeight) {
		get().height = newHeight;
//		System.out.println("[WINDOW]: Height is " + get().height);
	}

	public static FrameBuffer getFramebuffer() {
		return get().framebuffer;
	}
	
	public static float getTargetAspectRatio() {
		return 16.0f / 9.0f;
	}

	public static ImGuiLayer getImGuiLayer() {
		return get().imGuiLayer;
	}
	
	public static boolean isRuntimePlaying() {
		return get().runtimePlaying;
	}

	@Override
	public void onNotify(GameObject object, Event event) {
		switch (event.type) {
		case GameEngineStartPlay:
			System.out.println("Starting play");
			currentScene.save();
			Window.changeScene(new LevelEditorSceneInitializer());
			this.runtimePlaying = true;
			break;
		case GameEngineStopPlay:
			System.out.println("Stopping play");
			Window.changeScene(new LevelEditorSceneInitializer());
			this.runtimePlaying = false;
			break;
		case LoadLevel:
			Window.changeScene(new LevelEditorSceneInitializer());
			break;
		case SaveLevel:
			currentScene.save();
		default:
			System.out.println("[WINDOW]: Received unrecognized event. Ignoring...");
		
		}
	}

}
