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
package edu.uw.cs.lil.navi.eval;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.uw.cs.lil.navi.map.PerceptualCluster;

/**
 * Stores perceived PositionSets, allows augmenting PositionSets with new
 * information when identical position sets are added. Used during the
 * construction of the perceptual clusters.
 * 
 * @author Yoav Artzi
 */
public class PerceptualClusterRepo {
	private final Map<PerceptualCluster, PerceptualCluster>	map	= new HashMap<PerceptualCluster, PerceptualCluster>();
	
	public void add(PerceptualCluster ps) {
		if (!map.containsKey(ps)) {
			map.put(ps, ps);
		}
	}
	
	public Set<PerceptualCluster> toSet() {
		return new HashSet<PerceptualCluster>(map.values());
	}
}
