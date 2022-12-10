package physics2d;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.DistanceJoint;
import org.jbox2d.dynamics.joints.DistanceJointDef;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.MouseJoint;
import org.jbox2d.dynamics.joints.MouseJointDef;
import org.jbox2d.dynamics.joints.RevoluteJoint;
import org.jbox2d.dynamics.joints.RevoluteJointDef;
import org.jbox2d.dynamics.joints.RopeJoint;
import org.jbox2d.dynamics.joints.RopeJointDef;
import org.jbox2d.dynamics.joints.WeldJoint;
import org.jbox2d.dynamics.joints.WeldJointDef;
import org.joml.Vector2f;

import components.DistanceJoint2D;
import components.MouseJoint2D;
import components.RevoluteJoint2D;
import components.RopeJoint2D;
import jindow.GameObject;
import jindow.Transform;
import physics2d.components.Box2DCollider;
import physics2d.components.CircleCollider;
import physics2d.components.PillboxCollider;
import physics2d.components.Rigidbody2D;

public class Physics2D {
	private Vec2 gravity = new Vec2(0, -10.0f);
	private World world = new World(gravity);
	private Body groundBody;
	
	private float physicsTime = 0.0f;
	private float physicsTimeStep = 1.0f / 60.0f;
	
	private int velocityIterations = 8;
	private int positionIterations = 3;
	
	public Physics2D() {
		world.setContactListener(new JindowContactListener());
		
		// Create a ground object to attach mouse joints too
		PolygonShape groundPoly = new PolygonShape();
		groundPoly.setAsBox(1, 1);
		
		GameObject groundGameObject = new GameObject("Ground");
		BodyDef groundBodyDef = new BodyDef();
		groundBodyDef.type = BodyType.STATIC;
		groundBody = world.createBody(groundBodyDef);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = groundPoly;
		fixtureDef.filter.groupIndex = 0;
		fixtureDef.userData = groundGameObject;
		groundBody.createFixture(fixtureDef);
//		groundPoly.dispose();
	}
	
	public void add(GameObject gameObject) { 
		Rigidbody2D rb = gameObject.getComponent(Rigidbody2D.class);
		if (rb != null && rb.getRawBody() == null) {
			Transform transform = gameObject.transform;
			
			BodyDef bodyDef = new BodyDef();
			bodyDef.angle = (float) Math.toRadians(transform.rotation);
			bodyDef.position.set(transform.position.x, transform.position.y);
			bodyDef.angularDamping = rb.getAngularDamping();
			bodyDef.linearDamping = rb.getLinearDamping();
			bodyDef.fixedRotation = rb.isFixedRotation();
			bodyDef.bullet = rb.isContinuousCollision();
			bodyDef.gravityScale = rb.gravityScale;
			bodyDef.angularVelocity = rb.angularVelocity;
			bodyDef.userData = rb.gameObject;
			
			switch (rb.getBodyType()) {
			case Kinematic:
				bodyDef.type = BodyType.KINEMATIC;
				break;
			case Static:
				bodyDef.type = BodyType.STATIC;
				break;
			case Dynamic:
				bodyDef.type = BodyType.DYNAMIC;
				break;
			}

			Body body = this.world.createBody(bodyDef);
			body.m_mass = rb.getMass();
			rb.setRawBody(body);
			
			CircleCollider circleCollider;
			Box2DCollider boxCollider;
			
			if ((circleCollider = gameObject.getComponent(CircleCollider.class)) != null) {
				addCircleCollider(rb, circleCollider);
			}
			
			if ((boxCollider = gameObject.getComponent(Box2DCollider.class)) != null) {
				addBox2DCollider(rb, boxCollider);
			}
			
			DistanceJoint2D distanceJoint;
			MouseJoint2D mouseJoint;
			RevoluteJoint2D revoluteJoint;
			RopeJoint2D ropeJoint;
			
			if ((distanceJoint = gameObject.getComponent(DistanceJoint2D.class)) != null) {
				Rigidbody2D rb2 = distanceJoint.getOtheGameObject().getComponent(Rigidbody2D.class);
				// Let the Joint wrapper component take the reference back for future use
				distanceJoint.setRawJoint(addDistanceJoint(rb, rb2, distanceJoint.getLength()));
			}
			
			if ((mouseJoint = gameObject.getComponent(MouseJoint2D.class)) != null) {
				mouseJoint.setRawJoint(addMouseJoint(rb));
			}
			
			if ((revoluteJoint = gameObject.getComponent(RevoluteJoint2D.class)) != null) {
				Rigidbody2D rb2 = revoluteJoint.getOtherGameObject().getComponent(Rigidbody2D.class);
				revoluteJoint.setRawJoint(addRevoluteJoint(rb, rb2));
			}
			
			if ((ropeJoint = gameObject.getComponent(RopeJoint2D.class)) != null) {
				Rigidbody2D rb2 = ropeJoint.getOtherGameObject().getComponent(Rigidbody2D.class);
				ropeJoint.setRawJoint(addRopeJoint(rb, rb2, ropeJoint.getLength()));
			}
		}
	}

