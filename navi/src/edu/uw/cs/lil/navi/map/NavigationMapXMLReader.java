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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import edu.uw.cs.lil.navi.map.Horizon.HorizonBuilder;
import edu.uw.cs.lil.navi.map.Pose.Direction;
import edu.uw.cs.lil.navi.map.Position.MutablePosition;
import edu.uw.cs.lil.navi.map.objects.NaviHall;
import edu.uw.cs.lil.navi.map.objects.NaviObj;
import edu.uw.cs.lil.navi.map.objects.NaviWall;
import edu.uw.cs.utils.composites.Pair;

public class NavigationMapXMLReader {
	
	public static NavigationMap read(File file) throws SAXException,
			IOException {
		final XMLReader reader = XMLReaderFactory.createXMLReader();
		final Handler handler = new Handler();
		reader.setContentHandler(handler);
		reader.setErrorHandler(handler);
		reader.parse(new InputSource(new FileInputStream(file)));
		
		// Create all horizon builders and coordinates for positions
		final Map<Pose, HorizonBuilder> horizonBuilders = new HashMap<Pose, HorizonBuilder>();
		for (final Entry<Pose, NaviObj> entry : handler.getPositions()
				.entrySet()) {
			final Pose rawCoordinates = entry.getKey();
			final NaviObj item = entry.getValue();
			for (final Direction direction : Direction.values()) {
				final Pose coordinates = new Pose(
						rawCoordinates.getX(), rawCoordinates.getY(), direction);
				horizonBuilders.put(coordinates,
						new HorizonBuilder().setAt(item));
			}
		}
		
		// Set observations on all horizons
		for (final Entry<Pair<Pose, Pose>, Pair<NaviWall, NaviHall>> entry : handler
				.getHalls().entrySet()) {
			final Pose node1 = entry.getKey().first();
			final Pose node2 = entry.getKey().second();
			final NaviWall wall = entry.getValue().first();
			final NaviHall hall = entry.getValue().second();
			
			// From node1 to node2
			final Direction direction1 = handler.inferDirection(node1, node2);
			// Facing in the direction of the hallway
			horizonBuilders
					.get(new Pose(node1.getX(), node1.getY(), direction1))
					.setFront(hall).setFrontLeft(wall).setFrontRight(wall);
			// The same coordinates, facing left
			horizonBuilders.get(
					new Pose(node1.getX(), node1.getY(), direction1
							.left())).setRight(hall);
			// The same coordinates, facing right
			horizonBuilders.get(
					new Pose(node1.getX(), node1.getY(), direction1
							.right())).setLeft(hall);
			
			// From node2 to node1
			final Direction direction2 = handler.inferDirection(node2, node1);
			// Facing in the direction of the hallway
			horizonBuilders
					.get(new Pose(node2.getX(), node2.getY(), direction2))
					.setFront(hall).setFrontLeft(wall).setFrontRight(wall);
			// The same coordinates, facing left
			horizonBuilders.get(
					new Pose(node2.getX(), node2.getY(), direction2
							.left())).setRight(hall);
			// The same coordinates, facing right
			horizonBuilders.get(
					new Pose(node2.getX(), node2.getY(), direction2
							.right())).setLeft(hall);
		}
		
		// Create all the positions
		final Map<Pose, MutablePosition> positions = new HashMap<Pose, MutablePosition>();
		for (final Entry<Pose, HorizonBuilder> entry : horizonBuilders
				.entrySet()) {
			positions.put(entry.getKey(),
					new MutablePosition(handler.getName(), entry.getKey(),
							entry.getValue().build()));
		}
		
		// Connect positions that differ only by direction
		for (final MutablePosition position : positions.values()) {
			final Pose coordinates = position.getPose();
			final Direction direction = coordinates.getDirection();
			position.setLeft(positions.get(new Pose(coordinates.getX(),
					coordinates.getY(), direction.left())));
			position.setRight(positions.get(new Pose(coordinates.getX(),
					coordinates.getY(), direction.right())));
			position.setBack(positions.get(new Pose(coordinates.getX(),
					coordinates.getY(), direction.back())));
		}
		// Connect positions that are connected through hallways
		for (final Entry<Pair<Pose, Pose>, Pair<NaviWall, NaviHall>> entry : handler
				.getHalls().entrySet()) {
			final Pose node1 = entry.getKey().first();
			final Pose node2 = entry.getKey().second();
			final Direction direction1 = handler.inferDirection(node1, node2);
			final Direction direction2 = handler.inferDirection(node2, node1);
			
			final MutablePosition p1 = positions.get(new Pose(node1
					.getX(), node1.getY(), direction1));
			final MutablePosition p2 = positions.get(new Pose(node2
					.getX(), node2.getY(), direction2));
			
			p1.setForward(positions.get(new Pose(node2.getX(), node2
					.getY(), direction1)));
			p2.setForward(positions.get(new Pose(node1.getX(), node1
					.getY(), direction2)));
		}
		
		// Create mapping of IDs to positions
		final Map<Integer, Position> positionsIds = new HashMap<Integer, Position>();
		for (final Entry<Pose, Integer> entry : handler.getPositionIds()
				.entrySet()) {
			// Positions haev no direction value in MARCO, so we default to D0
			positionsIds.put(entry.getValue(), positions
					.get(new Pose(entry.getKey().getX(), entry.getKey()
							.getY(), Direction.D0)));
		}
		
		return new NavigationMap(handler.getName(),
				new HashSet<MutablePosition>(positions.values()), positionsIds);
	}
	
