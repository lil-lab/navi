package edu.uw.cs.lil.navi.map.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uw.cs.lil.navi.map.NavigationMap;
import edu.uw.cs.lil.navi.map.Position;

/**
 * Hallway in a NAVI map {@link NavigationMap}.
 * <p>
 * NOTE: not using enum here, as its final hashcode() implementation uses the
 * memory address, creating inconsistencies between runs.
 * 
 * @author Yoav Artzi
 */
public class NaviHall extends AbstractNaviItem {
	public static final NaviHall				BLUE		= new NaviHall(
																	"BLUE");
	public static final NaviHall				BRICK		= new NaviHall(
																	"BRICK");
	public static final NaviHall				CEMENT		= new NaviHall(
																	"CEMENT");
	public static final NaviHall				GRASS		= new NaviHall(
																	"GRASS");
	public static final NaviHall				HALL		= new NaviHall(
																	"HALL") {
																@Override
																public boolean isValid(
																		Position position) {
																	return !(UNKNOWN_H
																			.isValid(position) || WALL
																			.isValid(position));
																};
															};
	public static final NaviHall				HONEYCOMB	= new NaviHall(
																	"HONEYCOMB");
	public static final NaviHall				ROSE		= new NaviHall(
																	"ROSE");
	public static final NaviHall				STONE		= new NaviHall(
																	"STONE");
	public static final NaviHall				UNKNOWN_H	= new NaviHall(
																	"UNKNOWN_H");
	/**
	 * Indicates there's a wall in this position.
	 */
	public static final NaviHall				WALL		= new NaviHall(
																	"WALL");
	public static final NaviHall				WOOD		= new NaviHall(
																	"WOOD");
	
	private static final Map<String, NaviHall>	STRING_TO_NAVIHALL;
	private static final List<NaviHall>			VALUES;
	
	private NaviHall(String label) {
		super(label);
	}
	
	static {
		final Map<String, NaviHall> mapping = new HashMap<String, NaviHall>();
		
		mapping.put(BLUE.getLabel(), BLUE);
		mapping.put(BRICK.getLabel(), BRICK);
		mapping.put(CEMENT.getLabel(), CEMENT);
		mapping.put(GRASS.getLabel(), GRASS);
		mapping.put(HONEYCOMB.getLabel(), HONEYCOMB);
		mapping.put(ROSE.getLabel(), ROSE);
		mapping.put(STONE.getLabel(), STONE);
		mapping.put(UNKNOWN_H.getLabel(), UNKNOWN_H);
		mapping.put(WALL.getLabel(), WALL);
		mapping.put(WOOD.getLabel(), WOOD);
		mapping.put(HALL.getLabel(), HALL);
		
		STRING_TO_NAVIHALL = Collections.unmodifiableMap(mapping);
		VALUES = Collections.unmodifiableList(new ArrayList<NaviHall>(mapping
				.values()));
	}
	
	public static NaviHall valueOf(String string) {
		if (STRING_TO_NAVIHALL.containsKey(string)) {
			return STRING_TO_NAVIHALL.get(string);
		} else {
			throw new IllegalArgumentException("Unknown NaviHall: " + string);
		}
	}
	
	public static List<NaviHall> values() {
		return VALUES;
	}
	
	@Override
	public boolean isValid(Position position) {
		// Positions that have this hall in the front
		return position.getHorizon0().getFront() == this;
	}
}