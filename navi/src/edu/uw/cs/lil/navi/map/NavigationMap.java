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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.uw.cs.lil.navi.map.Position.MutablePosition;

/**
 * Represents a navigation map.
 * 
 * @author Yoav Artzi
 */
public class NavigationMap {
	private final Set<Position>				allPositions;
	private final String					name;
	private final Map<Integer, Position>	numberedPositions;
	
	private final Map<Pose, Position>		positions;
	
	public NavigationMap(String name, Set<MutablePosition> positions,
			Map<Integer, Position> numberedPositions) {
		this.name = name;
		this.numberedPositions = numberedPositions;
		final Map<Pose, Position> mutablePositions = new HashMap<Pose, Position>();
		for (final MutablePosition position : positions) {
			position.setMap(this);
			mutablePositions.put(position.getPose(), position);
		}
		this.positions = Collections.unmodifiableMap(mutablePositions);
		this.allPositions = Collections.unmodifiableSet(new HashSet<Position>(
				this.positions.values()));
	}
	
	public boolean containsPosition(Pose coordinates) {
		return positions.containsKey(coordinates);
	}
	
	public Set<Pose> coordinatesSet() {
		return Collections.unmodifiableSet(positions.keySet());
	}
	
	public Position get(int number) {
		return numberedPositions.get(number);
	}
	
	public Position get(int x, int y, Pose.Direction direction) {
		return get(new Pose(x, y, direction));
	}
	
	public Position get(Pose coordinates) {
		return positions.get(coordinates);
	}
	
	public Set<Pose> getAllPoses() {
		return Collections.unmodifiableSet(positions.keySet());
	}
	
	public Set<Position> getAllPositions() {
		return allPositions;
	}
	
	public String getName() {
		return name;
	}
	
	public Map<Integer, Position> getNumberedPositions() {
		return Collections.unmodifiableMap(numberedPositions);
	}
	
	public Set<Position> positionSet() {
		return Collections.unmodifiableSet(new HashSet<Position>(positions
				.values()));
	}
	
}