	public void destroyGameObject(GameObject gameObject) {
		Rigidbody2D rb = gameObject.getComponent(Rigidbody2D.class);
		if (rb != null) {
			if (rb.getRawBody() != null) {
				world.destroyBody(rb.getRawBody());
				rb.setRawBody(null);
			}
		}
	}
	
	public void update(float dt) {
		physicsTime += dt;
		
		// This ensures that the physics engine is incremented only at
		// FIXED ASSURED INTERVALS OF 16ms. If a frame has been processed
		// too fast (suppose it was completed in 14ms), physicsTime will
		// become negative (because 14ms - 16ms = -2ms). Hence the engine
		// will not be incremented for this frame since it is yet too soon
		if (physicsTime >= .0f) {
			physicsTime -= physicsTimeStep;
			world.step(physicsTimeStep, velocityIterations, positionIterations);	
		}
		
		
	}
	
	public void setIsSensor(Rigidbody2D rb) {
		Body body = rb.getRawBody();
		if (body == null) return;
		
		Fixture fixture = body.getFixtureList();
		while (fixture != null) {
			fixture.m_isSensor = true;
			fixture = fixture.m_next;
		}
	}
	
	public void setNotSensor(Rigidbody2D rb) {
		Body body = rb.getRawBody();
		if (body == null) return;
		
		Fixture fixture = body.getFixtureList();
		while (fixture != null) {
			fixture.m_isSensor = false;
			fixture = fixture.m_next;
		}
	}
	
	public void resetCircleCollider(Rigidbody2D rb, CircleCollider circleCollider) {
		Body body = rb.getRawBody();
		if (body == null) return;
		
		int size = fixtureListSize(body);
		for (int i = 0; i < size; i++) {
			body.destroyFixture(body.getFixtureList());
		}
		
		addCircleCollider(rb, circleCollider);
		body.resetMassData();
	}

	public void addBox2DCollider(Rigidbody2D rb, Box2DCollider boxCollider) {
		Body body = rb.getRawBody();
		assert body !=null : "ERROR: [PHYSICS 2D] Raw body must not be null";
		
		PolygonShape shape = new PolygonShape();
		Vector2f halfSize = new Vector2f(boxCollider.getHalfSize()).div(2f);
		Vector2f offset = boxCollider.getOffset();
		Vector2f origin = new Vector2f(boxCollider.getOrigin());
		shape.setAsBox(halfSize.x, halfSize.y, new Vec2(offset.x, offset.y), 0);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = rb.getDensity();
		fixtureDef.friction = rb.getFriction();
		fixtureDef.userData = boxCollider.gameObject;
		fixtureDef.isSensor = rb.isSensor();
		body.createFixture(fixtureDef);
	}
	
	public void resetBox2DCollider(Rigidbody2D rb, Box2DCollider boxCollider) {
		Body body = rb.getRawBody();
		if (body == null) return;
		
		int size = fixtureListSize(body);
		for (int i = 0; i < size; i++) {
			body.destroyFixture(body.getFixtureList());
		}
		
		addBox2DCollider(rb, boxCollider);
		body.resetMassData();
	}
	
	/**
	 * Adds a distance joint between two rigidbodies. Their anchors are assumed to be their world center. After the joint is added, a reference
	 * to it is returned. This reference must be destroyed when it is no longer used
	 * 
	 * @param objA the Rigidbody2D component of the first body
	 * @param objB the Rigidbody2D component of the second body
	 * @param length the length of the joint (must strictly adhere to MKS units)
	 * @return a reference to the newly created joint
	 */
	public DistanceJoint addDistanceJoint(Rigidbody2D objA, Rigidbody2D objB, float length) {
		Body bodyA = objA.getRawBody();
		Body bodyB = objB.getRawBody();
		
		DistanceJointDef jointDef = new DistanceJointDef();
		jointDef.initialize(bodyA, bodyB, bodyA.getWorldCenter(), bodyB.getWorldCenter());
		jointDef.dampingRatio = 1f;
		jointDef.frequencyHz = 30f;
		jointDef.length = length;
		
		return (DistanceJoint) world.createJoint(jointDef);
	}
	
