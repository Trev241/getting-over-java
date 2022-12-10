package physics2d.components;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.joml.Vector2f;

import components.Component;
import jindow.Window;
import physics2d.enums.BodyType;

public class Rigidbody2D extends Component {
	private Vector2f velocity = new Vector2f();
	private BodyType bodyType = BodyType.Dynamic; 
	
	private float angularDamping = .8f;
	private float linearDamping = .9f;
	private float mass = 0;
	private float density = 1f;
	
	public float friction = .1f;
	public float angularVelocity = 0.0f;
	public float gravityScale = 1.0f;
	
	private boolean isSensor = false;
	
	private boolean fixedRotation = false;
	private boolean continuousCollision = true;
	
	private transient Body rawBody = null;
	
	@Override
	public void update(float dt) {
		// Match our game object with the raw body that is processed by the physics engine
		if (rawBody != null) {
//			if (gameObject.getName().equals("Hammer")) {
//				rawBody.setTransform(
//						new Vec2(gameObject.transform.position.x, gameObject.transform.position.y),
//						gameObject.transform.rotation
//				);
//				return;
//			}
			
			this.gameObject.transform.position.set(rawBody.getPosition().x, rawBody.getPosition().y);
			this.gameObject.transform.rotation = (float) Math.toDegrees(rawBody.getAngle());
		}
	}
	
	public void addVelocity(Vector2f forceToAdd) {
		if (rawBody != null) {
			rawBody.applyForceToCenter(new Vec2(velocity.x, velocity.y));
		}
	}
	
	public void addImpulse(Vector2f impulse) {
		if (rawBody != null) {
			rawBody.applyLinearImpulse(new Vec2(impulse.x, impulse.y), rawBody.getWorldCenter());
		}
	}
	
	public Vector2f getVelocity() {
		return new Vector2f(rawBody.getLinearVelocity().x, rawBody.getLinearVelocity().y);
	}

	public void setVelocity(Vector2f velocity) {
		this.velocity.set(velocity);
		if (rawBody != null) {
			this.rawBody.setLinearVelocity(new Vec2(velocity.x, velocity.y));
		}
	}

	public BodyType getBodyType() {
		return bodyType;
	}

	public void setBodyType(BodyType bodyType) {
		this.bodyType = bodyType;
	}
	
	public void setAngularVelocity(float angularVelocity) {
		this.angularVelocity = angularVelocity;
		if (rawBody != null) {
			this.rawBody.setAngularDamping(angularVelocity);
		}
	}
	
	public void setGravityScale(float gravityScale) {
		this.gravityScale = gravityScale;
		if (rawBody != null) {
			this.rawBody.setGravityScale(gravityScale);
		}
	}


	public void setIsSensor() {
		isSensor = true;
		if (rawBody != null) {
			Window.getPhysics().setIsSensor(this);
		}
	}
	
	public void setNotSensor() {
		isSensor = false;
		if (rawBody != null) {
			Window.getPhysics().setNotSensor(this);
		}
	}
	
	public float getFriction() {
		return this.friction;
	}
	
	public void setFriction(float friction) {
		this.friction = friction;
	}
	
	public boolean isSensor() {
		return this.isSensor;
	}
	
	public void addForce(Vector2f force) {
		rawBody.applyForceToCenter(new Vec2(force.x, force.y));
	}
	
	public float getAngularDamping() {
		return angularDamping;
	}

	public void setAngularDamping(float angularDamping) {
		this.angularDamping = angularDamping;
	}

	public float getLinearDamping() {
		return linearDamping;
	}

	public void setLinearDamping(float linearDamping) {
		this.linearDamping = linearDamping;
	}
	
	public void setTorque(float torque) {
		this.rawBody.applyTorque(torque);
	}

	public float getMass() {
		return mass;
	}

	public void setMass(float mass) {
		this.mass = mass;
	}

	public boolean isFixedRotation() {
		return fixedRotation;
	}

	public void setFixedRotation(boolean fixedRotation) {
		this.fixedRotation = fixedRotation;
	}

	public boolean isContinuousCollision() {
		return continuousCollision;
	}

	public void setContinuousCollision(boolean continuousCollision) {
		this.continuousCollision = continuousCollision;
	}

	public Body getRawBody() {
		return rawBody;
	}

	public void setRawBody(Body rawBody) {
		this.rawBody = rawBody;
	}

	public float getGravityScale() {
		return this.gravityScale;
	}

	public void setDensity(float density) {
		this.density = density;
	}
	
	public float getDensity() {
		return this.density;
	}
}
