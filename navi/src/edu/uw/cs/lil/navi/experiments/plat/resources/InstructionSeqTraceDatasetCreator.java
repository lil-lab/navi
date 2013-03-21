package edu.uw.cs.lil.navi.experiments.plat.resources;

import java.io.IOException;
import java.util.Map;

import edu.uw.cs.lil.navi.data.InstructionSeqTraceDataset;
import edu.uw.cs.lil.navi.eval.Task;
import edu.uw.cs.lil.navi.experiments.plat.NaviExperiment;
import edu.uw.cs.lil.navi.map.NavigationMap;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.explat.IResourceRepository;
import edu.uw.cs.lil.tiny.explat.ParameterizedExperiment.Parameters;
import edu.uw.cs.lil.tiny.explat.resources.IResourceObjectCreator;
import edu.uw.cs.lil.tiny.explat.resources.usage.ResourceUsage;
import edu.uw.cs.lil.tiny.parser.ccg.genlex.ILexiconGenerator;
import edu.uw.cs.lil.tiny.parser.joint.model.JointDataItemWrapper;

public class InstructionSeqTraceDatasetCreator<Y> implements
		IResourceObjectCreator<InstructionSeqTraceDataset<Y>> {
	
	@SuppressWarnings("unchecked")
	@Override
	public InstructionSeqTraceDataset<Y> create(Parameters parameters,
			IResourceRepository resourceRepo) {
		try {
			return InstructionSeqTraceDataset
					.readFromFile(
							parameters.getAsFile("file"),
							(Map<String, NavigationMap>) resourceRepo
									.getResource(NaviExperiment.MAPS_RESOURCE),
							(ILexiconGenerator<JointDataItemWrapper<Sentence, Task>, Y>) resourceRepo
									.getResource(parameters.get("genlex")));
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
