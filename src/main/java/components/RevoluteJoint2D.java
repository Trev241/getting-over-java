package components;

import org.jbox2d.dynamics.joints.RevoluteJoint;

import jindow.GameObject;

public class RevoluteJoint2D extends Component {
	private GameObject otherGameObject;
	private RevoluteJoint rawJoint;
	
	public RevoluteJoint2D(GameObject otherGameObject) {
		this.otherGameObject = otherGameObject;
	}
	
	public void setRawJoint(RevoluteJoint rawJoint) {
		this.rawJoint = rawJoint;
	}
	
	public GameObject getOtherGameObject() {
		return this.gameObject;
	}
}
