package components;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.MouseJoint;
import org.joml.Vector2f;

public class MouseJoint2D extends Component {
	private MouseJoint rawMouseJoint;
	
	public void setTarget(Vector2f target) {
		rawMouseJoint.setTarget(new Vec2(target.x, target.y));
	}
	
	public void setRawJoint(MouseJoint rawMouseJoint) {
		this.rawMouseJoint = rawMouseJoint;
	}

	public MouseJoint getRawJoint() {
		return this.rawMouseJoint;
	}
	
	@Override
	public void destroy() {
		Joint.destroy(rawMouseJoint);
	}
}
