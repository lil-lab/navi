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
package edu.uw.cs.lil.navi.experiments.plat.resources;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uw.cs.lil.navi.data.InstructionSeqTraceDataset;
import edu.uw.cs.lil.navi.map.NavigationMap;
import edu.uw.cs.lil.tiny.explat.IResourceRepository;
import edu.uw.cs.lil.tiny.explat.ParameterizedExperiment.Parameters;
import edu.uw.cs.lil.tiny.explat.resources.IResourceObjectCreator;
import edu.uw.cs.lil.tiny.explat.resources.usage.ResourceUsage;

public class InstructionSeqTraceDatasetCreator<MR> implements
		IResourceObjectCreator<InstructionSeqTraceDataset<MR>> {
	
	private static Map<String, NavigationMap> toMap(List<String> ids,
			IResourceRepository repo) {
		final Map<String, NavigationMap> maps = new HashMap<String, NavigationMap>();
		for (final String id : ids) {
			final NavigationMap map = repo.getResource(id);
			maps.put(map.getName().toLowerCase(), map);
		}
		return maps;
	}
	
	@Override
	public InstructionSeqTraceDataset<MR> create(Parameters params,
			IResourceRepository repo) {
		try {
			return InstructionSeqTraceDataset.readFromFile(
					params.getAsFile("file"),
					toMap(params.getSplit("maps"), repo));
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public String type() {
		return "data.settrc";
	}
	
	@Override
	public ResourceUsage usage() {
		return new ResourceUsage.Builder(type(),
				InstructionSeqTraceDataset.class)
				.setDescription(
						"Dataset that pairs instruction sequences with segmented traces (aligned by sentence)")
				.addParam("file", "file", "Dataset file")
				.addParam("maps", NavigationMap.class, "Navigation maps.")
				.addParam("genlex", "id", "Lexicon generator").build();
	}
	
}
