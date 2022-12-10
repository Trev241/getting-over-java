package components;

import org.jbox2d.dynamics.joints.DistanceJoint;
import org.jbox2d.dynamics.joints.Joint;

import jindow.GameObject;

public class DistanceJoint2D extends Component {
	private float length;
	
	private GameObject otherGameObject;
	private DistanceJoint rawJoint;
	
	public DistanceJoint2D(GameObject otherGameObject, float length) {
		this.otherGameObject = otherGameObject;
		this.length = length;
	}
	
	public GameObject getOtheGameObject() {
		return this.otherGameObject;
	}

	public float getLength() {
		return this.length;
	}

	public void setLength(float radius) {
		this.length = radius;
		rawJoint.setLength(this.length);
	}
	
	public void setRawJoint(DistanceJoint rawJoint) {
		this.rawJoint = rawJoint;
	}
	
	@Override
	public void destroy() {
		Joint.destroy(rawJoint);
	}
}
