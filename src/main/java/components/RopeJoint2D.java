package components;

import org.jbox2d.dynamics.joints.RopeJoint;

import jindow.GameObject;

public class RopeJoint2D extends Component {
	private RopeJoint rawJoint;
	private GameObject otherGameObject;
	
	private float length;
	
	public RopeJoint2D(GameObject otherGameObject, float length) {
		this.otherGameObject = otherGameObject;
		this.length = length;
	}
	
	public void setRawJoint(RopeJoint rawJoint) {
		this.rawJoint = rawJoint;
	}

	public GameObject getOtherGameObject() {
		return this.otherGameObject;
	}

	public float getLength() {
		return this.length;
	}
}
