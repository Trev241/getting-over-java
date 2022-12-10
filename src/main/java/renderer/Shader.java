package renderer;

import static org.lwjgl.opengl.GL30.*;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

public class Shader {
	private int shaderProgramID;
	private boolean beingUsed;
	
	private String vertexSrc; 
	private String fragmentSrc;
	private String filepath;
	
	public Shader(String filepath) {
		this.filepath = filepath;
		String src = null;
		
		try {
			src = new String(Files.readAllBytes(Paths.get(filepath)));
		} catch (IOException e) {
			e.printStackTrace();
			assert false : "ERROR: Could not open file for shader: '" + filepath + "'" ;
		}

		String[] splitString = src.split("(#type)( )+([a-zA-Z]+)");
		if (splitString.length < 2)
			assert false : "ERROR: Shader '" + filepath + "' is not a valid shader";
		
		String[] shaderType = new String[splitString.length - 1];
		int count = 1;
		int startPos = 0;
		int endPos = 0;
		
		while (count < splitString.length) {
			startPos = src.indexOf("#type", endPos) + 6;		// Begin
			endPos = src.indexOf("\r\n", startPos); 			// End
			
			// Extracting name of the type of shader
			shaderType[count - 1] = src.substring(startPos, endPos).trim();
			
			switch (shaderType[count - 1]) {
			case "vertex":
				vertexSrc = splitString[count];
//				System.out.println("Vertex Source: " + vertexSrc);
				break;
			case "fragment":
				fragmentSrc = splitString[count];
//				System.out.println("Fragment Source: " + fragmentSrc);
				break;
			default:
				assert false : "ERROR: Shader '" + filepath + "' has invalid types";
			}
			
			++count;
		}
	}
	
	public void compile() {
		int vertexID;
		int fragmentID;
		
		// Load and compile vertex shader
		vertexID = glCreateShader(GL_VERTEX_SHADER);
		// Pass shader src to GPU
		glShaderSource(vertexID, vertexSrc);
		glCompileShader(vertexID);
		
		// Check for errors in compilation process
		int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
		if (success == GL_FALSE) {
			int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
			System.out.println("ERROR: 'defaultShader.glsl'\n\tVertex shader compilation failed.");
			System.out.println(glGetShaderInfoLog(vertexID, len));
			assert false : "";
		}
		
		// Load and compile vertex shader
		fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
		// Pass shader src to GPU
		glShaderSource(fragmentID, fragmentSrc);
		glCompileShader(fragmentID);
				
		// Check for errors in compilation process
		success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
		if (success == GL_FALSE) {
			int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
			System.out.println("ERROR: '" + filepath + "'\n\tFragment shader compilation failed.");
			System.out.println(glGetShaderInfoLog(fragmentID, len));
			assert false : "";
		}	
		
		// Link shaders and check for errors
		shaderProgramID = glCreateProgram();
		glAttachShader(shaderProgramID, vertexID);
		glAttachShader(shaderProgramID, fragmentID);
		glLinkProgram(shaderProgramID);
		
		success = glGetProgrami(shaderProgramID, GL_LINK_STATUS);
		if (success == GL_FALSE) {
			int len = glGetProgrami(shaderProgramID, GL_INFO_LOG_LENGTH);
			System.out.println("ERROR: '" + filepath + "'\n\tLinking shaders failed.");
			System.out.println(glGetProgramInfoLog(shaderProgramID, len));
			assert false : "";
		}
	}
	
	public void use() {
		if (!beingUsed) {
			// Bind shader program
			glUseProgram(shaderProgramID);
			beingUsed = true;
		}
	}
	
	public void detach() {
		glUseProgram(0);
		beingUsed = false;
	}
	
	public void uploadMat4f(String varName, Matrix4f mat4) {
		int varLocation = glGetUniformLocation(shaderProgramID, varName);
		use();
		FloatBuffer matBuffer = BufferUtils.createFloatBuffer(16);
		mat4.get(matBuffer);
		
		glUniformMatrix4fv(varLocation, false, matBuffer);
	}
	
	public void uploadMat3f(String varName, Matrix3f mat3) {
		int varLocation = glGetUniformLocation(shaderProgramID, varName);
		use();
		FloatBuffer matBuffer = BufferUtils.createFloatBuffer(9);
		mat3.get(matBuffer);
		
		glUniformMatrix3fv(varLocation, false, matBuffer);
	}
	
	public void uploadVec4f(String varName, Vector4f vec) {
		int varLocation = glGetUniformLocation(shaderProgramID, varName);
		use();
		glUniform4f(varLocation, vec.x, vec.y, vec.z, vec.w);
	}
	
	public void uploadVec3f(String varName, Vector3f vec) {
		int varLocation = glGetUniformLocation(shaderProgramID, varName);
		use();
		glUniform3f(varLocation, vec.x, vec.y, vec.z);
	}
	
	public void uploadVec2f(String varName, Vector2f vec) {
		int varLocation = glGetUniformLocation(shaderProgramID, varName);
		use();
		glUniform2f(varLocation, vec.x, vec.y);
	}
	
	public void uploadFloat(String varName, float val) {
		int varLocation = glGetUniformLocation(shaderProgramID, varName);
		use();
		glUniform1f(varLocation, val);
	}
	
	public void uploadInt(String varName, int val) {
		int varLocation = glGetUniformLocation(shaderProgramID, varName);
		use();
		glUniform1i(varLocation, val);
	}
	
	public void uploadTexture(String varName, int slot) {
		int varLocation = glGetUniformLocation(shaderProgramID, varName);
		use();
		glUniform1i(varLocation, slot);
	}
	
	public void uploadIntArray(String varName, int[] arr) {
		int varLocation = glGetUniformLocation(shaderProgramID, varName);
		use();
		glUniform1iv(varLocation, arr);
	}
}
