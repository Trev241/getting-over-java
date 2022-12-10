package jindow;

import org.joml.Vector2f;

import components.HammerController;
import components.MouseJoint2D;
import components.PlayerController;
import components.RevoluteJoint2D;
import components.Sprite;
import components.SpriteRenderer;
import physics2d.components.Box2DCollider;
import physics2d.components.Rigidbody2D;
import physics2d.enums.BodyType;

public class Prefabs {
	public static GameObject generateSpriteObject(Sprite sprite, float sizeX, float sizeY) {
		GameObject gameObject = Window.getScene().createGameObject("Sprite_Object_Gen");
		gameObject.transform.scale.x = sizeX;
		gameObject.transform.scale.y = sizeY;
		
		SpriteRenderer spriteRenderer = new SpriteRenderer();
		spriteRenderer.setSprite(sprite);
		gameObject.addComponent(spriteRenderer);
		
		return gameObject;
	}
	
	public static GameObject generatePlayerObject(Sprite sprite, float sizeX, float sizeY) {
		GameObject player = Window.getScene().createGameObject("Player");
		player.transform.scale.x = sizeX;
		player.transform.scale.y = sizeY;
		player.transform.position = new Vector2f(0, 5);
		player.transform.zIndex = 5;
		
		SpriteRenderer spriteRenderer = new SpriteRenderer();
		spriteRenderer.setSprite(sprite);
		player.addComponent(spriteRenderer);
	
		Rigidbody2D rb = new Rigidbody2D();
		rb.setBodyType(BodyType.Dynamic);
		rb.setContinuousCollision(false);
		rb.setFixedRotation(true);
		rb.setFriction(1);
		rb.setGravityScale(1f);
		rb.setMass(100f);
		rb.setDensity(3f);
		player.addComponent(rb);
		
		Box2DCollider bc = new Box2DCollider();
		bc.setHalfSize(new Vector2f(sizeX * .6f, sizeY));
		player.addComponent(bc);
		
		player.addComponent(new PlayerController(Window.getScene().camera()));
		
		return player;
	}
	
	public static GameObject generatePlayerHeadObject(GameObject player, Sprite sprite, float sizeX, float sizeY) {
		GameObject head = Window.getScene().createGameObject("Player Head");
		head.transform.scale.x = sizeX;
		head.transform.scale.y = sizeY;
		head.transform.zIndex = player.transform.zIndex + 1;
		
		SpriteRenderer spriteRenderer = new SpriteRenderer();
		spriteRenderer.setSprite(sprite);
		head.addComponent(spriteRenderer);
		
		player.getComponent(PlayerController.class).setHead(head);
		
		return head;
	}
	
	public static GameObject generateHammerObject(GameObject player, Sprite sprite, float sizeX, float sizeY) {
		GameObject hammer = Window.getScene().createGameObject("Hammer");
		
		hammer.transform.scale.x = sizeX;
		hammer.transform.scale.y = sizeY;
		
		SpriteRenderer spriteRenderer = new SpriteRenderer();
		spriteRenderer.setSprite(sprite);
		hammer.addComponent(spriteRenderer);
		
		Rigidbody2D rb = new Rigidbody2D();
		rb.setBodyType(BodyType.Dynamic);
		rb.setContinuousCollision(true);
		rb.setFixedRotation(true);
		rb.setFriction(100f);
		rb.setMass(.5f);
		rb.setGravityScale(0);
//		rb.setMass(player.getComponent(Rigidbody2D.class).getMass());
		hammer.addComponent(rb);
		
		Box2DCollider boxCollider = new Box2DCollider();
		boxCollider.setHalfSize(new Vector2f(sizeX, sizeY));
		
		hammer.transform.position = new Vector2f(
				player.transform.position.x + 1.5f, player.transform.position.y - 1f);
		hammer.addComponent(boxCollider);
		
		PlayerController playerController = player.getComponent(PlayerController.class);
		hammer.addComponent(new HammerController(playerController));
		
		playerController.setHammer(hammer);
		
		return hammer;
	}
	
	public static GameObject generateHammerVisual(GameObject player, Sprite sprite, float sizeX, float sizeY) {
		GameObject hammerVisual = Window.getScene().createGameObject("Hammer Visual");
		hammerVisual.transform.scale.x = sizeX;
		hammerVisual.transform.scale.y = sizeY;
		hammerVisual.transform.position = player.transform.position;
		hammerVisual.transform.zIndex = player.transform.zIndex + 2;
		
		SpriteRenderer spriteRenderer = new SpriteRenderer();
		spriteRenderer.setSprite(sprite);
		hammerVisual.addComponent(spriteRenderer);
		
		player.getComponent(PlayerController.class).setHammerVisual(hammerVisual);
		
		return hammerVisual;
	}
	
	public static GameObject generatePlayerPivot(GameObject player, Sprite sprite, float sizeX, float sizeY) { 
		GameObject playerPivot = Window.getScene().createGameObject("Player Pivot");
		playerPivot.transform.scale.x = sizeX;
		playerPivot.transform.scale.y = sizeY;
		playerPivot.transform.position = player.transform.position;
		
		SpriteRenderer spriteRenderer = new SpriteRenderer();
		spriteRenderer.setSprite(sprite);
		playerPivot.addComponent(spriteRenderer);
		
		Rigidbody2D rb = new Rigidbody2D();
		rb.setBodyType(BodyType.Dynamic);
		rb.setFixedRotation(false);
		playerPivot.addComponent(rb);
		
		Box2DCollider bc = new Box2DCollider();
		bc.setHalfSize(new Vector2f(sizeX, sizeY));
		playerPivot.addComponent(bc);
		
		playerPivot.addComponent(new RevoluteJoint2D(player));
		
		return playerPivot;
	}

	public static GameObject generateBlock(Sprite sprite, float sizeX, float sizeY) {
		GameObject block = Window.getScene().createGameObject("Block");
		block.transform.scale.x = sizeX;
		block.transform.scale.y = sizeY;
		block.transform.position = new Vector2f(0, 0);
		
		SpriteRenderer spriteRenderer = new SpriteRenderer();
		spriteRenderer.setSprite(sprite);
		block.addComponent(spriteRenderer);
		
		Rigidbody2D rb = new Rigidbody2D();
		rb.setBodyType(BodyType.Static);
		block.addComponent(rb);
		
		Box2DCollider bc = new Box2DCollider();
		bc.setHalfSize(new Vector2f(sizeX, sizeY));
		block.addComponent(bc);
		
		return block;
	}
}
