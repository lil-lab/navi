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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.uw.cs.lil.navi.data.InstructionSeqTrace;
import edu.uw.cs.lil.navi.data.InstructionSeqTraceDataset;
import edu.uw.cs.lil.navi.data.InstructionTrace;
import edu.uw.cs.lil.navi.data.InstructionTraceDataset;
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

public class InstructionTraceDatasetCreator<MR> implements
		IResourceObjectCreator<InstructionTraceDataset<MR>> {
	
	@SuppressWarnings("unchecked")
	@Override
	public InstructionTraceDataset<MR> create(Parameters params,
			IResourceRepository repo) {
		if (params.contains("sets")) {
			final InstructionSeqTraceDataset<MR> sets = repo.getResource(params
					.get("sets"));
			final List<InstructionTrace<MR>> items = new LinkedList<InstructionTrace<MR>>();
			for (final InstructionSeqTrace<MR> set : sets) {
				for (final InstructionTrace<MR> st : set) {
					items.add(st);
				}
			}
			return new InstructionTraceDataset<MR>(items);
		} else {
			try {
				return InstructionTraceDataset
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
	}
	
	@Override
	public String type() {
		return "data.trc";
	}
	
	@Override
	public ResourceUsage usage() {
		return new ResourceUsage.Builder(type(), InstructionTraceDataset.class)
				.setDescription(
						"Dataset of single instructions paired with execution traces.")
				.addParam(
						"sets",
						"id",
						"Dataset of instruction sequences to construct a labeled instruction dataset from (may be used instead of file and genlex).")
				.addParam("file", "file", "Dataset file")
				.addParam("genlex", "id", "Lexical generator").build();
	}
	
}
