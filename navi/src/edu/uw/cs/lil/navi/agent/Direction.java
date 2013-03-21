package edu.uw.cs.lil.navi.agent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uw.cs.lil.navi.agent.Action.AgentAction;
import edu.uw.cs.lil.navi.map.Position;

public abstract class Direction {
	public static final Direction				BACK	= new Direction("back",
																null) {
															@Override
															public Position orientPosition(
																	Position position) {
																return position
																		.getBack();
															}
														};
	
	public static final Direction				FORWARD	= new Direction(
																"forward",
																AgentAction.FORWARD) {
															@Override
															public Position orientPosition(
																	Position position) {
																return position;
															}
														};
	
	public static final Direction				LEFT	= new Direction(
																"left",
																AgentAction.LEFT) {
															@Override
															public Position orientPosition(
																	Position position) {
																return position
																		.getLeft();
															}
														};
	
	public static final Direction				RIGHT	= new Direction(
																"right",
																AgentAction.RIGHT) {
															@Override
															public Position orientPosition(
																	Position position) {
																return position
																		.getRight();
															}
														};
	
	private static final Map<String, Direction>	STRING_MAPPING;
	private static final List<Direction>		VALUES;
	
	private final AgentAction					agentAction;
	
	private final String						label;
	
	private Direction(String label, AgentAction agentAction) {
		this.label = label;
		this.agentAction = agentAction;
	}
	
	static {
		final Map<String, Direction> stringMapping = new HashMap<String, Direction>();
		
		stringMapping.put(LEFT.label, LEFT);
		stringMapping.put(RIGHT.label, RIGHT);
		stringMapping.put(FORWARD.label, FORWARD);
		stringMapping.put(BACK.label, BACK);
		
		STRING_MAPPING = Collections.unmodifiableMap(stringMapping);
		VALUES = Collections.unmodifiableList(new ArrayList<Direction>(
				stringMapping.values()));
	}
	
	public static Direction valueOf(String string) {
		return STRING_MAPPING.get(string);
	}
	
	public static List<Direction> values() {
		return VALUES;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj == this;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((agentAction == null) ? 0 : agentAction.hashCode());
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		return result;
	}
	
	public abstract Position orientPosition(Position position);
	
	@Override
	public String toString() {
		return label;
	}
	
	public boolean validaAgentAction(AgentAction action) {
		return action == agentAction;
	}
}
