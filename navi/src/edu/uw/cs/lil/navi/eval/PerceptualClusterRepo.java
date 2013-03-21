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
