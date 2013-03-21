package edu.uw.cs.lil.navi.eval;

import edu.uw.cs.lil.navi.map.Position;

public class State {
	private final Position	agentPosition;
	private final boolean	inMovement;
	
	public State(Position agentPosition, boolean inMovement) {
		this.agentPosition = agentPosition;
		this.inMovement = inMovement;
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
		final State other = (State) obj;
		if (agentPosition == null) {
			if (other.agentPosition != null) {
				return false;
			}
		} else if (!agentPosition.equals(other.agentPosition)) {
			return false;
		}
		if (inMovement != other.inMovement) {
			return false;
		}
		return true;
	}
	
	public Position getAgentPosition() {
		return agentPosition;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((agentPosition == null) ? 0 : agentPosition.hashCode());
		result = prime * result + (inMovement ? 1231 : 1237);
		return result;
	}
	
	public boolean isInMovement() {
		return inMovement;
	}
	
	@Override
	public String toString() {
		return "State [agentPosition=" + agentPosition + ", inMovement="
				+ inMovement + "]";
	}
	
}
