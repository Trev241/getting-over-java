package renderer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glLineWidth;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glBufferSubData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.joml.Vector2f;
import org.joml.Vector3f;

import jindow.Window;
import util.AssetPool;
import util.JMath;

public class DebugDraw {
	private static int MAX_LINES = 500;
	
	private static List<Line2D> lines = new ArrayList<>();
	// 6 floats per vertex; 2 vertices per line
	private static float[] vertexArray = new float[MAX_LINES * 6 * 2];
	private static Shader shader = AssetPool.getShader("assets/shaders/debugLine2D.glsl");
	
	private static int vaoID;
	private static int vboID;
	
	private static boolean started = false;
	
	public static void start() {
		// Generate the VAO
		vaoID = glGenVertexArrays();
		glBindVertexArray(vaoID);
		
		// Create VBO and buffer some memory
		vboID = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboID);
		glBufferData(GL_ARRAY_BUFFER, vertexArray.length * Float.BYTES, GL_DYNAMIC_DRAW);
		
		// Enable the vertex attributes
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * Float.BYTES, 0);
		glEnableVertexAttribArray(0);
		
		glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
		glEnableVertexAttribArray(1);
		
		glLineWidth(2.0f);
	}
	
	public static void beginFrame() {
		if (!started) {
			start();
			started = true;
		}
		
		// Remove dead lines
		for (int i = 0; i < lines.size(); i++) {
			if (lines.get(i).beginFrame() < 0) 
				lines.remove(i--);
		}
	}
	
	public static void draw() {
		if (lines.size() <= 0) return;
		
		int index = 0;
		for (Line2D line : lines) {
			for (int i = 0; i < 2; i++) {
				Vector2f position = i == 0 ? line.getFrom() : line.getTo();
				Vector3f color = line.getColor();
			
				// Load position
				vertexArray[index] = position.x;
				vertexArray[index + 1] = position.y;
				vertexArray[index + 2] = -10.0f;
				
				// Load color
				vertexArray[index + 3] = color.x;
				vertexArray[index + 4] = color.y;
				vertexArray[index + 5] = color.z;
				
				index += 6;
			}
		}
		
		glBindBuffer(GL_ARRAY_BUFFER, vboID);
		glBufferSubData(GL_ARRAY_BUFFER, 0, Arrays.copyOfRange(vertexArray, 0, lines.size() * 6 * 2));
		
		shader.use();
		shader.uploadMat4f("uProjection", Window.getScene().camera().getProjectionMatrix());
		shader.uploadMat4f("uView", Window.getScene().camera().getViewMatrix());
		
		glBindVertexArray(vaoID);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		
		// Draw the batch
		glDrawArrays(GL_LINES, 0, lines.size() * 6 * 2);
		
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glBindVertexArray(0);
		
		shader.detach();
	}
	
	// Add Line2D
	public static void addLine2D(Vector2f from, Vector2f to) {
		addLine2D(from, to, new Vector3f(0, 1, 0), 1);
	}
	
	public static void addLine2D(Vector2f from, Vector2f to, Vector3f color) {
		addLine2D(from, to, color, 1);
	}
	
	public static void addLine2D(Vector2f from, Vector2f to, Vector3f color, int lifetime) {
		if (lines.size() >= MAX_LINES) return;
		DebugDraw.lines.add(new Line2D(from, to, color, lifetime));
	}
	
	// Add Box2D
	public static void addBox2D(Vector2f center, Vector2f dimensions, float rotation) {
		addBox2D(center, dimensions, rotation, new Vector3f(0, 1, 0), 1);
	}
	
	public static void addBox2D(Vector2f center, Vector2f dimensions, float rotation, Vector3f color) {
		addBox2D(center, dimensions, rotation, color, 1);
	}
	
	public static void addBox2D(Vector2f center, Vector2f dimensions, float rotation, Vector3f color, int lifetime) {
		// Subtract centre coordinates by half of the dimensions to obtain the bottom left vertex
		Vector2f min = new Vector2f(center).sub(new Vector2f(dimensions).div(2.0f));
		Vector2f max = new Vector2f(center).add(new Vector2f(dimensions).div(2.0f));
		
		Vector2f[] vertices = {
			new Vector2f(min.x, min.y), new Vector2f(min.x, max.y), 
			new Vector2f(max.x, max.y), new Vector2f(max.x, min.y)
		};
		
		if (rotation != 0.0f) {
			for (Vector2f vert : vertices)
				JMath.rotateInDegrees(vert, rotation, center);
		}
		
		addLine2D(vertices[0], vertices[1], color, lifetime);
		addLine2D(vertices[1], vertices[2], color, lifetime);
		addLine2D(vertices[2], vertices[3], color, lifetime);
		addLine2D(vertices[3], vertices[0], color, lifetime);
	}
	
	// Add circle
	public static void addCircle(Vector2f centre, float radius) {
		addCircle(centre, radius, new Vector3f(0, 1, 0), 1);
	}
	
	public static void addCircle(Vector2f centre, float radius, Vector3f color) {
		addCircle(centre, radius, color, 1);
	}
	
	public static void addCircle(Vector2f centre, float radius, Vector3f color, int lifetime) {
		Vector2f[] points = new Vector2f[20];
		int increment = 360 / points.length;
		int currentAngle = 0;
		
		for (int i = 0; i < points.length; i++) {
			Vector2f tmp = new Vector2f(radius, 0);
			JMath.rotateInDegrees(tmp, currentAngle, new Vector2f());
			points[i] = new Vector2f(tmp).add(centre);
			
			if (i > 0)
				addLine2D(points[i - 1], points[i], color, lifetime);
			
			currentAngle += increment;
		}
		
		addLine2D(points[points.length - 1], points[0], color, lifetime);
	}
}