	public RopeJoint addRopeJoint(Rigidbody2D objA, Rigidbody2D objB, float length) {
		Body bodyA = objA.getRawBody();
		Body bodyB = objB.getRawBody();
		
		RopeJointDef jointDef = new RopeJointDef();
		jointDef.bodyA = bodyA;
		jointDef.bodyB = bodyB;
		jointDef.localAnchorA.set(0, 0);
		jointDef.localAnchorB.set(0, 0);
		jointDef.maxLength = length;
		jointDef.collideConnected = false;
		
		return (RopeJoint) world.createJoint(jointDef);
	}
	
	public WeldJoint addWeldJoint(Rigidbody2D objA, Rigidbody2D objB, float length) {
		Body bodyA = objA.getRawBody();
		Body bodyB = objB.getRawBody();
		
		WeldJointDef jointDef = new WeldJointDef();
		jointDef.initialize(bodyA, bodyB, bodyA.getWorldCenter());
		jointDef.bodyA = bodyA;
		jointDef.bodyB = bodyB;
		jointDef.collideConnected = false;
		
		return (WeldJoint) world.createJoint(jointDef);
	}
	
	/**
	 * Adds a mouse joint that is attached to the rigidbody passed. The anchor is assumed to be its world center. After the joint is added, a reference to it is
	 * returned. This reference must be destroyed when it is no longer used.
	 * @param obj the object to which the mouse joint is attached
	 * @return a reference to the newly created joint
	 */
	public MouseJoint addMouseJoint(Rigidbody2D obj) {
		Body bodyB = obj.getRawBody();
		
		MouseJointDef jointDef = new MouseJointDef();
		jointDef.bodyA = groundBody;
		jointDef.bodyB = bodyB;
		jointDef.frequencyHz = 30f;
		jointDef.dampingRatio = 1f;
		jointDef.maxForce = (float) (210.0f * bodyB.getMass());
		jointDef.target.set(bodyB.getWorldCenter());
		
		return (MouseJoint) world.createJoint(jointDef);
	}
	
	public RevoluteJoint addRevoluteJoint(Rigidbody2D objA, Rigidbody2D objB) {
		Body bodyA = objA.getRawBody();
		Body bodyB = objB.getRawBody();
		
		RevoluteJointDef jointDef = new RevoluteJointDef();
		jointDef.initialize(bodyA, bodyB, bodyA.getWorldCenter());
		jointDef.enableMotor = false;
		jointDef.collideConnected = false;
		
		return (RevoluteJoint) world.createJoint(jointDef);
	}
	
	/**
	 * Destroys the joint passed. As an additional measure, you may also set the joint's reference to <b>null</b> after invoking this function.
	 * This method will destroy any type of joint as long as it inherits org.jbox2d.dynamics.joints.Joint.
	 * @param joint
	 */
	public void destroyJoint(Joint joint) {
		world.destroyJoint(joint);
	}
	
	public void addPillboxCollider(Rigidbody2D rb, PillboxCollider pb) {
		Body body = rb.getRawBody();
		assert body !=null : "ERROR: [PHYSICS 2D] Raw body must not be null";
		
		addBox2DCollider(rb, pb.getBox());
		addCircleCollider(rb, pb.getTopCircle());
		addCircleCollider(rb, pb.getBottomCircle());
	}
	
	public void resetPillboxCollider(Rigidbody2D rb, PillboxCollider pb) {
		Body body = rb.getRawBody();
		if (body == null) return;
		
		int size = fixtureListSize(body);
		for (int i = 0; i < size; i++) {
			body.destroyFixture(body.getFixtureList());
		}
		
		addPillboxCollider(rb, pb);
		body.resetMassData();
	}
	
	public RaycastInfo raycast(GameObject requestingObject, Vector2f point1, Vector2f point2) {
		RaycastInfo callback = new RaycastInfo(requestingObject);
		world.raycast(callback, new Vec2(point1.x, point1.y), new Vec2(point2.x, point2.y));
		return callback;
	}
	

	public void addCircleCollider(Rigidbody2D rb, CircleCollider circleCollider) {
		Body body = rb.getRawBody();
		assert body !=null : "ERROR: [PHYSICS 2D] Raw body must not be null";
		
		CircleShape shape = new CircleShape();
		shape.setRadius(circleCollider.getRadius());
		shape.m_p.set(circleCollider.getOffset().x, circleCollider.getOffset().y);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 1.0f;
		fixtureDef.friction = rb.getFriction();
		fixtureDef.userData = circleCollider.gameObject;
		fixtureDef.isSensor = rb.isSensor();
		body.createFixture(fixtureDef);
	}	
	
	private int fixtureListSize(Body body) {
		int size = 0;
		Fixture fixture = body.getFixtureList();
		while (fixture != null) {
			size++;
			fixture = fixture.m_next;
		}
		
		return size;
	}

	public boolean isLocked() {
		 return world.isLocked();
	}
}
