/*******************************************************************************
 * Navi. Copyright (C) 2013 Yoav Artzi
 * <p>
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or any later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * <p>
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 ******************************************************************************/
package edu.uw.cs.lil.navi.map.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uw.cs.lil.navi.map.NavigationMap;
import edu.uw.cs.lil.navi.map.Position;

/**
 * Object that may appear in a position in a NAVI map {@link NavigationMap}.
 * <p>
 * NOTE: not using enum here, as its final hashcode() implementation uses the
 * memory address, creating inconsistencies between runs.
 * 
 * @author Yoav Artzi
 */
public class NaviObj extends AbstractNaviItem {
	public static final NaviObj					BARSTOOL	= new NaviObj(
																	"BARSTOOL");
	
	public static final NaviObj					CHAIR		= new NaviObj(
																	"CHAIR");
	
	public static final NaviObj					EASEL		= new NaviObj(
																	"EASEL");
	public static final NaviObj					EMPTY		= new NaviObj(
																	"EMPTY");
	public static final NaviObj					FURNITURE	= new NaviObj(
																	"FURNITURE") {
																@Override
																public boolean isValid(
																		Position position) {
																	return !(EMPTY
																			.isValid(position) || UNKNOWN_O
																			.isValid(position));
																};
															};
	public static final NaviObj					HATRACK		= new NaviObj(
																	"HATRACK");
	public static final NaviObj					LAMP		= new NaviObj(
																	"LAMP");
	public static final NaviObj					SOFA		= new NaviObj(
																	"SOFA");
	public static final NaviObj					UNKNOWN_O	= new NaviObj(
																	"UNKNOWN_O");
	
	private static final Map<String, NaviObj>	STRING_TO_NAVIOBJ;
	private static final List<NaviObj>			VALUES;
	
	public NaviObj(String label) {
		super(label);
	}
	
	static {
		final Map<String, NaviObj> mapping = new HashMap<String, NaviObj>();
		
		mapping.put(BARSTOOL.getLabel(), BARSTOOL);
		mapping.put(CHAIR.getLabel(), CHAIR);
		mapping.put(EASEL.getLabel(), EASEL);
		mapping.put(EMPTY.getLabel(), EMPTY);
		mapping.put(HATRACK.getLabel(), HATRACK);
		mapping.put(LAMP.getLabel(), LAMP);
		mapping.put(SOFA.getLabel(), SOFA);
		mapping.put(FURNITURE.getLabel(), FURNITURE);
		mapping.put(UNKNOWN_O.getLabel(), UNKNOWN_O);
		
		STRING_TO_NAVIOBJ = Collections.unmodifiableMap(mapping);
		VALUES = Collections.unmodifiableList(new ArrayList<NaviObj>(mapping
				.values()));
	}
	
	public static NaviObj valueOf(String string) {
		if ("".equals(string)) {
			return EMPTY;
		} else if (STRING_TO_NAVIOBJ.containsKey(string)) {
			return STRING_TO_NAVIOBJ.get(string);
		} else {
			throw new IllegalArgumentException("Unknown NaviObj: " + string);
		}
	}
	
	public static List<NaviObj> values() {
		return VALUES;
	}
	
	@Override
	public boolean isValid(Position position) {
		return position.getHorizon0().getAt() == this;
	}
}
