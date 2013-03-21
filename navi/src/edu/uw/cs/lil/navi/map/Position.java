package edu.uw.cs.lil.navi.map;

import java.util.HashSet;
import java.util.Set;

import edu.uw.cs.lil.navi.map.objects.NaviHall;
import edu.uw.cs.lil.navi.map.objects.NaviObj;
import edu.uw.cs.lil.navi.map.objects.NaviWall;

/**
 * Represents a single position in a {@link NavigationMap}. Only the coordinates
 * and the map name determine the identity of this positions, and they are the
 * only one used for comparison and hash code computation.
 * 
 * @author Yoav Artzi
 */
public abstract class Position {
	
	/**
	 * Map name for prototype positions.
	 */
	private static final String		ABSTRACT_LAND			= "ABSTRACT_LAND";
	private static final Position	BASE_PROTOTYPE_POSITION	= new MutablePosition(
																	ABSTRACT_LAND,
																	Pose.PROTOTYPE_POSE,
																	new Horizon.HorizonBuilder()
																			.build());
	private final int				hashCode;
	private final Horizon			horizon0;
	private final String			mapName;
	private final Pose				pose;
	
	private Position(String mapName, Pose pose, Horizon horizon0) {
		this.mapName = mapName;
		this.pose = pose;
		this.horizon0 = horizon0;
		this.hashCode = calcHashCode(mapName, pose);
	}
	
	public static Position createPrototype(Position representative) {
		if (representative.equals(BASE_PROTOTYPE_POSITION)) {
			return representative;
		} else {
			return new PartiallyVisiblePosition(BASE_PROTOTYPE_POSITION,
					representative.horizon0);
		}
	}
	
	private static int calcHashCode(String mapName, Pose coordinates) {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((coordinates == null) ? 0 : coordinates.hashCode());
		result = prime * result + ((mapName == null) ? 0 : mapName.hashCode());
		return result;
	}
	
