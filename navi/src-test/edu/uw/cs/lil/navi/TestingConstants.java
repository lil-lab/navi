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
package edu.uw.cs.lil.navi;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import edu.uw.cs.lil.navi.map.NavigationMap;
import edu.uw.cs.lil.navi.map.NavigationMapXMLReader;
import edu.uw.cs.lil.tiny.ccg.categories.ICategoryServices;
import edu.uw.cs.lil.tiny.mr.lambda.FlexibleTypeComparator;
import edu.uw.cs.lil.tiny.mr.lambda.LogicLanguageServices;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.mr.lambda.Ontology;
import edu.uw.cs.lil.tiny.mr.lambda.ccg.LogicalExpressionCategoryServices;
import edu.uw.cs.lil.tiny.mr.language.type.TypeRepository;
import edu.uw.cs.utils.collections.ListUtils;
import edu.uw.cs.utils.log.LogLevel;
import edu.uw.cs.utils.log.Logger;

public class TestingConstants {
	public static final ICategoryServices<LogicalExpression>	CATEGORY_SERVICES;
	public static final Map<String, NavigationMap>				MAPS;
	
	static {
		try {
			LogLevel.setLogLevel(LogLevel.DEV);
			Logger.setSkipPrefix(true);
			
			LogicLanguageServices
					.setInstance(new LogicLanguageServices.Builder(
							new TypeRepository(new File("..",
									"resources/navi.types")))
							.setNumeralTypeName("n")
							.setTypeComparator(new FlexibleTypeComparator())
							.build());
			
			CATEGORY_SERVICES = new LogicalExpressionCategoryServices();
			try {
				// Ontology is currently not used, so we are just reading it,
				// not
				// storing
				new Ontology(ListUtils.createList(new File("..",
						"resources/generic.ont"), new File("..",
						"resources/navi.ont")));
			} catch (final IOException e) {
				throw new RuntimeException(e);
			}
			
			// Load maps
			final Map<String, NavigationMap> maps = new HashMap<String, NavigationMap>();
			final NavigationMap gridMap = NavigationMapXMLReader.read(new File(
					"..", "resources/maps/map-grid.xml"));
			maps.put(gridMap.getName().toLowerCase(), gridMap);
			
			final NavigationMap lMap = NavigationMapXMLReader.read(new File(
					"..", "resources/maps/map-l.xml"));
			maps.put(lMap.getName().toLowerCase(), lMap);
			
			final NavigationMap jellyMap = NavigationMapXMLReader
					.read(new File("..", "resources/maps/map-jelly.xml"));
			maps.put(jellyMap.getName().toLowerCase(), jellyMap);
			
			MAPS = Collections.unmodifiableMap(maps);
			
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}
	
}
