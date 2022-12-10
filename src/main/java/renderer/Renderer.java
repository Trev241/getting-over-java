package renderer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import components.SpriteRenderer;
import jindow.GameObject;

public class Renderer {
	private final int MAX_BATCH_SIZE = 1000;
	private List<RenderBatch> batches;
	
	private static Shader currentShader;
	
	public Renderer() {
		this.batches = new ArrayList<RenderBatch>();
	}
	
	public void add(GameObject gameObject) {
		SpriteRenderer spr = gameObject.getComponent(SpriteRenderer.class);
		if (spr != null) {
			add(spr);
		}
	}
	
	private void add(SpriteRenderer spr) {
		boolean added = false;
		for (RenderBatch batch : batches) {
			if (batch.hasRoom() && batch.zIndex() == spr.gameObject.transform.zIndex) {
				Texture texture = spr.getTexture();
				if (texture == null || (batch.hasTexture(texture) || batch.hasTextureRoom())) {
					batch.addSprite(spr);
					added = true;
					break;
				} 
			}
		}
		
		if (!added) {
			RenderBatch newBatch = new RenderBatch(MAX_BATCH_SIZE, 
					(int) spr.gameObject.transform.zIndex, this);
			newBatch.start();
			batches.add(newBatch);
			newBatch.addSprite(spr);
			
			// Sort batches by z index so that the layers are correctly displayed
			Collections.sort(batches);
		}
	}
	
	public void destroyGameObject(GameObject gameObject) {
		if (gameObject.getComponent(SpriteRenderer.class) == null) return;
		for (RenderBatch batch : batches) {
			if (batch.destroyIfExists(gameObject)) {
				return;
			}
		}
	}
	
	public static void bindShader(Shader shader) {
		currentShader = shader;
	}
	
	public static Shader getBoundShader() {
		return currentShader;
	}
	
	public void render() {
		currentShader.use();
		for (int i = 0; i < batches.size(); i++)
			batches.get(i).render();
	}

}
