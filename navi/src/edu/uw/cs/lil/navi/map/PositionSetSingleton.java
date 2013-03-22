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
package edu.uw.cs.lil.navi.map;

import java.util.HashSet;
import java.util.Set;

/**
 * Set of positions that contains a single position. Hence, a singleton.
 * 
 * @author Yoav Artzi
 */
public class PositionSetSingleton extends PositionSet {
	
	private final Position	position;
	
	private PositionSetSingleton(Position position, Set<Position> singleton) {
		super(singleton, true);
		this.position = position;
	}
	
	public static PositionSetSingleton of(Position position) {
		final Set<Position> singleton = new HashSet<Position>();
		singleton.add(position);
		return new PositionSetSingleton(position, singleton);
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
		final PositionSetSingleton other = (PositionSetSingleton) obj;
		if (position == null) {
			if (other.position != null) {
				return false;
			}
		} else if (!position.equals(other.position)) {
			return false;
		}
		return true;
	}
	
	public Position get() {
		return position;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((position == null) ? 0 : position.hashCode());
		return result;
	}
}
