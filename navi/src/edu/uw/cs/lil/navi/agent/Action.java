package edu.uw.cs.lil.navi.agent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.uw.cs.lil.navi.data.Step;
import edu.uw.cs.lil.navi.map.Position;

public class Action implements Comparable<Action> {
	private static final List<Action>	EMPTY_LIST	= Collections.emptyList();
	
	private final AgentAction			agentAction;
	private final Position				end;
	private final List<Action>			implicitActions;
	private final List<Position>		intermediatePositions;
	
	private final int					length;
	private final int					numImplicitSteps;
	private final int					numSteps;
	private final Position				start;
	
	public Action(Action explicit, List<Action> implicitActions) {
		this(explicit.getAgentAction(), explicit.getEnd(), explicit
				.getIntermediatePositions(), explicit.getStart(),
				implicitActions);
	}
	
	public Action(AgentAction agentAction, Position end,
			List<Position> intermediatePositions, Position start) {
		this(agentAction, end, intermediatePositions, start, EMPTY_LIST);
	}
	
	public Action(AgentAction agentAction, Position end,
			List<Position> intermediatePositions, Position start,
			List<Action> implicitActions) {
		this.agentAction = agentAction;
		this.end = end;
		this.start = start;
		this.intermediatePositions = intermediatePositions;
		this.implicitActions = Collections.unmodifiableList(implicitActions);
		
		// All members below are computed from above ones, and therefore not
		// required for hashcode() and equals()
		this.length = intermediatePositions.isEmpty() ? (end.equals(start) ? 0
				: 1) : (intermediatePositions.size() + 1);
		int counter = 0;
		for (final Action action : implicitActions) {
			counter += action.numSteps;
		}
		this.numImplicitSteps = counter;
		this.numSteps = 1 + intermediatePositions.size() + numImplicitSteps;
	}
	
	/**
	 * Shorter action is "smaller" than a longer one, length tie is broken by
	 * action order {@see AgentAction#order}.
	 * 
	 * @param a1
	 * @param a2
	 * @return
	 */
	public static int compare(Action a1, Action a2) {
		if (a1.implicitActions.size() != a2.implicitActions.size()) {
			return a1.implicitActions.size() - a2.implicitActions.size();
		} else if (a1.numImplicitSteps != a2.numImplicitSteps) {
			return a1.numImplicitSteps - a2.numImplicitSteps;
		} else if (a1.length != a2.length) {
			return a1.length() - a2.length();
		} else {
			return a1.agentAction.compareTo(a2.agentAction);
		}
	}
	
	public int compareTo(Action o) {
		return compare(this, o);
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
		final Action other = (Action) obj;
		if (agentAction == null) {
			if (other.agentAction != null) {
				return false;
			}
		} else if (!agentAction.equals(other.agentAction)) {
			return false;
		}
		if (end == null) {
			if (other.end != null) {
				return false;
			}
		} else if (!end.equals(other.end)) {
			return false;
		}
		if (implicitActions == null) {
			if (other.implicitActions != null) {
				return false;
			}
		} else if (!implicitActions.equals(other.implicitActions)) {
			return false;
		}
		if (intermediatePositions == null) {
			if (other.intermediatePositions != null) {
				return false;
			}
		} else if (!intermediatePositions.equals(other.intermediatePositions)) {
			return false;
		}
		if (start == null) {
			if (other.start != null) {
				return false;
			}
		} else if (!start.equals(other.start)) {
			return false;
		}
		return true;
	}
	
	public AgentAction getAgentAction() {
		return agentAction;
	}
	
	public Position getEnd() {
		return end;
	}
	
	public List<Action> getImplicitActions() {
		return implicitActions;
	}
	
	public List<Position> getIntermediatePositions() {
		return intermediatePositions;
	}
	
	public int getNumSteps() {
		return numSteps;
	}
	
	public Position getStart() {
		return start;
	}
	
	public List<Step> getSteps() {
		final List<Step> steps = new ArrayList<Step>(numSteps);
		// Steps from implicit actions
		for (final Action implicitAction : implicitActions) {
			for (final Step step : implicitAction.getSteps()) {
				steps.add(step.cloneAsImplicit());
			}
		}
		// Steps from this action
		steps.add(new Step(start, agentAction));
		for (final Position position : intermediatePositions) {
			steps.add(new Step(position, agentAction));
		}
		return steps;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((agentAction == null) ? 0 : agentAction.hashCode());
		result = prime * result + ((end == null) ? 0 : end.hashCode());
		result = prime * result
				+ ((implicitActions == null) ? 0 : implicitActions.hashCode());
		result = prime
				* result
				+ ((intermediatePositions == null) ? 0 : intermediatePositions
						.hashCode());
		result = prime * result + ((start == null) ? 0 : start.hashCode());
		return result;
	}
	
	public int length() {
		return length;
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder(agentAction.toString());
		if (length != 1) {
			sb.append("(x").append(length).append(")");
		}
		if (!implicitActions.isEmpty()) {
			sb.append("{");
			final Iterator<Action> iterator = implicitActions.iterator();
			while (iterator.hasNext()) {
				sb.append(iterator.next().toString());
				if (iterator.hasNext()) {
					sb.append(" => ");
				}
			}
			sb.append("}");
		}
		sb.append("[").append(start).append(" -> ");
		if (!intermediatePositions.isEmpty()) {
			sb.append(intermediatePositions).append(" -> ");
		}
		sb.append(end).append("]");
		return sb.toString();
	}
	
	public static class AgentAction implements Comparable<AgentAction> {
		public static final AgentAction					FORWARD	= new AgentAction(
																		"FORWARD",
																		0);
		
		public static final AgentAction					LEFT	= new AgentAction(
																		"LEFT",
																		1);
		
		public static final AgentAction					RIGHT	= new AgentAction(
																		"RIGHT",
																		2);
		public static final AgentAction					VERIFY	= new AgentAction(
																		"VERIFY",
																		3);
		private static final Map<String, AgentAction>	STRING_MAPPING;
		
		private static final List<AgentAction>			VALUES;
		private final String							label;
		
		private final int								order;
		
		public AgentAction(String label, int order) {
			this.label = label;
			this.order = order;
		}
		
		static {
			final Map<String, AgentAction> stringMapping = new HashMap<String, Action.AgentAction>();
			
			stringMapping.put(LEFT.toString(), LEFT);
			stringMapping.put(RIGHT.toString(), RIGHT);
			stringMapping.put(FORWARD.toString(), FORWARD);
			stringMapping.put(VERIFY.toString(), VERIFY);
			
			STRING_MAPPING = Collections.unmodifiableMap(stringMapping);
			VALUES = Collections.unmodifiableList(new ArrayList<AgentAction>(
					stringMapping.values()));
		}
		
		public static AgentAction valueOf(String string) {
			return STRING_MAPPING.get(string);
		}
		
		public static List<AgentAction> values() {
			return VALUES;
		}
		
		@Override
		public int compareTo(AgentAction o) {
			return order - o.order;
		}
		
		@Override
		public boolean equals(Object obj) {
			return obj == this;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((label == null) ? 0 : label.hashCode());
			return result;
		}
		
		@Override
		public String toString() {
			return label;
		}
	}
}
