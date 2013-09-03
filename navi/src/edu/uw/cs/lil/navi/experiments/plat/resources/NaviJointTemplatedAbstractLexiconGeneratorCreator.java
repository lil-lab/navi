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

import edu.uw.cs.lil.navi.data.Trace;
import edu.uw.cs.lil.navi.eval.NaviEvaluationServicesFactory;
import edu.uw.cs.lil.navi.eval.Task;
import edu.uw.cs.lil.navi.experiments.plat.NaviExperiment;
import edu.uw.cs.lil.navi.learn.lexicalgen.NaviJointTemplatedAbstractLexiconGenerator;
import edu.uw.cs.lil.tiny.ccg.lexicon.ILexicon;
import edu.uw.cs.lil.tiny.data.IDataItem;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.data.utils.IValidator;
import edu.uw.cs.lil.tiny.explat.IResourceRepository;
import edu.uw.cs.lil.tiny.explat.ParameterizedExperiment.Parameters;
import edu.uw.cs.lil.tiny.explat.resources.IResourceObjectCreator;
import edu.uw.cs.lil.tiny.explat.resources.usage.ResourceUsage;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.mr.lambda.Ontology;
import edu.uw.cs.lil.tiny.parser.IParser;
import edu.uw.cs.lil.tiny.parser.ccg.joint.genlex.JointTemplatedAbstractLexiconGenerator;
import edu.uw.cs.lil.tiny.parser.ccg.joint.genlex.JointTemplatedAbstractLexiconGenerator.Builder;
import edu.uw.cs.lil.tiny.parser.ccg.model.IModelImmutable;
import edu.uw.cs.lil.tiny.parser.joint.IJointParser;
import edu.uw.cs.utils.composites.Pair;

public class NaviJointTemplatedAbstractLexiconGeneratorCreator
		implements
		IResourceObjectCreator<JointTemplatedAbstractLexiconGenerator<Task, Trace, Trace, IDataItem<Pair<Sentence, Task>>>> {
	
	@SuppressWarnings("unchecked")
	@Override
	public JointTemplatedAbstractLexiconGenerator<Task, Trace, Trace, IDataItem<Pair<Sentence, Task>>> create(
			Parameters params, IResourceRepository resourceRepo) {
		final Builder<Task, Trace, Trace, IDataItem<Pair<Sentence, Task>>> builder = new NaviJointTemplatedAbstractLexiconGenerator.Builder(
				Integer.valueOf(params.get("maxTokens")),
				(IParser<Sentence, LogicalExpression>) resourceRepo
						.getResource(params.get("baseParser")),
				Integer.valueOf(params.get("beam")),
				((NaviEvaluationServicesFactory) resourceRepo
						.getResource(NaviExperiment.EVAL_SERVICES_FACTORY))
						.getNaviEvaluationConsts(),
				(IJointParser<Sentence, Task, LogicalExpression, Trace, Trace>) resourceRepo
						.getResource(params.get("parser")),
				(IValidator<IDataItem<Pair<Sentence, Task>>, Pair<LogicalExpression, Trace>>) resourceRepo
						.getResource(params.get("validator")))
				.addConstants((Ontology) resourceRepo
						.getResource(NaviExperiment.DOMAIN_ONTOLOGY_RESOURCE));
		
		if (params.contains("templatesModel")) {
			builder.addTemplatesFromModel((IModelImmutable<?, LogicalExpression>) resourceRepo
					.getResource(params.get("model")));
		} else if (params.contains("lexicon")) {
			builder.addTemplatesFromLexicon((ILexicon<LogicalExpression>) resourceRepo
					.getResource(params.get("lexicon")));
		} else {
			throw new IllegalStateException("no templates source specified");
		}
		
		if (params.contains("margin")) {
			builder.setMargin(Double.valueOf(params.get("margin")));
		}
		
		return builder.build();
	}
	
	@Override
	public String type() {
		return "genlex.templated.abstract.joint.navi";
	}
	
	@Override
	public ResourceUsage usage() {
		return new ResourceUsage.Builder(type(),
				JointTemplatedAbstractLexiconGenerator.class)
				.setDescription(
						"Lexical generator that uses abstract constants for coarse-to-fine pruning of lexical entries and joint inference for choosing a threshold model score for parses")
				.addParam("maxTokens", "int",
						"Max number of tokens to include in lexical entries")
				.addParam("model", "id",
						"Joint model to use for inference and obtain the set of templates")
				.addParam("validator", "id",
						"Validation function to select valid parses")
				.addParam(
						"margin",
						"double",
						"Parses that resepect this margin against the top valid current parses, are pruned during generation. Default: 0.0.")
				.addParam("beam", "int", "Beam to use for inference").build();
	}
	
}
