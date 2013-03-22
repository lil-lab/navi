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

import java.util.Set;

import edu.uw.cs.lil.navi.map.objects.NaviObj;
import edu.uw.cs.lil.navi.map.objects.metaitems.NaviMetaItem;
import edu.uw.cs.utils.filter.IFilter;

/**
 * Captures a cluster of positions that include a single perceived object (e.g.
 * hallway, furniture object, etc.).
 * 
 * @author Yoav Artzi
 */
public class PerceptualCluster extends PositionSet {
	
	private final Position	prototype;
	private final Horizon	prototypeHorizon;
	
	public PerceptualCluster(Set<Position> positions, Position representative,
			boolean hasOrientation) {
		super(positions, hasOrientation);
		this.prototype = Position.createPrototype(representative);
		this.prototypeHorizon = prototype.getHorizon0();
	}
	
	@Override
	public boolean accept(IFilter<Position> item) {
		if (item instanceof NaviMetaItem) {
			// Only allow filtering as a meta item if this cluster has an object
			// (incl. EMPTY). This means only objects (and empty locations) can
			// be identified as meta items.
			return prototype.getHorizon0().getAt() != NaviObj.UNKNOWN_O
					&& super.accept(item);
		} else {
			return item.isValid(prototype);
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final PerceptualCluster other = (PerceptualCluster) obj;
		if (prototype == null) {
			if (other.prototype != null) {
				return false;
			}
		} else if (!prototype.equals(other.prototype)) {
			return false;
		}
		if (prototypeHorizon == null) {
			if (other.prototypeHorizon != null) {
				return false;
			}
		} else if (!prototypeHorizon.equals(other.prototypeHorizon)) {
			return false;
		}
		return true;
	}
	
	public Position getPrototype() {
		return prototype;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((prototype == null) ? 0 : prototype.hashCode());
		result = prime
				* result
				+ ((prototypeHorizon == null) ? 0 : prototypeHorizon.hashCode());
		return result;
	}
	
	@Override
	public String toString() {
		return super.toString() + prototype.getHorizon0();
	}
	
}
