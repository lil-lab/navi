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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jregex.Matcher;
import jregex.Pattern;

/**
 * Represents a specific location and orientation.
 * 
 * @author Yoav Artzi
 */
public class Pose {
	public static final Pose		PROTOTYPE_POSE	= new Pose(-1, -1,
															Direction.D0);
	
	private static final Pattern	STRING_PATTERN	= new Pattern(
															"\\(([0-9]+),\\s*([0-9]+),\\s*(0|90|180|270)\\)");
	
	private final Coordinates		coordinates;
	
	private final Direction			direction;
	
	public Pose(Coordinates coordinates, Direction direction) {
		this.coordinates = coordinates;
		this.direction = direction;
	}
	
	public Pose(int x, int y, Direction direction) {
		this.coordinates = new Coordinates(x, y);
		this.direction = direction;
	}
	
	/**
	 * Return the angle between the two coordinates, as if there's a line
	 * between them. Regardless of the orientation of the given coordinates.
	 * 
	 * @param source
	 * @param target
	 * @return
	 */
	public static double getAngle(Pose source, Pose target) {
		if (source.getX() == target.getX() && source.getY() == target.getY()) {
			// If the positions are identical as far as X and Y, just return the
			// angle of target
			return target.getDirection().getDegrees();
		} else {
			final double deg = Math.toDegrees(Math.atan2(
					target.getY() - source.getY(),
					target.getX() - source.getX())
					+ Math.PI / 2.0);
			return deg >= 0 ? deg : deg + 360.0;
		}
	}
	
	/**
	 * Returns the euclidean distance between two coordinates.
	 * 
	 * @param p1
	 * @param p2
	 * @return
	 */
	public static double getDistance(Pose p1, Pose p2) {
		return Math.sqrt(Math.pow(p1.getX() - p2.getX(), 2.0)
				+ Math.pow(p1.getY() - p2.getY(), 2.0));
	}
	
	public static void main(String[] args) {
		final Pose c = new Pose(0, 5, Direction.D90);
		System.out.println(Pose.valueOf(c.toString()));
		
	}
	
	/**
	 * Parse a string into a coordinates object.
	 * 
	 * @param string
	 * @return
	 */
	public static Pose valueOf(String string) {
		final Matcher matcher = STRING_PATTERN.matcher(string);
		if (!matcher.matches()) {
			return null;
		}
		final String x = matcher.group(1);
		final String y = matcher.group(2);
		final String dir = matcher.group(3);
		if (x == null || y == null || dir == null) {
			return null;
		} else {
			return new Pose(Integer.valueOf(x), Integer.valueOf(y),
					Direction.fromInteger(Integer.valueOf(dir)));
		}
		
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
		final Pose other = (Pose) obj;
		if (coordinates == null) {
			if (other.coordinates != null) {
				return false;
			}
		} else if (!coordinates.equals(other.coordinates)) {
			return false;
		}
		if (direction != other.direction) {
			return false;
		}
		return true;
	}
	
	public boolean equalsWithoutOrientation(Pose other) {
		return coordinates.equals(other.coordinates);
	}
	
	public Coordinates getCoordinates() {
		return coordinates;
	}
	
	public Direction getDirection() {
		return direction;
	}
	
	public int getX() {
		return coordinates.getX();
	}
	
	public int getY() {
		return coordinates.getY();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((coordinates == null) ? 0 : coordinates.hashCode());
		result = prime * result
				+ ((direction == null) ? 0 : direction.hashCode());
		return result;
	}
	
	@Override
	public String toString() {
		return String.format("(%d, %d, %s)", coordinates.getX(),
				coordinates.getY(), direction);
	}
	
	public static class Direction {
		public static final Direction				D0		= new Direction(0);
		public static final Direction				D180	= new Direction(180);
		public static final Direction				D270	= new Direction(270);
		
		public static final Direction				D90		= new Direction(90);
		
		private static final Map<String, Direction>	STRING_MAPPING;
		private static final List<Direction>		VALUES;
		
		private final int							degrees;
		
		private Direction(int directionNum) {
			this.degrees = directionNum;
		}
		
		static {
			final Map<String, Direction> mapping = new HashMap<String, Pose.Direction>();
			
			mapping.put(Integer.toString(D0.degrees), D0);
			mapping.put(Integer.toString(D180.degrees), D180);
			mapping.put(Integer.toString(D270.degrees), D270);
			mapping.put(Integer.toString(D90.degrees), D90);
			
			STRING_MAPPING = Collections.unmodifiableMap(mapping);
			VALUES = Collections.unmodifiableList(new ArrayList<Direction>(
					mapping.values()));
		}
		
		public static Direction back(Direction direction) {
			if (direction == D0) {
				return D180;
			} else if (direction == D180) {
				return D0;
			} else if (direction == D270) {
				return D90;
			} else if (direction == D90) {
				return D270;
			} else {
				throw new IllegalStateException("unhandled case");
			}
		}
		
		/**
		 * Given a degree, will return the closes direction.
		 * 
		 * @param degree
		 * @return
		 */
		public static Direction fromDegree(double degree) {
			return fromInteger((int) ((Math.round(degree / 90.0) % 4) * 90.0));
		}
		
		public static Direction fromInteger(int i) {
			return Direction.valueOf(String.format("%d", i));
		}
		
		public static Direction left(Direction direction) {
			if (direction == D0) {
				return D270;
			} else if (direction == D180) {
				return D90;
			} else if (direction == D270) {
				return D180;
			} else if (direction == D90) {
				return D0;
			} else {
				throw new IllegalStateException("unhandled case");
			}
		}
		
		public static Direction right(Direction direction) {
			if (direction == D0) {
				return D90;
			} else if (direction == D180) {
				return D270;
			} else if (direction == D270) {
				return D0;
			} else if (direction == D90) {
				return D180;
			} else {
				throw new IllegalStateException("unhandled case");
			}
		}
		
		public static Direction valueOf(String string) {
			return STRING_MAPPING.get(string);
		}
		
		public static List<Direction> values() {
			return VALUES;
		}
		
		public Direction back() {
			return back(this);
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
			final Direction other = (Direction) obj;
			if (degrees != other.degrees) {
				return false;
			}
			return true;
		}
		
		public int getDegrees() {
			return degrees;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + degrees;
			return result;
		}
		
		public Direction left() {
			return left(this);
		}
		
		public Direction right() {
			return right(this);
		}
		
		@Override
		public String toString() {
			return String.valueOf(degrees);
		}
	}
}
