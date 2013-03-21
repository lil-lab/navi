package edu.uw.cs.lil.navi.experiments.plat.resources;

import java.io.IOException;
import java.util.Map;

import edu.uw.cs.lil.navi.data.LabeledInstructionSeqTraceDataset;
import edu.uw.cs.lil.navi.eval.Task;
import edu.uw.cs.lil.navi.experiments.plat.NaviExperiment;
import edu.uw.cs.lil.navi.map.NavigationMap;
import edu.uw.cs.lil.tiny.ccg.categories.ICategoryServices;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.explat.IResourceRepository;
import edu.uw.cs.lil.tiny.explat.ParameterizedExperiment.Parameters;
import edu.uw.cs.lil.tiny.explat.resources.IResourceObjectCreator;
import edu.uw.cs.lil.tiny.explat.resources.usage.ResourceUsage;
import edu.uw.cs.lil.tiny.parser.ccg.genlex.ILexiconGenerator;
import edu.uw.cs.lil.tiny.parser.joint.model.JointDataItemWrapper;

public class LabeledInstructionSeqTraceDatasetCreator<Y> implements
		IResourceObjectCreator<LabeledInstructionSeqTraceDataset<Y>> {
	
	@SuppressWarnings("unchecked")
	@Override
	public LabeledInstructionSeqTraceDataset<Y> create(Parameters parameters,
			IResourceRepository resourceRepo) {
		try {
			return LabeledInstructionSeqTraceDataset
					.readFromFile(
							parameters.getAsFile("file"),
							(Map<String, NavigationMap>) resourceRepo
									.getResource(NaviExperiment.MAPS_RESOURCE),
							(ILexiconGenerator<JointDataItemWrapper<Sentence, Task>, Y>) resourceRepo
									.getResource(parameters.get("genlex")),
							(ICategoryServices<Y>) resourceRepo
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
