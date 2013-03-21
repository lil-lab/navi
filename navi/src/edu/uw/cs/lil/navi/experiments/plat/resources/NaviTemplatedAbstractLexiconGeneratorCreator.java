package edu.uw.cs.lil.navi.experiments.plat.resources;

import edu.uw.cs.lil.navi.eval.NaviEvaluationServicesFactory;
import edu.uw.cs.lil.navi.experiments.plat.NaviExperiment;
import edu.uw.cs.lil.navi.learn.lexicalgen.NaviTemplatedAbstractLexiconGenerator;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.explat.IResourceRepository;
import edu.uw.cs.lil.tiny.explat.ParameterizedExperiment.Parameters;
import edu.uw.cs.lil.tiny.explat.resources.IResourceObjectCreator;
import edu.uw.cs.lil.tiny.explat.resources.usage.ResourceUsage;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.mr.lambda.Ontology;
import edu.uw.cs.lil.tiny.parser.IParser;
import edu.uw.cs.lil.tiny.parser.ccg.genlex.TemplatedAbstractLexiconGenerator;
import edu.uw.cs.lil.tiny.parser.ccg.model.IModelImmutable;

public class NaviTemplatedAbstractLexiconGeneratorCreator implements
		IResourceObjectCreator<TemplatedAbstractLexiconGenerator> {
	
	@SuppressWarnings("unchecked")
	@Override
	public TemplatedAbstractLexiconGenerator create(Parameters parameters,
			IResourceRepository resourceRepo) {
		return new NaviTemplatedAbstractLexiconGenerator.Builder(
				Integer.valueOf(parameters.get("maxTokens")),
				(IModelImmutable<Sentence, LogicalExpression>) resourceRepo
						.getResource(parameters.get("model")),
				(IParser<Sentence, LogicalExpression>) resourceRepo
						.getResource(NaviExperiment.BASE_PARSER_RESOURCE),
				Integer.valueOf(parameters.get("beam")),
				((NaviEvaluationServicesFactory) resourceRepo
						.getResource(NaviExperiment.EVAL_SERVICES_FACTORY))
						.getNaviEvaluationConsts())
				.addTemplatesFromModel(
						(IModelImmutable<?, LogicalExpression>) resourceRepo
								.getResource(parameters.get("model")))
				.addConstants(
						(Ontology) resourceRepo
								.getResource(NaviExperiment.DOMAIN_ONTOLOGY_RESOURCE))
				.build();
	}
	
	@Override
	public String type() {
		return "genlex.templated.abstract.navi";
	}
	
	@Override
	public ResourceUsage usage() {
		return new ResourceUsage.Builder(type(),
				TemplatedAbstractLexiconGenerator.class)
				.setDescription(
						"Lexical generator that uses abstract constants for coarse-to-fine pruning of lexical entries")
				.addParam("maxTokens", "int",
						"Max number of tokens to include in lexical entries")
				.addParam("model", "id",
						"Model to use for inference and obtain the set of templates")
				.addParam("beam", "int", "Beam to use for inference").build();
	}
	
}
