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

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import edu.uw.cs.utils.collections.ListUtils;
import edu.uw.cs.utils.collections.SetUtils;
import edu.uw.cs.utils.filter.IFilter;

/**
 * Set of positions that disallows adding new positions. However, it's not
 * completely immutable since it allows removing (pruning) positions.
 * 
 * @author Yoav Artzi
 */
public class PositionSet implements Set<Position> {
	
	public static final PositionSet	EMPTY	= new PositionSet(
													new HashSet<Position>(),
													false);
	private Set<Coordinates>		coordinates;
	private final boolean			hasOrientation;
	private final Set<Position>		positions;
	
	public PositionSet(Set<Position> positions, boolean hasOrientation) {
		this.positions = positions;
		this.hasOrientation = hasOrientation;
		this.coordinates = new HashSet<Coordinates>(ListUtils.map(positions,
				new ListUtils.Mapper<Position, Coordinates>() {
					@Override
					public Coordinates process(Position obj) {
						return obj.getPose().getCoordinates();
					}
				}));
	}
	
	public static double maxDistance(PositionSet ps1, PositionSet ps2) {
		double maxDistance = -Double.MAX_VALUE;
		for (final Position p1 : ps1) {
			for (final Position p2 : ps2) {
				final double distance = Pose.getDistance(p1.getPose(),
						p2.getPose());
				if (distance > maxDistance) {
					maxDistance = distance;
				}
			}
		}
		return maxDistance;
	}
	
	public static double minDistance(PositionSet ps1, PositionSet ps2) {
		double minDistance = Double.MAX_VALUE;
		for (final Position p1 : ps1) {
			for (final Position p2 : ps2) {
				final double distance = Pose.getDistance(p1.getPose(),
						p2.getPose());
				if (distance < minDistance) {
					minDistance = distance;
				}
			}
		}
		return minDistance;
	}
	
	/**
	 * Verifies that all positions in the set fit the item's filter.
	 * 
	 * @param item
	 * @return
	 */
	public boolean accept(IFilter<Position> item) {
		for (final Position position : positions) {
			if (!item.isValid(position)) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public final boolean add(Position e) {
		throw new IllegalAccessError("Adding not allowed");
	}
	
	@Override
	public final boolean addAll(Collection<? extends Position> c) {
		throw new IllegalAccessError("Adding not allowed");
	}
	
	@Override
	public void clear() {
		positions.clear();
	}
	
	@Override
	public boolean contains(Object o) {
		return positions.contains(o);
	}
	
	@Override
	public boolean containsAll(Collection<?> c) {
		return positions.containsAll(c);
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
		final PositionSet other = (PositionSet) obj;
		if (hasOrientation != other.hasOrientation) {
			return false;
		}
		if (positions == null) {
			if (other.positions != null) {
				return false;
			}
		} else if (!positions.equals(other.positions)) {
			return false;
		}
		return true;
	}
	
	public Set<Coordinates> getAllCoordinates() {
		return coordinates;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (hasOrientation ? 1231 : 1237);
		result = prime * result
				+ ((positions == null) ? 0 : positions.hashCode());
		return result;
	}
	
	public boolean hasOrientation() {
		return hasOrientation;
	}
	
	@Override
	public boolean isEmpty() {
		return positions.isEmpty();
	}
	
	public boolean isIntersective(PositionSet other) {
		if (!hasOrientation || !other.hasOrientation) {
			// Case one of the two doesn't care about orientation, just compare
			// the collection of coordinates
			return SetUtils.isIntersecting(coordinates, other.coordinates);
		} else {
			return SetUtils.isIntersecting(positions, other.positions);
		}
	}
	
	@Override
	public Iterator<Position> iterator() {
		return positions.iterator();
	}
	
	@Override
	public boolean remove(Object o) {
		return positions.remove(o);
	}
	
	@Override
	public boolean removeAll(Collection<?> c) {
		return positions.removeAll(c);
	}
	
	@Override
	public boolean retainAll(Collection<?> c) {
		return positions.retainAll(c);
	}
	
	@Override
	public int size() {
		return positions.size();
	}
	
	@Override
	public Object[] toArray() {
		return positions.toArray();
	}
	
	@Override
	public <T> T[] toArray(T[] a) {
		return positions.toArray(a);
	}
	
	@Override
	public String toString() {
		return positions.toString();
	}
	
}
