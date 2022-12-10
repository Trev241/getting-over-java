package components;

import org.jbox2d.dynamics.contacts.Contact;
import org.jbox2d.dynamics.joints.MouseJointDef;
import org.joml.Vector2f;

import jindow.Camera;
import jindow.GameObject;
import jindow.MouseListener;
import jindow.Sound;
import physics2d.components.Rigidbody2D;
import util.AssetPool;

public class PlayerController extends Component {
	private double angle;
	
	private float hammerVelModifier = 12f;
	private float inputModifier= 8f;
	private float playerDefaultGravityScale;
	private float radius;
	private float minRadius = .35f;
	private float maxRadius = 1.25f;
	private float cameraSensitivity = 3.75f;
	private float maxImpulse = .75f;
	private float soundCooldown = 2f;
	private float currentSoundCooldown = soundCooldown;
	
	private boolean hammerSoundPlayed;
	
	private GameObject head;
	private GameObject hammer;
	private GameObject hammerVisual;
	private Rigidbody2D playerRb;
	private Rigidbody2D hammerRb;
	private RopeJoint2D ropeJoint;
	private HammerController hammerController;
	private Sound bucketSound;
	private Sound gruntSound;
	private Sound hammerSound;
	private Camera camera;
	
	public PlayerController(Camera camera) {
		this.camera = camera;
	}
	
	@Override
	public void start() {
		playerRb = gameObject.getComponent(Rigidbody2D.class);
		hammerRb = hammer.getComponent(Rigidbody2D.class);
		ropeJoint = (RopeJoint2D) gameObject.addComponent(new RopeJoint2D(hammer, maxRadius));
//		distanceJoint = (DistanceJoint2D) gameObject.addComponent(new DistanceJoint2D(hammer, maxRadius));
		hammerController = hammer.getComponent(HammerController.class);
		
		bucketSound = AssetPool.getSound("assets/sounds/bucket-ting.ogg");
		gruntSound = AssetPool.getSound("assets/sounds/wo.ogg");
		hammerSound = AssetPool.getSound("assets/sounds/hammer.ogg");
		
		playerDefaultGravityScale = playerRb.getGravityScale();
	}
	
