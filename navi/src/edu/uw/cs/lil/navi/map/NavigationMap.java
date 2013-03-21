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
