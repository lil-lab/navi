package edu.uw.cs.lil.navi.map.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uw.cs.lil.navi.map.NavigationMap;
import edu.uw.cs.lil.navi.map.Position;

/**
 * Wallpaper pictures in a NAVI map {@link NavigationMap}.
 * <p>
 * NOTE: not using enum here, as its final hashcode() implementation uses the
 * memory address, creating inconsistencies between runs.
 * 
 * @author Yoav Artzi
 */
public class NaviWall extends AbstractNaviItem {
	public static final NaviWall				BUTTERFLY	= new NaviWall(
																	"BUTTERFLY");
	public static final NaviWall				EIFFEL		= new NaviWall(
																	"EIFFEL");
	public static final NaviWall				END			= new NaviWall(
																	"END");
	public static final NaviWall				FISH		= new NaviWall(
																	"FISH");
	public static final NaviWall				UNKNOWN_W	= new NaviWall(
																	"UNKNOWN_W");
	
	private static final Map<String, NaviWall>	STRING_TO_NAVIWALL;
	private static final List<NaviWall>			VALUES;
	
	public NaviWall(String label) {
		super(label);
	}
	
	static {
		final Map<String, NaviWall> mapping = new HashMap<String, NaviWall>();
		
		mapping.put(BUTTERFLY.getLabel(), BUTTERFLY);
		mapping.put(EIFFEL.getLabel(), EIFFEL);
		mapping.put(END.getLabel(), END);
		mapping.put(FISH.getLabel(), FISH);
		mapping.put(UNKNOWN_W.getLabel(), UNKNOWN_W);
		
		STRING_TO_NAVIWALL = Collections.unmodifiableMap(mapping);
		VALUES = Collections.unmodifiableList(new ArrayList<NaviWall>(mapping
				.values()));
	}
	
	public static NaviWall valueOf(String string) {
		if (STRING_TO_NAVIWALL.containsKey(string)) {
			return STRING_TO_NAVIWALL.get(string);
		} else {
			throw new IllegalArgumentException("Unknown NaviWall: " + string);
		}
	}
	
	public static List<NaviWall> values() {
		return VALUES;
	}
	
	@Override
	public boolean isValid(Position position) {
		return position.getHorizon0().getFrontLeft() == this
				|| position.getHorizon0().getFrontRight() == this;
	}
}