	@Override
	public void update(float dt) {
		// Focus camera on player
		Vector2f targetPos = new Vector2f(
				gameObject.transform.position.x - camera.getProjectionSize().x / 2,
				gameObject.transform.position.y - camera.getProjectionSize().y / 2
		);
		this.camera.position.lerp(targetPos, dt * cameraSensitivity);
		
		Vector2f mousePos = MouseListener.getWorld();
		
		double adj = mousePos.x - gameObject.transform.position.x;
		double opp = mousePos.y - gameObject.transform.position.y;
		this.angle = (float) Math.atan2(opp, adj);
		
		radius = (float) Math.abs(Math.sqrt(
				Math.pow(mousePos.x - gameObject.transform.position.x, 2) +
				Math.pow(mousePos.y - gameObject.transform.position.y, 2)
		));
		
		radius = Math.max(minRadius, Math.min(radius, maxRadius));

		// Don't re-adjust the joint's length. It causes a spring effect if we constantly try to resize it.
		// This is most noticeable when the player rests hammer on the ground right below the player's hitbox
		// Use ROPE JOINT instead
//		distanceJoint.setLength(radius);

		float xForce = Math.max(-maxImpulse, Math.min(MouseListener.getWorldDx() * inputModifier, maxImpulse));
		float yForce = Math.max(-maxImpulse, Math.min(MouseListener.getWorldDy() * inputModifier, maxImpulse));
		
		if (hammerController.isColliding() && !hammerController.isAnchoredTop()) {
			if (hammerController.isAnchoredBottom()) playerRb.setGravityScale(0); 
			else {
				yForce = 0;
				playerRb.setGravityScale(playerDefaultGravityScale);
			}			
			
			if (!hammerSoundPlayed) {
				hammerSoundPlayed = true;
				hammerSound.play();
			}
			
//			System.out.println("[PLAYER CONTROLLER]: Impulse generated: " + xForce + " " + yForce);
			playerRb.addImpulse(new Vector2f(-xForce, -yForce));
		} else {
			// Gradually return the gravity back to normal, this is better than instantly restoring it.
			// Helps avoid issues where the player occasionally falls back down
//			if (playerRb.getGravityScale() < playerDefaultGravityScale) 
//				playerRb.setGravityScale(playerRb.getGravityScale() + Math.min(playerDefaultGravityScale - playerRb.getGravityScale(), .1f));
			playerRb.setGravityScale(playerDefaultGravityScale);
			hammerSoundPlayed = false;
		}
	
		// Calculate coordinates on circumference
		float xLocal = (float) Math.cos(this.angle) * radius; 
		float yLocal = (float) Math.sin(this.angle) * radius;
		
		// Adjust with player's position so that the coordinates are now relative to the player
		float x = (float) xLocal + gameObject.transform.position.x;
		float y = (float) yLocal + gameObject.transform.position.y;
		
		// Move the hammer
		Vector2f velocity = new Vector2f(x, y).sub(hammer.transform.position).mul(hammerVelModifier);
		hammerRb.setVelocity(velocity);
		
		float playerDistanceFromHammer = (float) Math.abs(Math.sqrt(
				Math.pow(hammer.transform.position.x - gameObject.transform.position.x, 2) +
				Math.pow(hammer.transform.position.y - gameObject.transform.position.y, 2)
		));
		
		float hammerDistFromRadius = (playerDistanceFromHammer - hammerVisual.transform.scale.x / 2);
		double playerHammerAngle = Math.atan2( 
			hammer.transform.position.y - gameObject.transform.position.y,
			hammer.transform.position.x - gameObject.transform.position.x
		);
		
		float hammerVisualX = (float) Math.cos(playerHammerAngle) * hammerDistFromRadius + gameObject.transform.position.x;
		float hammerVisualY = (float) Math.sin(playerHammerAngle) * hammerDistFromRadius + gameObject.transform.position.y;
		
		hammerVisual.transform.position = new Vector2f(hammerVisualX, hammerVisualY);
		hammerVisual.transform.rotation = (float) Math.toDegrees(playerHammerAngle);
		
		float angleInDegrees = (float) Math.toDegrees(this.angle);
		head.transform.position = new Vector2f(gameObject.transform.position.x, gameObject.transform.position.y + .52f);
		// If facing left but scale is positive i.e. sprite is facing right 
		// OR
		// If facing right but scale is negative i.e. sprite is facing left
		if ((xLocal < 0 && head.transform.scale.x > 0) || (xLocal > 0 && head.transform.scale.x < 0)) {
			head.transform.scale.x *= -1;
		}
		
		if (xLocal >= 0)
			head.transform.rotation = Math.min(15, Math.max(-5, angleInDegrees));
		else {
			angleInDegrees -= 180;
			if (angleInDegrees > -355 && angleInDegrees <= -270)
				angleInDegrees = -355;
			else if (angleInDegrees < -15 && angleInDegrees >= -90)
				angleInDegrees = -15;
			head.transform.rotation = angleInDegrees;
		}
		
		if (currentSoundCooldown > 0) currentSoundCooldown -= dt;
		
	}

	@Override
	public void preSolve(GameObject collidingObject, Contact contact, Vector2f hitNormal) {
		
	}
	
	@Override
	public void beginCollision(GameObject collidingObject, Contact contact, Vector2f hitNormal) {
//		System.out.println(playerRb.getVelocity().y);
		if (hitNormal.y <= -.675f && Math.abs(playerRb.getVelocity().y) >= 3f && !bucketSound.isPlaying())
			bucketSound.play();
	}
	
	@Override
	public void destroy() {
		
	}

	public void setHammer(GameObject hammer) {
		this.hammer = hammer;
	}

	public GameObject getHammer() {
		return this.hammer;
	}

	public void setHammerVisual(GameObject hammerVisual) {
		this.hammerVisual = hammerVisual;
	}

	public double getRadius() {
		return this.radius;
	}

	public double getAngle() {
		return this.angle;
	}

	public void setHead(GameObject head) {
		this.head = head;
	}
} 
