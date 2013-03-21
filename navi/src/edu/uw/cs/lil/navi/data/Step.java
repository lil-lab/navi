package edu.uw.cs.lil.navi.data;

import jregex.Matcher;
import jregex.Pattern;
import edu.uw.cs.lil.navi.agent.Action.AgentAction;
import edu.uw.cs.lil.navi.map.NavigationMap;
import edu.uw.cs.lil.navi.map.Pose;
import edu.uw.cs.lil.navi.map.Position;
import edu.uw.cs.utils.assertion.Assert;

/**
 * A single user step, includes the position and the action taken.
 * 
 * @author Yoav Artzi
 */
public class Step {
	
	private static final Pattern	STRING_PATTERN	= new Pattern(
															"({act}[a-zA-Z]+)({i}(\\[I\\])|)({pose}.*)");
	
	private final AgentAction		action;
	private final boolean			implicit;
	private final Position			position;
	
	public Step(AgentAction action, Position position, boolean implicit) {
		this.position = Assert.ifNull(position);
		this.action = Assert.ifNull(action);
		this.implicit = implicit;
	}
	
	public Step(Position position, AgentAction action) {
		this(action, position, false);
	}
	
	public static Step valueOf(String string, NavigationMap map) {
		final Matcher matcher = STRING_PATTERN.matcher(string);
		if (!matcher.matches()) {
			return null;
		} else {
			return new Step(AgentAction.valueOf(matcher.group("act")),
					map.get(Pose.valueOf(matcher.group("pose"))), matcher
							.group("i").length() != 0);
		}
		
	}
	
	public Step cloneAsImplicit() {
		return new Step(action, position, true);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Step other = (Step) obj;
		if (action == null) {
			if (other.action != null) {
				return false;
			}
		} else if (!action.equals(other.action)) {
			return false;
		}
		if (implicit != other.implicit) {
			return false;
		}
		if (position == null) {
			if (other.position != null) {
				return false;
			}
		} else if (!position.equals(other.position)) {
			return false;
		}
		return true;
	}
	
	public boolean equalsWithoutImplicit(Step step) {
		return action.equals(step.action) && position.equals(step.position);
	}
	
	public AgentAction getAction() {
		return action;
	}
	
	public Position getPosition() {
		return position;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((action == null) ? 0 : action.hashCode());
		result = prime * result + (implicit ? 1231 : 1237);
		result = prime * result
				+ ((position == null) ? 0 : position.hashCode());
		return result;
	}
	
	public boolean isImplicit() {
		return implicit;
	}
	
	@Override
	public String toString() {
		return action.toString() + (implicit ? "[I]" : "")
				+ position.toString();
	}
}
