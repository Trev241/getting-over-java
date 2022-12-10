package components;

import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;

import jindow.GameObject;
import jindow.MouseListener;
import physics2d.components.Rigidbody2D;

public class HammerController extends Component {
	private boolean isColliding;
	private boolean isAnchoredBottom;
	private boolean isAnchoredSide;
	private boolean isAnchoredTop;
	
	private float defaultFriction = 100f;
	
	private Rigidbody2D hammerRb;
	private PlayerController playerController;
	
	public HammerController(PlayerController playerController) {
		this.playerController = playerController;
	}
	
	@Override
	public void start() {
		hammerRb = gameObject.getComponent(Rigidbody2D.class);
		defaultFriction = hammerRb.getFriction();
	}
	
	@Override
	public void update(float dt) {
		
	}
	
	@Override
	public void preSolve(GameObject collidingObject, Contact contact, Vector2f hitNormal) {
		// Ignore the player
		if (collidingObject.getName().equals("Player"))
			contact.setEnabled(false); 
		
		if (hitNormal.y < 0)
			hammerRb.getRawBody().getFixtureList().m_friction = Math.abs(hitNormal.y) * defaultFriction;
		else 
			hammerRb.getRawBody().getFixtureList().m_friction = 0f;
		
//		System.out.println("Friction assigned: " + hammerRb.getFriction());
//		System.out.println(hammerRb.getRawBody().getFixtureList().m_friction);
//		System.out.println(hitNormal.x + " " + hitNormal.y);
	}
	
	@Override
	public void beginCollision(GameObject collidingObject, Contact contact, Vector2f hitNormal) {
		// Disregard collisions with player (these should not be occurring anyways since pre-solve disables such contacts with the player)
		if (collidingObject.getName().equals("Player")) return;
		
		isColliding = true;
		isAnchoredBottom = Math.abs(hitNormal.x) <= .75f;
		isAnchoredTop = hitNormal.y >= 0.8f;
	}
	
	@Override
	public void endCollision(GameObject collidingObject, Contact contact, Vector2f hitNormal) {
		isColliding = false;
	}
	
	public boolean isColliding() {
		return isColliding;
	}
	
	public boolean isAnchoredBottom() {
		return this.isAnchoredBottom;
	}
	
	public boolean isAnchoredSide() {
		return this.isAnchoredSide;
	}
	
	public boolean isAnchoredTop() {
		return this.isAnchoredTop;
	}
	
	public void setDefaultFriction(float defaultFriction) {
		this.defaultFriction = defaultFriction;
	}
}
