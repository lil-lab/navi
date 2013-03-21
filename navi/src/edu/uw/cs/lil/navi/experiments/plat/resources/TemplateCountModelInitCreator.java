package edu.uw.cs.lil.navi.experiments.plat.resources;

import edu.uw.cs.lil.navi.features.init.TemplateCountModelInit;
import edu.uw.cs.lil.tiny.explat.IResourceRepository;
import edu.uw.cs.lil.tiny.explat.ParameterizedExperiment.Parameters;
import edu.uw.cs.lil.tiny.explat.resources.IResourceObjectCreator;
import edu.uw.cs.lil.tiny.explat.resources.usage.ResourceUsage;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.ILexicon;

public class TemplateCountModelInitCreator implements
		IResourceObjectCreator<TemplateCountModelInit> {
	
	@SuppressWarnings("unchecked")
	@Override
	public TemplateCountModelInit create(Parameters params,
			IResourceRepository repo) {
		return new TemplateCountModelInit(
				(ILexicon<LogicalExpression>) repo.getResource(params
						.get("lexicon")), params.get("tag"));
	}
	
	@Override
	public String type() {
		return "init.feats.templates";
	}
	
	@Override
	public ResourceUsage usage() {
		return new ResourceUsage.Builder(type(), TemplateCountModelInit.class)
				.setDescription(
						"Model initilizer that initalizes lexical template features by computing frequence stats on a seed lexicon")
				.addParam("lexicon", "id",
						"Lexicon to compute frequency stats from")
				.addParam("tag", "string", "Lexical template features tag")
				.build();
	}
	
}
