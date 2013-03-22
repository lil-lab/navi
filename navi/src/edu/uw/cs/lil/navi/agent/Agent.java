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
package edu.uw.cs.lil.navi.agent;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import edu.uw.cs.lil.navi.map.Horizon;
import edu.uw.cs.lil.navi.map.Position;
import edu.uw.cs.lil.navi.map.PositionSetSingleton;

/**
 * Represents an agent that can move between positions.
 * 
 * @author Yoav Artzi
 */
public class Agent {
	private final Position	position;
	
	public Agent(Position position) {
		this.position = position;
	}
	
	public static Set<Position> getForwardColumn(Position position) {
		return getForwardColumn(position, true);
	}
	
	/**
	 * Return the column of positions results from moving forward recursively
	 * from this position.
	 * 
	 * @param position
	 * @return
	 */
	public static Set<Position> getForwardColumn(Position position,
			boolean includeCurrent) {
		final Set<Position> positions = new HashSet<Position>();
		Position current;
		if (includeCurrent) {
			current = position;
		} else {
			current = position.getForward();
		}
		while (current != null) {
			positions.add(current);
			if (current != position) {
				positions.add(current.getBack());
				positions.add(current.getLeft());
				positions.add(current.getRight());
			}
			current = current.getForward();
		}
		return positions;
	}
	
	public static Set<Position> getObservedPositions(Position position) {
		final Set<Position> positions = new HashSet<Position>();
		positions.addAll(getForwardColumn(position));
		positions.addAll(getForwardColumn(position.getLeft()));
		positions.addAll(getForwardColumn(position.getRight()));
		// Looking back
		positions.addAll(getForwardColumn(position.getBack()));
		return positions;
	}
	
	/**
	 * Return the sequence of (@link Horizon) visible from the current position.
	 * 
	 * @return
	 */
	public List<Horizon> getObservation() {
		final List<Horizon> observation = new LinkedList<Horizon>();
		Position current = position;
		while (current != null) {
			observation.add(current.getHorizon0());
			current = current.getForward();
		}
		return observation;
	}
	
	public Set<Position> getObservedPositions() {
		return getObservedPositions(position);
	}
	
	public Position getPosition() {
		return position;
	}
	
	public PositionSetSingleton getPositionAsSet() {
		return PositionSetSingleton.of(position);
	}
	
	public Agent moveForward() {
		if (position.getForward() == null) {
			return null;
		} else {
			return new Agent(position.getForward());
		}
	}
	
	@Override
	public String toString() {
		return "A" + position.toString();
	}
	
	public Agent turnLeft() {
		if (position.getLeft() == null) {
			return null;
		} else {
			return new Agent(position.getLeft());
		}
	}
	
	public Agent turnRight() {
		if (position.getRight() == null) {
			return null;
		} else {
			return new Agent(position.getRight());
		}
	}
}
