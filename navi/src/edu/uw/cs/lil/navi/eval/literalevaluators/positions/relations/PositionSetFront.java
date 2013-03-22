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
package edu.uw.cs.lil.navi.eval.literalevaluators.positions.relations;

import java.util.HashSet;
import java.util.Set;

import edu.uw.cs.lil.navi.agent.Agent;
import edu.uw.cs.lil.navi.eval.NaviEvaluationServices;
import edu.uw.cs.lil.navi.eval.literalevaluators.INaviLiteralEvaluator;
import edu.uw.cs.lil.navi.map.Coordinates;
import edu.uw.cs.lil.navi.map.PerceptualCluster;
import edu.uw.cs.lil.navi.map.Pose;
import edu.uw.cs.lil.navi.map.Pose.Direction;
import edu.uw.cs.lil.navi.map.Position;
import edu.uw.cs.lil.navi.map.PositionSet;
import edu.uw.cs.lil.navi.map.PositionSetSingleton;
import edu.uw.cs.lil.navi.map.objects.NaviHall;
import edu.uw.cs.utils.collections.SetUtils;

public class PositionSetFront implements INaviLiteralEvaluator {
	
	@Override
	public boolean agentDependent() {
		return true;
	}
	
	@Override
	public Object evaluate(NaviEvaluationServices services, Object[] args) {
		if (args.length == 2 && args[0] instanceof PositionSet
				&& args[1] instanceof PositionSet) {
			final PositionSet ps1 = (PositionSet) args[0];
			final PositionSet ps2 = (PositionSet) args[1];
			
			if (ps2 instanceof PerceptualCluster && ps2.accept(NaviHall.HALL)) {
				// Special treatment for the case of ps2 being a hallway
				return SetUtils.isIntersecting(ps1, ps2);
			} else {
				// Normal case
				final Set<Coordinates> ps1Coordinates = ps1.getAllCoordinates();
				
				if (ps1Coordinates.contains(services.getCurrentAgentPosition()
						.getPose().getCoordinates())
						&& ps1Coordinates.size() > services
								.getCurrentAgentPositionSet()
								.getAllCoordinates().size()) {
					// Case can't judge front for this object
					return false;
				}
				
				final Set<Position> frontPositions = frontPositions(ps1,
						services.getCurrentAgentPositionSet());
				
				return SetUtils.isIntersecting(ps2, frontPositions);
			}
		} else {
			return null;
		}
	}
	
	private Set<Position> frontPositions(PositionSet ps,
			PositionSetSingleton agentPs) {
		final Set<Position> fronts = new HashSet<Position>();
		
		for (final Position p : ps) {
			final Pose agentPose = agentPs.get().getPose();
			final Pose pose = p.getPose();
			if (agentPose.equalsWithoutOrientation(pose)) {
				// Case the position is same as the current agent position,
				// regardless of the orientation
				fronts.addAll(Agent.getForwardColumn(p, false));
			} else {
				// Case need the relative 'front' of another position. First get
				// the direction of the vector from position to the agent, and
				// then get the visible column.
				final Position orientedPosition = p.getMap().get(
						new Pose(pose.getX(), pose.getY(), Direction
								.fromDegree(Pose.getAngle(pose, agentPose))));
				fronts.addAll(Agent.getForwardColumn(orientedPosition, false));
			}
			
		}
		
		return fronts;
	}
	
}
