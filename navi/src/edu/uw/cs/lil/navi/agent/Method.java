package edu.uw.cs.lil.navi.agent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Method {
	public static final Method					GOAL	= new Method("GOAL");
	public static final Method					TRAVEL	= new Method("TRAVEL");
	public static final Method					TURN	= new Method("TURN");
	
	private static final Map<String, Method>	STRING_MAPPING;
	private static final List<Method>			VALUES;
	
	private final String						label;
	
	private Method(String label) {
		this.label = label;
	}
	
	static {
		final Map<String, Method> mapping = new HashMap<String, Method>();
		
		mapping.put(GOAL.label, GOAL);
		mapping.put(TRAVEL.label, TRAVEL);
		mapping.put(TURN.label, TURN);
		
		STRING_MAPPING = Collections.unmodifiableMap(mapping);
		VALUES = Collections.unmodifiableList(new ArrayList<Method>(mapping
				.values()));
	}
	
	public static Method valueOf(String string) {
		return STRING_MAPPING.get(string);
	}
	
	public static List<Method> values() {
		return VALUES;
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
		final Method other = (Method) obj;
		if (label == null) {
			if (other.label != null) {
				return false;
			}
		} else if (!label.equals(other.label)) {
			return false;
		}
		return true;
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
