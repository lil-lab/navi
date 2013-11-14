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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.uw.cs.lil.navi.experiments.plat.NaviExperiment;
import edu.uw.cs.lil.navi.map.NavigationMap;
import edu.uw.cs.lil.tiny.ccg.categories.ICategoryServices;
import edu.uw.cs.lil.tiny.data.collection.IDataCollection;
import edu.uw.cs.lil.tiny.explat.IResourceRepository;
import edu.uw.cs.lil.tiny.explat.ParameterizedExperiment.Parameters;
import edu.uw.cs.lil.tiny.explat.resources.IResourceObjectCreator;
import edu.uw.cs.lil.tiny.explat.resources.usage.ResourceUsage;
import edu.uw.cs.utils.collections.ListUtils;
import edu.uw.cs.utils.io.FileUtils;

/**
 * Data set of {@link LabeledInstructionSeqTrace}.
 * 
 * @author Yoav Artzi
 * @param <MR>
 */
public class LabeledInstructionSeqTraceDataset<MR> implements
		IDataCollection<LabeledInstructionSeqTrace<MR>> {
	private final List<LabeledInstructionSeqTrace<MR>>	items;
	
	public LabeledInstructionSeqTraceDataset(
			List<LabeledInstructionSeqTrace<MR>> items) {
		this.items = items;
	}
	
	public static <MR> LabeledInstructionSeqTraceDataset<MR> readFromFile(
			File f, final Map<String, NavigationMap> maps,
			final ICategoryServices<MR> categoryServices) throws IOException {
		final String fileString = FileUtils.readFile(f);
		
		return new LabeledInstructionSeqTraceDataset<MR>(
				Collections.unmodifiableList(Collections.unmodifiableList(ListUtils.map(
						Arrays.asList(fileString.replaceAll("//.*\n", "")
								.split("\n\n")),
						new ListUtils.Mapper<String, LabeledInstructionSeqTrace<MR>>() {
							private int	counter	= 0;
							
							@Override
							public LabeledInstructionSeqTrace<MR> process(
									String obj) {
								counter++;
								try {
									return LabeledInstructionSeqTrace.parse(
											obj, maps, categoryServices);
								} catch (final Exception e) {
									throw new InstructionTraceDatasetException(
											e, obj, counter);
								}
							}
						}))));
		
	}
	
	@Override
	public Iterator<LabeledInstructionSeqTrace<MR>> iterator() {
		return items.iterator();
	}
	
	@Override
	public int size() {
		return items.size();
	}
	
	@Override
	public String toString() {
		final Iterator<LabeledInstructionSeqTrace<MR>> iterator = items
				.iterator();
		final StringBuilder sb = new StringBuilder();
		while (iterator.hasNext()) {
			sb.append(iterator.next().toString());
			if (iterator.hasNext()) {
				sb.append("\n\n");
			}
		}
		return sb.toString();
	}
	
	public static class Creator<MR> implements
			IResourceObjectCreator<LabeledInstructionSeqTraceDataset<MR>> {
		
		private static Map<String, NavigationMap> toMap(List<String> ids,
				IResourceRepository repo) {
			final Map<String, NavigationMap> maps = new HashMap<String, NavigationMap>();
			for (final String id : ids) {
				final NavigationMap map = repo.getResource(id);
				maps.put(map.getName().toLowerCase(), map);
			}
			return maps;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public LabeledInstructionSeqTraceDataset<MR> create(Parameters params,
				IResourceRepository repo) {
			try {
				return LabeledInstructionSeqTraceDataset
						.readFromFile(
								params.getAsFile("file"),
								toMap(params.getSplit("maps"), repo),
								(ICategoryServices<MR>) repo
										.getResource(NaviExperiment.CATEGORY_SERVICES_RESOURCE));
			} catch (final IOException e) {
				throw new RuntimeException(e);
			}
		}
		
		@Override
		public String type() {
			return "data.ccgsettrc";
		}
		
		@Override
		public ResourceUsage usage() {
			return new ResourceUsage.Builder(type(),
					LabeledInstructionSeqTraceDataset.class)
					.setDescription(
							"Dataset of instruction sequences paired with logical expressions and execution traces (segmented and aligned by sentences)")
					.addParam("file", "file", "Dataset file")
					.addParam("maps", NavigationMap.class, "Navigation maps.")
					.addParam("genlex", "id", "Lexical generator").build();
		}
		
	}
	
}
