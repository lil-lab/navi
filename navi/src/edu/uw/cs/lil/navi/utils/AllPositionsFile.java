package edu.uw.cs.lil.navi.utils;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.xml.sax.SAXException;

import edu.uw.cs.lil.navi.map.NavigationMap;
import edu.uw.cs.lil.navi.map.NavigationMapXMLReader;
import edu.uw.cs.lil.navi.map.Position;

/**
 * Generate the all-positions file used for train annotation tool.
 * 
 * @author Yoav Artzi
 */
public class AllPositionsFile {
	
	private static final Map<String, NavigationMap>	MAPS;
	
	private AllPositionsFile() {
		
	}
	
	static {
		try {
			// Load maps
			final Map<String, NavigationMap> maps = new HashMap<String, NavigationMap>();
			NavigationMap gridMap;
			gridMap = NavigationMapXMLReader.read(new File(
					"resources/maps/map-grid.xml"));
			maps.put(gridMap.getName().toLowerCase(), gridMap);
			
			final NavigationMap lMap = NavigationMapXMLReader.read(new File(
					"resources/maps/map-l.xml"));
			maps.put(lMap.getName().toLowerCase(), lMap);
			
			final NavigationMap jellyMap = NavigationMapXMLReader
					.read(new File("resources/maps/map-jelly.xml"));
			maps.put(jellyMap.getName().toLowerCase(), jellyMap);
			
			MAPS = Collections.unmodifiableMap(maps);
		} catch (final SAXException e) {
			throw new RuntimeException(e);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void main(String[] args) {
		for (final Map.Entry<String, NavigationMap> entry : MAPS.entrySet()) {
			final Map<Integer, Position> numberedPositions = entry.getValue()
					.getNumberedPositions();
			final Map<Position, Integer> inverseNumberedPosition = new HashMap<Position, Integer>();
			for (final Entry<Integer, Position> e : numberedPositions
					.entrySet()) {
				inverseNumberedPosition.put(e.getValue(), e.getKey());
			}
			
			for (final Position p : entry.getValue().getAllPositions()) {
				final StringBuilder sb = new StringBuilder();
				sb.append(entry.getKey()).append('\t').append(p.toString());
				if (p.getForward() != null) {
					sb.append('\t').append("f=").append(p.getForward());
				}
				sb.append('\t').append("l=").append(p.getLeft());
				sb.append('\t').append("r=").append(p.getRight());
				sb.append('\t').append("b=").append(p.getBack());
				if (inverseNumberedPosition.containsKey(p)) {
					sb.append('\t').append("num=")
							.append(inverseNumberedPosition.get(p));
				}
				System.out.println(sb.toString());
			}
		}
		
	}
	
}
