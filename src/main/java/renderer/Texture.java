package renderer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

public class Texture {
	private String filepath; 
	
	private transient int id;
	private int width;
	private int height;
	
	public Texture() {
		id = -1;
		width = -1;
		height = -1;
	}
	
	public Texture(int width, int height) {
		this.filepath = "Generated";
		
		// Generate texture on GPU
		id = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, id);
		
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 
				0, GL_RGBA, GL_UNSIGNED_BYTE, 0);
	}

	public void init(String filepath) {
		this.filepath = filepath;
		
		// Generate texture on GPU
		id = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, id);
		
		// Set texture parameters
		// Repeate image in both directions if there is extra space designated on the UV
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		// When stretching the image, we want pixelation
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		// When shrinking the image, we want pixelation
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		
		IntBuffer width = BufferUtils.createIntBuffer(1);
		IntBuffer height = BufferUtils.createIntBuffer(1);
		IntBuffer channels = BufferUtils.createIntBuffer(1);
		stbi_set_flip_vertically_on_load(true);
		ByteBuffer image = stbi_load(filepath, width, height, channels, 0);
		
		if (image != null) {
			this.width = width.get(0);
			this.height = height.get(0);
			
			int internalFormat = GL_RGBA;
			if (channels.get(0) == 3) 
				internalFormat = GL_RGB;
			else if (channels.get(0) == 4)
				internalFormat = GL_RGBA;
			else 
				assert false : "ERROR: [TEXTURE] Unknown number of channels '" + filepath + "'";
			
			glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, width.get(0), height.get(0), 
					0, internalFormat, GL_UNSIGNED_BYTE, image);
		} else {
			assert false : "ERROR: [TEXTURE] Could not load image '" + filepath + "'";
		}
		
		stbi_image_free(image); 		
	}
	
	public void bind() {
		glBindTexture(GL_TEXTURE_2D, id);
	}
	
	public void unbind() {
		glBindTexture(GL_TEXTURE_2D, 0);
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public String getFilepath() {
		return this.filepath;
	}

	public int getID() {
		return id;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof Texture)) return false;
		
		Texture oTex = (Texture) o;
		return oTex.getWidth() == this.width && oTex.getHeight() == this.height &&
				oTex.getID() == this.id && oTex.getFilepath().equals(this.filepath);
	}
}
