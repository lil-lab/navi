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

import edu.uw.cs.lil.navi.data.LabeledInstructionSeqTraceDataset;
import edu.uw.cs.lil.navi.data.Trace;
import edu.uw.cs.lil.navi.eval.Task;
import edu.uw.cs.lil.navi.experiments.plat.NaviExperiment;
import edu.uw.cs.lil.navi.map.NavigationMap;
import edu.uw.cs.lil.tiny.ccg.categories.ICategoryServices;
import edu.uw.cs.lil.tiny.data.ILabeledDataItem;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.explat.IResourceRepository;
import edu.uw.cs.lil.tiny.explat.ParameterizedExperiment.Parameters;
import edu.uw.cs.lil.tiny.explat.resources.IResourceObjectCreator;
import edu.uw.cs.lil.tiny.explat.resources.usage.ResourceUsage;
import edu.uw.cs.lil.tiny.genlex.ccg.ILexiconGenerator;
import edu.uw.cs.utils.composites.Pair;

public class LabeledInstructionSeqTraceDatasetCreator<MR> implements
		IResourceObjectCreator<LabeledInstructionSeqTraceDataset<MR>> {
	
	@SuppressWarnings("unchecked")
	@Override
	public LabeledInstructionSeqTraceDataset<MR> create(Parameters parameters,
			IResourceRepository resourceRepo) {
		try {
			return LabeledInstructionSeqTraceDataset
					.readFromFile(
							parameters.getAsFile("file"),
							(Map<String, NavigationMap>) resourceRepo
									.getResource(NaviExperiment.MAPS_RESOURCE),
							(ILexiconGenerator<ILabeledDataItem<Pair<Sentence, Task>, Pair<MR, Trace>>, MR>) resourceRepo
									.getResource(parameters.get("genlex")),
							(ICategoryServices<MR>) resourceRepo
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
				.addParam("genlex", "id", "Lexical generator").build();
	}
	
}
