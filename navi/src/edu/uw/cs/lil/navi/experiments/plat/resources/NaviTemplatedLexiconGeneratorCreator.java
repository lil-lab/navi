package edu.uw.cs.lil.navi.experiments.plat.resources;

import edu.uw.cs.lil.navi.eval.NaviEvaluationServicesFactory;
import edu.uw.cs.lil.navi.experiments.plat.NaviExperiment;
import edu.uw.cs.lil.navi.learn.lexicalgen.NaviTemplatedLexiconGenerator;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.explat.IResourceRepository;
import edu.uw.cs.lil.tiny.explat.ParameterizedExperiment.Parameters;
import edu.uw.cs.lil.tiny.explat.resources.IResourceObjectCreator;
import edu.uw.cs.lil.tiny.explat.resources.usage.ResourceUsage;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.mr.lambda.Ontology;
import edu.uw.cs.lil.tiny.parser.ccg.genlex.TemplatedLexiconGenerator;
import edu.uw.cs.lil.tiny.parser.ccg.model.IModelImmutable;

public class NaviTemplatedLexiconGeneratorCreator implements
		IResourceObjectCreator<TemplatedLexiconGenerator> {
	
	@SuppressWarnings("unchecked")
	@Override
	public TemplatedLexiconGenerator create(Parameters parameters,
			IResourceRepository resourceRepo) {
		return new NaviTemplatedLexiconGenerator.Builder(
				Integer.valueOf(parameters.get("maxTokens")),
				((NaviEvaluationServicesFactory) resourceRepo
						.getResource(NaviExperiment.EVAL_SERVICES_FACTORY))
						.getNaviEvaluationConsts(),
				(IModelImmutable<Sentence, LogicalExpression>) resourceRepo
						.getResource(parameters.get("model")))
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
		return "genlex.templated.navi";
	}
	
	@Override
	public ResourceUsage usage() {
		return new ResourceUsage.Builder(type(),
				TemplatedLexiconGenerator.class)
				.setDescription("Navi templated lexicon generator")
				.addParam("maxTokens", "int",
						"Maximum number of tokens in a generated lexical entry")
				.addParam(
						"model",
						"id",
						"Use this model to prune existing lexical entries and to generate the set of templates.")
				.build();
	}
}
