package scenes;

import java.io.File;
import java.util.Collection;

import org.joml.Vector2f;

import components.EditorCamera;
import components.GizmoSystem;
import components.GridLines;
import components.KeyControls;
import components.MouseControls;
import components.Sprite;
import components.SpriteRenderer;
import components.SpriteSheet;
import imgui.ImGui;
import imgui.ImVec2;
import jindow.GameObject;
import jindow.Prefabs;
import jindow.Sound;
import jindow.Window;
import renderer.Texture;
import util.AssetPool;

public class LevelEditorSceneInitializer extends SceneInitializer {
//	private int vertexID, fragmentID, shaderProgram;
//	
//	private Shader defaultShader;
//	private Texture testTexture;
//	
//	private GameObject testObject;
//	
//	private float[] vertexArray = {
//		// Format as
//		// position (in normalized device coordinates)			color						UV Coords.
//		100.0f,	0.0f,	0.0f,									1.0f, 0.0f, 0.0f, 1.0f,		1, 1,	// Bottom right
//		0.0f,	100.0f,	0.0f,									0.0f, 1.0f, 0.0f, 1.0f,		0, 0,	// Top left
//		100.0f,	100.0f,	0.0f,									0.0f, 0.0f, 1.0f, 1.0f, 	1, 0,	// Top right
//		0.0f,	0.0f,	0.0f,									1.0f, 1.0f, 0.0f, 1.0f,		0, 1	// Bottom left
//	};
//	
//	// Warning! Must be in counter-clockwise order!
//	private int[] elementArray = {
//		/*
//		 * 		1			2
//		 * 
//		 * 
//		 * 
//		 * 		3			0
//		 * 
//		 * Suppose the numbers above represent the four vertices,
//		 * then the triangle must be constructed in COUNTER-CLOCKWISE order
//		 * i.e. 0 -> 2 -> 1 and 0 -> 1 -> 3
//		 */
//			0, 2, 1,
//			0, 1, 3
//	};
//	
//	private int vaoID, vboID, eboID;
//	// Respectively, 
//	// Vertex Array Object, Vertex Buffer Object and Element Buffer Object
//	
	private SpriteSheet spriteSheet;
	private GameObject levelEditorStuff;
	private Scene scene;
	
	@Override
	public void init(Scene scene) {
		this.scene = scene;
		
		spriteSheet = AssetPool.getSpriteSheet("assets/images/spritesheets/decorationsAndBlocks.png");
		SpriteSheet gizmos = AssetPool.getSpriteSheet("assets/images/gizmos.png");
		
//		this.testObject = new GameObject("Test");
//		this.testObject.addComponent(new SpriteRenderer());
//		this.testObject.addComponent(new FontRenderer());
//		this.addGameObjectToScene(testObject);
//		
//		this.camera = new Camera(new Vector2f(-200, -300));
//		
//		defaultShader = new Shader("assets/shaders/default.glsl");
//		defaultShader.compile();
//		
//		this.testTexture = new Texture("assets/images/placeholder-player.png");
//		
//		// Generating VAO, VBO and EBO. Send to GPU
//		vaoID = glGenVertexArrays();
//		glBindVertexArray(vaoID);	// By binding the vertex array, we ensure that the following code will modify only the VAO
//		
//		// Create float buffer of vertices
//		FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
//		vertexBuffer.put(vertexArray).flip();
//		
//		// Create VBO and upload the vertex buffer
//		vboID = glGenBuffers();
//		glBindBuffer(GL_ARRAY_BUFFER, vboID);
//		glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);	// upload vertexBuffer to the vboID
//		
//		// Create the indices and upload
//		IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
//		elementBuffer.put(elementArray).flip();
//		
//		eboID = glGenBuffers();
//		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
//		glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);
//		
//		// Add the vertex attribute pointers
//		int positionsSize = 3;		// (3 coordinates)
//		int colorSize = 4;			// (4 color values - R, G, B, A)
//		int uvSize = 2;			
//		int vertexSizeByBytes = (positionsSize + colorSize + uvSize) * Float.BYTES;	// Total size of each vertex
//		
//		// Offset is given as 0 because position values are at the beginning of the vertex itself 
//		glVertexAttribPointer(0, positionsSize, GL_FLOAT, false, vertexSizeByBytes, 0);
//		glEnableVertexAttribArray(0);	// Enable the index
//		
//		// Offset is given positionsSize because color values begin after 3 float locations
//		glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeByBytes, positionsSize * Float.BYTES); 
//		glEnableVertexAttribArray(1);	// Enable the index
//		
//		glVertexAttribPointer(2, uvSize, GL_FLOAT, false, vertexSizeByBytes, (positionsSize + colorSize) * Float.BYTES);
//		glEnableVertexAttribArray(2);

		levelEditorStuff = scene.createGameObject("Level Editor");
		levelEditorStuff.setSerializable(false);
		levelEditorStuff.addComponent(new MouseControls());
		levelEditorStuff.addComponent(new GridLines());
		levelEditorStuff.addComponent(new EditorCamera(scene.camera()));
		levelEditorStuff.addComponent(new GizmoSystem(gizmos));
		levelEditorStuff.addComponent(new KeyControls());
		
		scene.addGameObjectToScene(levelEditorStuff);
	}
	