	@Override
	public final boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Position)) {
			return false;
		}
		final Position other = (Position) obj;
		if (pose == null) {
			if (other.pose != null) {
				return false;
			}
		} else if (!pose.equals(other.pose)) {
			return false;
		}
		if (mapName == null) {
			if (other.mapName != null) {
				return false;
			}
		} else if (!mapName.equals(other.mapName)) {
			return false;
		}
		return true;
	}
	
	public Set<Position> getAllOrientations() {
		final Set<Position> allOrientations = new HashSet<Position>();
		allOrientations.add(getBack());
		allOrientations.add(this);
		allOrientations.add(getLeft());
		allOrientations.add(getRight());
		return allOrientations;
	}
	
	/**
	 * Return the position with the inverse orientation.
	 * 
	 * @return
	 */
	public abstract Position getBack();
	
	/**
	 * Return the position resulting from moving one step forward.
	 * 
	 * @return
	 */
	public abstract Position getForward();
	
	public Position getHallOnlyPosition() {
		return new PartiallyVisiblePosition(this, new Horizon.HorizonBuilder(
				horizon0).setAt(NaviObj.UNKNOWN_O).setLeft(NaviHall.UNKNOWN_H)
				.setRight(NaviHall.UNKNOWN_H).setFrontLeft(NaviWall.UNKNOWN_W)
				.setFrontRight(NaviWall.UNKNOWN_W).build());
	}
	
	public Horizon getHorizon0() {
		return horizon0;
	}
	
	/**
	 * Return the position resulting from turning 90deg to the left.
	 * 
	 * @return
	 */
	public abstract Position getLeft();
	
	public Position getLeftWallOnlyPosition() {
		return new PartiallyVisiblePosition(this, new Horizon.HorizonBuilder(
				horizon0).setFront(NaviHall.UNKNOWN_H)
				.setLeft(NaviHall.UNKNOWN_H).setRight(NaviHall.UNKNOWN_H)
				.setAt(NaviObj.UNKNOWN_O).setFrontRight(NaviWall.UNKNOWN_W)
				.build());
	}
	
	/**
	 * Returns the map that includes this position.
	 * 
	 * @return
	 */
	public abstract NavigationMap getMap();
	
	public Position getObjectOnlyPosition() {
		return new PartiallyVisiblePosition(this, new Horizon.HorizonBuilder(
				horizon0).setFront(NaviHall.UNKNOWN_H)
				.setLeft(NaviHall.UNKNOWN_H).setRight(NaviHall.UNKNOWN_H)
				.setFrontLeft(NaviWall.UNKNOWN_W)
				.setFrontRight(NaviWall.UNKNOWN_W).build());
	}
	
	/**
	 * The coordinates of the location captured by this position.
	 * 
	 * @return
	 */
	public Pose getPose() {
		return pose;
	}
	
	/**
	 * Return the position resulting from turning 90deg to the right.
	 * 
	 * @return
	 */
	public abstract Position getRight();
	
	public Position getRightWallOnlyPosition() {
		return new PartiallyVisiblePosition(this, new Horizon.HorizonBuilder(
				horizon0).setFront(NaviHall.UNKNOWN_H)
				.setLeft(NaviHall.UNKNOWN_H).setRight(NaviHall.UNKNOWN_H)
				.setAt(NaviObj.UNKNOWN_O).setFrontLeft(NaviWall.UNKNOWN_W)
				.build());
	}
	
	public Position getUnknownsOnlyPosition() {
		return new PartiallyVisiblePosition(this, new Horizon.HorizonBuilder(
				horizon0).setFront(NaviHall.UNKNOWN_H)
				.setLeft(NaviHall.UNKNOWN_H).setRight(NaviHall.UNKNOWN_H)
				.setAt(NaviObj.UNKNOWN_O).setFrontLeft(NaviWall.UNKNOWN_W)
				.setFrontRight(NaviWall.UNKNOWN_W).build());
	}
	
	public Position getWallOnlyPosition() {
		return new PartiallyVisiblePosition(this, new Horizon.HorizonBuilder(
				horizon0).setFront(NaviHall.UNKNOWN_H)
				.setLeft(NaviHall.UNKNOWN_H).setRight(NaviHall.UNKNOWN_H)
				.setAt(NaviObj.UNKNOWN_O).build());
	}
	
	@Override
	public final int hashCode() {
		return hashCode;
	}
	
	public boolean isAdjacent(Position other) {
		return pose.getCoordinates().equals(other.pose.getCoordinates());
	}
	
	@Override
	public String toString() {
		return pose.toString();
	}
	
	/**
	 * A mutable position, allowing the modification of its neighbors.
	 * 
	 * @author Yoav Artzi
	 */
	public static class MutablePosition extends Position {
		
		private Position		back	= null;
		private Position		forward	= null;
		private Position		left	= null;
		private NavigationMap	map		= null;
		private Position		right	= null;
		
		public MutablePosition(String mapName, Pose pose, Horizon horizon0) {
			super(mapName, pose, horizon0);
		}
		
		@Override
		public Position getBack() {
			return back;
		}
		
		@Override
		public Position getForward() {
			return forward;
		}
		
		@Override
		public Position getLeft() {
			return left;
		}
		
		@Override
		public NavigationMap getMap() {
			return map;
		}
		
		@Override
		public Position getRight() {
			return right;
		}
		
		public MutablePosition setBack(Position back) {
			this.back = back;
			return this;
		}
		
		public MutablePosition setForward(Position forward) {
			this.forward = forward;
			return this;
		}
		
		public MutablePosition setLeft(Position left) {
			this.left = left;
			return this;
		}
		
		public MutablePosition setMap(NavigationMap map) {
			this.map = map;
			return this;
		}
		
		public MutablePosition setRight(Position right) {
			this.right = right;
			return this;
		}
	}
	
	/**
	 * Position that is only partially observable.
	 * 
	 * @author Yoav Artzi
	 */
	public static class PartiallyVisiblePosition extends Position {
		
		private final Position	position;
		
		public PartiallyVisiblePosition(Position position, Horizon horizon0) {
			super(position.mapName, position.getPose(), horizon0);
			this.position = position instanceof PartiallyVisiblePosition ? ((PartiallyVisiblePosition) position).position
					: position;
		}
		
		@Override
		public Position getBack() {
			return new PartiallyVisiblePosition(position.getBack(),
					position.getBack().horizon0.cloneMasking(getHorizon0()));
		}
		
		@Override
		public Position getForward() {
			if (position.getForward() == null) {
				return null;
			} else {
				return new PartiallyVisiblePosition(position.getForward(),
						position.getForward().horizon0
								.cloneMasking(getHorizon0()));
			}
		}
		
		@Override
		public Position getLeft() {
			return new PartiallyVisiblePosition(position.getLeft(),
					position.getLeft().horizon0.cloneMasking(getHorizon0()));
		}
		
		@Override
		public NavigationMap getMap() {
			return position.getMap();
		}
		
		@Override
		public Position getRight() {
			return new PartiallyVisiblePosition(position.getRight(),
					position.getRight().horizon0.cloneMasking(getHorizon0()));
		}
	}
	
}
