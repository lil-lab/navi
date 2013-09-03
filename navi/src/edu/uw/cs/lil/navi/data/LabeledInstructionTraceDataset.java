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
package edu.uw.cs.lil.navi.data;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.uw.cs.lil.navi.map.NavigationMap;
import edu.uw.cs.lil.tiny.ccg.categories.ICategoryServices;
import edu.uw.cs.lil.tiny.data.collection.IDataCollection;
import edu.uw.cs.utils.collections.ListUtils;
import edu.uw.cs.utils.io.FileUtils;

/**
 * Data set of {@link LabeledInstructionTrace}.
 * 
 * @author Yoav Artzi
 * @param <MR>
 *            Type of meaning representation.
 */
public class LabeledInstructionTraceDataset<MR> implements
		IDataCollection<LabeledInstructionTrace<MR>> {
	
	private final List<LabeledInstructionTrace<MR>>	items;
	
	public LabeledInstructionTraceDataset(
			List<LabeledInstructionTrace<MR>> items) {
		this.items = items;
	}
	
	public static <MR> LabeledInstructionTraceDataset<MR> readFromFile(File f,
			final Map<String, NavigationMap> maps,
			final ICategoryServices<MR> categoryServices) throws IOException {
		final String fileString = FileUtils.readFile(f);
		
		return new LabeledInstructionTraceDataset<MR>(
				Collections.unmodifiableList(ListUtils.map(
						Arrays.asList(fileString.split("\n\n")),
						new ListUtils.Mapper<String, LabeledInstructionTrace<MR>>() {
							private int	counter	= 0;
							
							@Override
							public LabeledInstructionTrace<MR> process(
									String obj) {
								counter++;
								try {
									return LabeledInstructionTrace.parse(obj,
											maps, categoryServices);
								} catch (final Exception e) {
									throw new LabeledInstructionTraceDatasetException(
											e, obj, counter);
								}
							}
						})));
		
	}
	
	@Override
	public Iterator<LabeledInstructionTrace<MR>> iterator() {
		return items.iterator();
	}
	
	@Override
	public int size() {
		return items.size();
	}
	
	@Override
	public String toString() {
		final Iterator<LabeledInstructionTrace<MR>> iterator = items.iterator();
		final StringBuilder sb = new StringBuilder();
		while (iterator.hasNext()) {
			sb.append(iterator.next().toString());
			if (iterator.hasNext()) {
				sb.append("\n\n");
			}
		}
		return sb.toString();
	}
	
}