	private static class Handler extends DefaultHandler {
		private final Map<Pair<Pose, Pose>, Pair<NaviWall, NaviHall>>	halls		= new HashMap<Pair<Pose, Pose>, Pair<NaviWall, NaviHall>>();
		private String																name		= null;
		private final Map<Pose, Integer>										positionIds	= new HashMap<Pose, Integer>();
		private final Map<Pose, NaviObj>										positions	= new HashMap<Pose, NaviObj>();
		private Direction															xminus		= null;
		private Direction															xplus		= null;
		private Direction															yminus		= null;
		private Direction															yplus		= null;
		
		private static NaviHall stringToHall(String string) {
			if ("blue".equals(string)) {
				return NaviHall.BLUE;
			} else if ("brick".equals(string)) {
				return NaviHall.BRICK;
			} else if ("concrete".equals(string)) {
				return NaviHall.CEMENT;
			} else if ("yellow".equals(string)) {
				return NaviHall.HONEYCOMB;
			} else if ("grass".equals(string)) {
				return NaviHall.GRASS;
			} else if ("gravel".equals(string)) {
				return NaviHall.STONE;
			} else if ("wood".equals(string)) {
				return NaviHall.WOOD;
			} else if ("flower".equals(string)) {
				return NaviHall.ROSE;
			} else {
				return null;
			}
		}
		
		private static NaviWall stringToWall(String string) {
			if ("tower".equals(string)) {
				return NaviWall.EIFFEL;
			} else if ("fish".equals(string)) {
				return NaviWall.FISH;
			} else if ("butterfly".equals(string)) {
				return NaviWall.BUTTERFLY;
			} else {
				return null;
			}
		}
		
		public Map<Pair<Pose, Pose>, Pair<NaviWall, NaviHall>> getHalls() {
			return halls;
		}
		
		public String getName() {
			return name;
		}
		
		public Map<Pose, Integer> getPositionIds() {
			return positionIds;
		}
		
		public Map<Pose, NaviObj> getPositions() {
			return positions;
		}
		
		public Direction inferDirection(Pose source, Pose target) {
			if (Math.abs(source.getX() - target.getX())
					+ Math.abs(source.getY() - target.getY()) == 1) {
				if (source.getX() - target.getX() == 1) {
					return xminus;
				} else if (source.getX() - target.getX() == -1) {
					return xplus;
				} else if (source.getY() - target.getY() == 1) {
					return yminus;
				} else if (source.getY() - target.getY() == -1) {
					return yplus;
				} else {
					return null;
				}
			} else {
				// Case not neighbors, return null
				return null;
			}
		}
		
		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			if ("map".equals(qName)) {
				xplus = Direction.fromInteger(Integer.valueOf(attributes
						.getValue("xplus")));
				yplus = Direction.fromInteger(Integer.valueOf(attributes
						.getValue("yplus")));
				xminus = Direction.fromInteger(Integer.valueOf(attributes
						.getValue("xminus")));
				yminus = Direction.fromInteger(Integer.valueOf(attributes
						.getValue("yminus")));
				name = attributes.getValue("name");
			} else if ("node".equals(qName)) {
				final Pose coordinates = new Pose(
						Integer.valueOf(attributes.getValue("x")),
						Integer.valueOf(attributes.getValue("y")), null);
				final Integer id = attributes.getValue("id") == null ? null
						: Integer.valueOf(attributes.getValue("id"));
				final NaviObj item = "".equals(attributes.getValue("item")) ? NaviObj.EMPTY
						: NaviObj.valueOf(attributes.getValue("item")
								.toUpperCase());
				
				if (id != null) {
					// Case the position has an ID
					positionIds.put(coordinates, id);
				}
				
				// Record item
				positions.put(coordinates, item);
			} else if ("edge".equals(qName)) {
				final String[] node1split = attributes.getValue("node1").split(
						",");
				final Pose node1 = new Pose(
						Integer.valueOf(node1split[0]),
						Integer.valueOf(node1split[1]), null);
				final String[] node2split = attributes.getValue("node2").split(
						",");
				final Pose node2 = new Pose(
						Integer.valueOf(node2split[0]),
						Integer.valueOf(node2split[1]), null);
				final NaviWall wall = stringToWall(attributes.getValue("wall"));
				final NaviHall floor = stringToHall(attributes
						.getValue("floor"));
				
				halls.put(Pair.of(node1, node2), Pair.of(wall, floor));
			} else {
				throw new RuntimeException("Unexpected XML entity: " + qName);
			}
		}
	}
}
