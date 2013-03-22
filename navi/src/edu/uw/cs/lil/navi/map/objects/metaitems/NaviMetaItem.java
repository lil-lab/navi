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
package edu.uw.cs.lil.navi.map.objects.metaitems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uw.cs.lil.navi.map.objects.AbstractNaviItem;

/**
 * Complex meta objects.
 * <p>
 * NOTE: not using enum here, as its final hashcode() implementation uses the
 * memory address, creating inconsistencies between runs.
 * 
 * @author Yoav Artzi
 */
public abstract class NaviMetaItem extends AbstractNaviItem {
	public static final NaviMetaItem				CORNER			= Corner.INSTANCE;
	public static final NaviMetaItem				DEADEND			= Deadend.INSTANCE;
	public static final NaviMetaItem				INTERSECTION	= Intersection.INSTANCE;
	public static final NaviMetaItem				T_INTERSECTION	= TIntersection.INSTANCE;
	private static final Map<String, NaviMetaItem>	STRING_TO_NAVIMETAITEM;
	
	private static final List<NaviMetaItem>			VALUES;
	
	protected NaviMetaItem(String label) {
		super(label);
	}
	
	static {
		final Map<String, NaviMetaItem> mapping = new HashMap<String, NaviMetaItem>();
		
		mapping.put(CORNER.getLabel(), CORNER);
		mapping.put(DEADEND.getLabel(), DEADEND);
		mapping.put(INTERSECTION.getLabel(), INTERSECTION);
		mapping.put(T_INTERSECTION.getLabel(), T_INTERSECTION);
		
		STRING_TO_NAVIMETAITEM = Collections.unmodifiableMap(mapping);
		VALUES = Collections.unmodifiableList(new ArrayList<NaviMetaItem>(
				mapping.values()));
	}
	
	public static NaviMetaItem valueOf(String string) {
		if (STRING_TO_NAVIMETAITEM.containsKey(string)) {
			return STRING_TO_NAVIMETAITEM.get(string);
		} else {
			throw new IllegalArgumentException("Unknown NaviMetaItem: "
					+ string);
		}
	}
	
	public static List<NaviMetaItem> values() {
		return VALUES;
	}
}
