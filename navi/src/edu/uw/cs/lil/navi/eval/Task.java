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

import java.util.Map;
import java.util.Map.Entry;

import edu.uw.cs.lil.navi.agent.Agent;
import edu.uw.cs.lil.navi.map.NavigationMap;
import edu.uw.cs.lil.navi.map.Pose;
import edu.uw.cs.lil.navi.map.PositionSet;
import edu.uw.cs.utils.collections.ListUtils;
import edu.uw.cs.utils.log.ILogger;
import edu.uw.cs.utils.log.LoggerFactory;

/**
 * Describes the current task.
 * 
 * @author Yoav Artzi
 */
public class Task {
	public static final String			ID_KEY	= "id";
	public static final ILogger		LOG		= LoggerFactory
														.create(Task.class);
	
	private final Agent					agent;
	/** World map */
	private final NavigationMap			map;
	
	/** Position X -- task end position, all directions are valid */
	private final PositionSet			positionX;
	
	/** Position Y -- the starting position of the task */
	private final PositionSet			positionY;
	
	private final Map<String, String>	properties;
	
	public Task(Agent agent, PositionSet start, PositionSet goal,
			Map<String, String> properties, NavigationMap map) {
		
		// If the instruction set is valid but incorrect (leads to the wrong
		// position), use the specified alternative goal
		if ("False".equals(properties.get("correct"))
				&& "True".equals(properties.get("valid"))) {
			goal = new PositionSet(map
					.get(Pose.valueOf(properties.get("xalt")))
					.getAllOrientations(), false);
			LOG.info("Modified goal for %s: %s -> %s", properties.get(ID_KEY),
					map.get(Integer.valueOf(properties.get("x"))).toString(),
					goal.toString());
		}
		
		this.agent = agent;
		this.positionX = goal;
		this.properties = properties;
		this.map = map;
		this.positionY = start;
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
		final Task other = (Task) obj;
		if (agent == null) {
			if (other.agent != null) {
				return false;
			}
		} else if (!agent.equals(other.agent)) {
			return false;
		}
		if (map == null) {
			if (other.map != null) {
				return false;
			}
		} else if (!map.equals(other.map)) {
			return false;
		}
		if (positionX == null) {
			if (other.positionX != null) {
				return false;
			}
		} else if (!positionX.equals(other.positionX)) {
			return false;
		}
		if (positionY == null) {
			if (other.positionY != null) {
				return false;
			}
		} else if (!positionY.equals(other.positionY)) {
			return false;
		}
		if (properties == null) {
			if (other.properties != null) {
				return false;
			}
		} else if (!properties.equals(other.properties)) {
			return false;
		}
		return true;
	}
	
	public Agent getAgent() {
		return agent;
	}
	
	public NavigationMap getMap() {
		return map;
	}
	
	public PositionSet getPositionX() {
		return positionX;
	}
	
	public PositionSet getPositionY() {
		return positionY;
	}
	
	public String getProperty(String key) {
		return properties.get(key);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((agent == null) ? 0 : agent.hashCode());
		result = prime * result + ((map == null) ? 0 : map.hashCode());
		result = prime * result
				+ ((positionX == null) ? 0 : positionX.hashCode());
		result = prime * result
				+ ((positionY == null) ? 0 : positionY.hashCode());
		result = prime * result
				+ ((properties == null) ? 0 : properties.hashCode());
		return result;
	}
	
	public String propertiesToString() {
		return ListUtils.join(ListUtils.map(properties.entrySet(),
				new ListUtils.Mapper<Map.Entry<String, String>, String>() {
					
					@Override
					public String process(Entry<String, String> obj) {
						return obj.getKey() + "=" + obj.getValue();
					}
				}), "\t");
	}
	
	@Override
	public String toString() {
		return new StringBuilder().append("agent=").append(agent)
				.append(", map=").append(map.getName()).append(", y=")
				.append(positionY).append(", x=").append(positionX)
				.append(", properites=[").append(propertiesToString())
				.append("]").toString();
	}
	
	public Task updateAgent(Agent newAgent) {
		return new Task(newAgent, positionY, positionX, properties, map);
	}
	
}