	@Override
	public void loadResources(Scene scene) {
		// Load shader
		AssetPool.getShader("assets/shaders/default.glsl");
		
		// Load sprites
		AssetPool.addSpriteSheet("assets/images/spritesheets/decorationsAndBlocks.png", 
				new SpriteSheet(AssetPool.getTexture("assets/images/spritesheets/decorationsAndBlocks.png"
						), 16, 16, 81, 0));
		AssetPool.addSpriteSheet("assets/images/gizmos.png", new SpriteSheet(AssetPool.getTexture("assets/images/gizmos.png"), 24, 48, 3, 0));
		for (GameObject gameObject : scene.getGameObjects()) {
			if (gameObject.getComponent(SpriteRenderer.class) != null) {
				SpriteRenderer spr = gameObject.getComponent(SpriteRenderer.class);
				
				// Retrieve the texture object if there happens to be any created during
				// the deserialization process. Reset the sprite's texture to a reference 
				// of the one currently held by the AssetPool using the filepath. This ensures the
				// presence of only ONE instance/copy of the texture.
				if (spr.getTexture() != null) {
					spr.setTexture(AssetPool.getTexture(spr.getTexture().getFilepath()));
				}
			}
		}
		AssetPool.getTextures("assets/images");
		
		// Load sounds
		AssetPool.addSounds("assets/sounds");
	}

//	float t = 0.0f;
//	float angle = 0.0f;
//	
//	float x = 0.0f;
//	float y = 0.0f;
	
//	TODO: Useful math for later
//	float x = ((float) Math.sin(t) * 200.0f) + 600;
//	float y = ((float) Math.cos(t) * 200.0f) + 400;
//	t += .05f;
//	DebugDraw.addLine2D(new Vector2f(600, 400), new Vector2f(x, y), new Vector3f(0, 0, 1), 1);

// 	Delta time represents the time taken between drawing two frames. 
// 	Hence, it can be implied that delta time represents the number of seconds
// 	taken to render per frame or symbolically speaking s f^-1 or s/f.
// 	Drawing from the earlier relation, we can represent FPS (frames per second) by
// 	reciprocating the delta time.
// 	Therefore,
//		f/s = 1/dt
//	System.out.println("FPS: " + (1.0f / dt));	
	
	@Override
	public void imgui() {
		if (Window.isRuntimePlaying()) return;
		
//		System.out.println("x: " + MouseListener.getScreenX() + ""
//				+ "\ny: " + MouseListener.getScreenY());
		
		ImGui.begin("Level Editor Stuff");
		levelEditorStuff.imgui();
		ImGui.end();
		
		ImGui.begin("Test Window");
		
		if (ImGui.beginTabBar("WindowTabBar")) {
			if (ImGui.beginTabItem("Solid Blocks")) {
				ImVec2 windowPos = new ImVec2();
				ImGui.getWindowPos(windowPos);
				ImVec2 windowSize = new ImVec2();
				ImGui.getWindowSize(windowSize);
				ImVec2 itemSpacing = new ImVec2();
				ImGui.getStyle().getItemSpacing(itemSpacing);
				
				// Rightmost window coordinate
				float windowX2 = windowPos.x + windowSize.x;
				for (int i = 0; i < spriteSheet.size(); i++) {
					Sprite sprite = spriteSheet.getSprite(i);
					float spriteWidth = sprite.getWidth() * 4;
					float spriteHeight = sprite.getHeight() * 4;
					int id = sprite.getTextureID();
					Vector2f[] texCoords = sprite.getTexCoords();
					
					ImGui.pushID(i);
					if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
						GameObject object = Prefabs.generateSpriteObject(sprite, .25f, .25f);
						levelEditorStuff.getComponent(MouseControls.class).pickupObject(object);
					}
					ImGui.popID();
					
					ImVec2 lastButtonPos = new ImVec2();
					ImGui.getItemRectMax(lastButtonPos);
					float lastButtonX2 = lastButtonPos.x;
					float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth;
					if (i + 1 < spriteSheet.size() && nextButtonX2 < windowX2)
						ImGui.sameLine();
				}
				
				ImGui.endTabItem();
			}
			
			if (ImGui.beginTabItem("Prefabs")) {
				if (ImGui.button("Player")) {
					float playerSizeX = .5f;
					float playerSizeY = .5f;
					GameObject player = Prefabs.generatePlayerObject(
							new Sprite().setTexture(AssetPool.getTexture("assets/images/placeholder-player.png")),
							playerSizeX, playerSizeY
					);
					GameObject hammer = Prefabs.generateHammerObject(
							player, new Sprite().setTexture(AssetPool.getTexture("assets/images/blendImage2.png")),
							playerSizeX * .3f, playerSizeX * .3f 
					);
					scene.addGameObjectToScene(hammer);
					scene.addGameObjectToScene(player);
				}
				
				for (Texture texture : AssetPool.getAllTextures()) {
					if (ImGui.button(texture.getFilepath())) {
						GameObject block = Prefabs.generateBlock(new Sprite().setTexture(texture), 1f, 1f);
						scene.addGameObjectToScene(block);
					}
				}
				
				ImGui.endTabItem();
			}
			
			if (ImGui.beginTabItem("Sounds")) {
				Collection<Sound> sounds = AssetPool.getAllSounds();
				for (Sound sound : sounds) {
					File tmp = new File(sound.getFilepath());
					if (ImGui.button(tmp.getName())) {
						if (!sound.isPlaying()) {
							sound.play();
						} else {
							sound.stop();
						}
					}
					
					if (ImGui.getContentRegionAvailX() > 100) {
						ImGui.sameLine();
					}
				}
				
				ImGui.endTabItem();
			}
			
			ImGui.endTabBar();
		}
		
		
		
		ImGui.end();
	}
}
