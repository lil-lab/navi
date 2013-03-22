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

import edu.uw.cs.lil.navi.map.objects.NaviHall;
import edu.uw.cs.lil.navi.map.objects.NaviObj;
import edu.uw.cs.lil.navi.map.objects.NaviWall;

/**
 * Represent a single horizon, capturing the observation of a single coordinate.
 * 
 * @author Yoav Artzi
 */
public class Horizon {
	private final NaviObj	at;
	private final NaviWall	frontLeft, frontRight;
	private final NaviHall	left, right, front;
	
	private Horizon(NaviHall left, NaviObj at, NaviHall right,
			NaviWall frontLeft, NaviHall front, NaviWall frontRight) {
		this.left = left;
		this.at = at;
		this.right = right;
		this.frontLeft = frontLeft;
		this.front = front;
		this.frontRight = frontRight;
	}
	
	public Horizon cloneMasking(Horizon other) {
		return new Horizon(
				other.left == NaviHall.UNKNOWN_H ? NaviHall.UNKNOWN_H : left,
				other.at == NaviObj.UNKNOWN_O ? NaviObj.UNKNOWN_O : at,
				other.right == NaviHall.UNKNOWN_H ? NaviHall.UNKNOWN_H : right,
				other.frontLeft == NaviWall.UNKNOWN_W ? NaviWall.UNKNOWN_W
						: frontLeft,
				other.front == NaviHall.UNKNOWN_H ? NaviHall.UNKNOWN_H : front,
				other.frontRight == NaviWall.UNKNOWN_W ? NaviWall.UNKNOWN_W
						: frontRight);
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
		final Horizon other = (Horizon) obj;
		if (at == null) {
			if (other.at != null) {
				return false;
			}
		} else if (!at.equals(other.at)) {
			return false;
		}
		if (front == null) {
			if (other.front != null) {
				return false;
			}
		} else if (!front.equals(other.front)) {
			return false;
		}
		if (frontLeft == null) {
			if (other.frontLeft != null) {
				return false;
			}
		} else if (!frontLeft.equals(other.frontLeft)) {
			return false;
		}
		if (frontRight == null) {
			if (other.frontRight != null) {
				return false;
			}
		} else if (!frontRight.equals(other.frontRight)) {
			return false;
		}
		if (left == null) {
			if (other.left != null) {
				return false;
			}
		} else if (!left.equals(other.left)) {
			return false;
		}
		if (right == null) {
			if (other.right != null) {
				return false;
			}
		} else if (!right.equals(other.right)) {
			return false;
		}
		return true;
	}
	
	public NaviObj getAt() {
		return at;
	}
	
	public NaviHall getFront() {
		return front;
	}
	
	public NaviWall getFrontLeft() {
		return frontLeft;
	}
	
	public NaviWall getFrontRight() {
		return frontRight;
	}
	
	public NaviHall getLeft() {
		return left;
	}
	
	public NaviHall getRight() {
		return right;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((at == null) ? 0 : at.hashCode());
		result = prime * result + ((front == null) ? 0 : front.hashCode());
		result = prime * result
				+ ((frontLeft == null) ? 0 : frontLeft.hashCode());
		result = prime * result
				+ ((frontRight == null) ? 0 : frontRight.hashCode());
		result = prime * result + ((left == null) ? 0 : left.hashCode());
		result = prime * result + ((right == null) ? 0 : right.hashCode());
		return result;
	}
	
	public Horizon merge(Horizon other) {
		
		final HorizonBuilder builder = new HorizonBuilder(this);
		
		if (left != other.left) {
			if (left == NaviHall.UNKNOWN_H) {
				builder.setLeft(other.left);
			} else if (other.left != NaviHall.UNKNOWN_H) {
				throw new IllegalArgumentException(
						"Conflict between merged horizons: " + this + " + "
								+ other);
			}
		}
		
		if (at != other.at) {
			if (at == NaviObj.UNKNOWN_O) {
				builder.setAt(other.at);
			} else if (other.at != NaviObj.UNKNOWN_O) {
				throw new IllegalArgumentException(
						"Conflict between merged horizons: " + this + " + "
								+ other);
			}
		}
		
		if (right != other.right) {
			if (right == NaviHall.UNKNOWN_H) {
				builder.setRight(other.right);
			} else if (other.right != NaviHall.UNKNOWN_H) {
				throw new IllegalArgumentException(
						"Conflict between merged horizons: " + this + " + "
								+ other);
			}
		}
		
		if (frontLeft != other.frontLeft) {
			if (frontLeft == NaviWall.UNKNOWN_W) {
				builder.setFrontLeft(other.frontLeft);
			} else if (other.frontLeft != NaviWall.UNKNOWN_W) {
				throw new IllegalArgumentException(
						"Conflict between merged horizons: " + this + " + "
								+ other);
			}
		}
		
		if (front != other.front) {
			if (front == NaviHall.UNKNOWN_H) {
				builder.setFront(other.front);
			} else if (other.front != NaviHall.UNKNOWN_H) {
				throw new IllegalArgumentException(
						"Conflict between merged horizons: " + this + " + "
								+ other);
			}
		}
		
		if (frontRight != other.frontRight) {
			if (frontRight == NaviWall.UNKNOWN_W) {
				builder.setFrontRight(other.frontRight);
			} else if (other.frontRight != NaviWall.UNKNOWN_W) {
				throw new IllegalArgumentException(
						"Conflict between merged horizons: " + this + " + "
								+ other);
			}
		}
		
		return builder.build();
	}
	
	@Override
	public String toString() {
		return String.format("{%s, %s, %s, %s, %s, %s}", left, at, right,
				frontLeft, front, frontRight);
	}
	
	public static class HorizonBuilder {
		private NaviObj	at	= NaviObj.EMPTY;
		private NaviWall	frontLeft	= NaviWall.END,
				frontRight = NaviWall.END;
		private NaviHall	left		= NaviHall.WALL, right = NaviHall.WALL,
				front = NaviHall.WALL;
		
		public HorizonBuilder() {
		}
		
		public HorizonBuilder(Horizon other) {
			this.at = other.at;
			this.front = other.front;
			this.right = other.right;
			this.left = other.left;
			this.frontLeft = other.frontLeft;
			this.frontRight = other.frontRight;
		}
		
		public Horizon build() {
			return new Horizon(left, at, right, frontLeft, front, frontRight);
		}
		
		public HorizonBuilder setAt(NaviObj at) {
			this.at = at;
			return this;
		}
		
		public HorizonBuilder setFront(NaviHall front) {
			this.front = front;
			return this;
		}
		
		public HorizonBuilder setFrontLeft(NaviWall frontLeft) {
			this.frontLeft = frontLeft;
			return this;
		}
		
		public HorizonBuilder setFrontRight(NaviWall frontRight) {
			this.frontRight = frontRight;
			return this;
		}
		
		public HorizonBuilder setLeft(NaviHall left) {
			this.left = left;
			return this;
		}
		
		public HorizonBuilder setRight(NaviHall right) {
			this.right = right;
			return this;
		}
	}
	
}
