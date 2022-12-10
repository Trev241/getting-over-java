package renderer;

import static org.lwjgl.opengl.GL11.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_NONE;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDrawBuffer;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glReadBuffer;
import static org.lwjgl.opengl.GL11.glReadPixels;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_DEPTH_ATTACHMENT;
import static org.lwjgl.opengl.GL30.GL_DRAW_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_COMPLETE;
import static org.lwjgl.opengl.GL30.GL_READ_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_RGB32F;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.opengl.GL30.glCheckFramebufferStatus;
import static org.lwjgl.opengl.GL30.glFramebufferTexture2D;
import static org.lwjgl.opengl.GL30.glGenFramebuffers;

import org.joml.Vector2i;

public class PickingTexture {
	private int pickingTextureID;
	private int fboID;
	private int depthTexture;
	
	public PickingTexture(int width, int height) {
		if (!init(width, height)) {
			assert false : "ERROR: [PICKING TEXTURE] Could not initialize picking texture";
		}
	}
	
	public boolean init(int width, int height) {
		// Generate framebuffer
		fboID = glGenFramebuffers();
		glBindFramebuffer(GL_FRAMEBUFFER, fboID);
		
		// Create texture to render data to and attach to framebuffer
		pickingTextureID = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, pickingTextureID);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB32F, width, height, 0, GL_RGB, GL_FLOAT, 0);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, 
				this.pickingTextureID, 0);
		
		// Create texture object for depth buffer
		glEnable(GL_DEPTH_ATTACHMENT);
		depthTexture = glGenTextures();
		glBindTexture(GL_DEPTH_ATTACHMENT, depthTexture);
		glTexImage2D(GL_DEPTH_ATTACHMENT, 0, GL_DEPTH_COMPONENT, width, height, 0, GL_DEPTH_COMPONENT, GL_FLOAT, 0);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthTexture, 0);

		// Disbale reading
		glReadBuffer(GL_NONE);
		glDrawBuffer(GL_COLOR_ATTACHMENT0);
		
		
		if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
			assert false : "ERROR: [PICKING TEXTURE] Frame buffer is not complete!";
			return false;
		}
		
		// Unbind the texture and framebuffer
		glBindTexture(GL_TEXTURE_2D, 0);
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		
		return true;
	}
	
	public void enableWriting() {
		glBindFramebuffer(GL_DRAW_FRAMEBUFFER, fboID);
	}
	
	public void disableWriting() {
		glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);	
	}
	
	public int readPixel(int x, int y) {
		glBindFramebuffer(GL_READ_FRAMEBUFFER, fboID);
		glReadBuffer(GL_COLOR_ATTACHMENT0);
		
		float[] pixels = new float[3];
		glReadPixels(x, y, 1, 1, GL_RGB, GL_FLOAT, pixels);
		
		return (int) pixels[0] - 1;
	}
	
	public float[] readPixels(Vector2i start, Vector2i end) {
		glBindFramebuffer(GL_READ_FRAMEBUFFER, fboID);
		glReadBuffer(GL_COLOR_ATTACHMENT0);
		
		Vector2i size = new Vector2i(end).sub(start).absolute();
		int numPixels = size.x * size.y;
		float[] pixels = new float[3 * numPixels];
		glReadPixels(start.x, start.y, size.x, size.y, GL_RGB, GL_FLOAT, pixels);
		for (int i = 0; i < pixels.length; i++) {
			pixels[i] -= 1;
		}
		
		return pixels;
	}
}
