package util;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import components.SpriteSheet;
import jindow.Sound;
import renderer.Shader;
import renderer.Texture;

public class AssetPool {
	private static Map<String, Shader> shaders = new HashMap<>();
	private static Map<String, Texture> textures = new HashMap<>();
	private static Map<String, SpriteSheet> spriteSheets = new HashMap<>();
	private static Map<String, Sound> sounds = new HashMap<>();
	
	public static Shader getShader(String resourceName) {
		File file = new File(resourceName);
		if (AssetPool.shaders.containsKey(file.getAbsolutePath())) {
			return AssetPool.shaders.get(file.getAbsolutePath());
		} else {
			Shader shader = new Shader(resourceName);
			shader.compile();
			AssetPool.shaders.put(file.getAbsolutePath(), shader);
			return shader;
		}
	}
	
	public static Texture getTexture(String resourceName) {
		File file = new File(resourceName);
		if (AssetPool.textures.containsKey(file.getAbsolutePath())) {
			return AssetPool.textures.get(file.getAbsolutePath());
		} else {
			Texture texture = new Texture();
			texture.init(resourceName);
			AssetPool.textures.put(file.getAbsolutePath(), texture);
			return texture;
		}
	}
	
	public static void getTextures(String resourceName) {
		File folder = new File(resourceName);
		File[] listOfFiles = folder.listFiles();
		
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				System.out.println("[ASSET POOL]: Loading texture: " + listOfFiles[i].getPath());
				getTexture(listOfFiles[i].getPath());
			}
		}
	}
	
	public static Collection<Texture> getAllTextures() {
		return AssetPool.textures.values();
	}
	
	public static void addSpriteSheet(String resourceName, SpriteSheet spriteSheet) {
		 File file = new File(resourceName);
		 if (!AssetPool.spriteSheets.containsKey(file.getAbsolutePath())) {
			 AssetPool.spriteSheets.put(file.getAbsolutePath(), spriteSheet);
		 }
	}
	
	public static SpriteSheet getSpriteSheet(String resourceName) {
		File file = new File(resourceName);
		if (!AssetPool.spriteSheets.containsKey(file.getAbsolutePath())) {
			assert false : "ERROR: [ASSET POOL] Tried to access sprite sheet '" + resourceName + "' but it is non-existent";
		}
		return AssetPool.spriteSheets.getOrDefault(file.getAbsolutePath(), null);
	}
	
	public static Sound getSound(String soundFile) {
		File file = new File(soundFile);
		if (sounds.containsKey(file.getAbsolutePath())) {
			return sounds.get(file.getAbsolutePath());
		} else {
			assert false : "ERROR: [ASSET POOL] Tried to access sound '" + soundFile + "'";
		}
		
		return null;
	}
	
	public static void addSounds(String soundFolder) {
		File folder = new File(soundFolder);
		File[] listOfFiles = folder.listFiles();
		
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				System.out.println("[ASSET POOL]: Loading sound: " + listOfFiles[i].getPath());
				addSound(listOfFiles[i].getPath(), false);
			}
		}
	}
	
	public static Collection<Sound> getAllSounds() {
		return sounds.values();
	}
	
	public static Sound addSound(String soundFile, boolean loops) {
		File file = new File(soundFile);
		if (sounds.containsKey(file.getAbsolutePath())) {
			return sounds.get(file.getAbsolutePath());
		} else {
			Sound sound = new Sound(file.getAbsolutePath(), loops);
			AssetPool.sounds.put(file.getAbsolutePath(), sound);
			return sound;
		}
	}
}
