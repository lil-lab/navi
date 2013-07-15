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
import java.util.Map;

import edu.uw.cs.lil.navi.data.InstructionSeqTraceDataset;
import edu.uw.cs.lil.navi.eval.Task;
import edu.uw.cs.lil.navi.experiments.plat.NaviExperiment;
import edu.uw.cs.lil.navi.map.NavigationMap;
import edu.uw.cs.lil.tiny.data.IDataItem;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.explat.IResourceRepository;
import edu.uw.cs.lil.tiny.explat.ParameterizedExperiment.Parameters;
import edu.uw.cs.lil.tiny.explat.resources.IResourceObjectCreator;
import edu.uw.cs.lil.tiny.explat.resources.usage.ResourceUsage;
import edu.uw.cs.lil.tiny.genlex.ccg.ILexiconGenerator;
import edu.uw.cs.utils.composites.Pair;

public class InstructionSeqTraceDatasetCreator<MR> implements
		IResourceObjectCreator<InstructionSeqTraceDataset<MR>> {
	
	@SuppressWarnings("unchecked")
	@Override
	public InstructionSeqTraceDataset<MR> create(Parameters params,
			IResourceRepository repo) {
		try {
			return InstructionSeqTraceDataset
					.readFromFile(
							params.getAsFile("file"),
							(Map<String, NavigationMap>) repo
									.getResource(NaviExperiment.MAPS_RESOURCE),
							(ILexiconGenerator<IDataItem<Pair<Sentence, Task>>, MR>) repo
									.getResource(params.get("genlex")));
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
				.addParam("genlex", "id", "Lexicon generator").build();
	}
	
}
