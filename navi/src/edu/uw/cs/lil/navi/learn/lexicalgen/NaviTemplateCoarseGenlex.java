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
package edu.uw.cs.lil.navi.learn.lexicalgen;

import java.util.List;
import java.util.Set;

import edu.uw.cs.lil.navi.data.Instruction;
import edu.uw.cs.lil.navi.eval.NaviEvaluationConstants;
import edu.uw.cs.lil.navi.eval.NaviEvaluationServicesFactory;
import edu.uw.cs.lil.navi.experiments.plat.NaviExperiment;
import edu.uw.cs.lil.tiny.ccg.categories.syntax.ComplexSyntax;
import edu.uw.cs.lil.tiny.ccg.categories.syntax.Syntax;
import edu.uw.cs.lil.tiny.ccg.categories.syntax.Syntax.SimpleSyntax;
import edu.uw.cs.lil.tiny.ccg.lexicon.ILexicon;
import edu.uw.cs.lil.tiny.ccg.lexicon.factored.lambda.LexicalTemplate;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.explat.IResourceRepository;
import edu.uw.cs.lil.tiny.explat.ParameterizedExperiment.Parameters;
import edu.uw.cs.lil.tiny.explat.resources.IResourceObjectCreator;
import edu.uw.cs.lil.tiny.explat.resources.usage.ResourceUsage;
import edu.uw.cs.lil.tiny.genlex.ccg.template.coarse.TemplateCoarseGenlex;
import edu.uw.cs.lil.tiny.mr.lambda.LogicLanguageServices;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalConstant;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.mr.language.type.ComplexType;
import edu.uw.cs.lil.tiny.mr.language.type.Type;
import edu.uw.cs.lil.tiny.parser.IParser;
import edu.uw.cs.lil.tiny.parser.ccg.model.IModelImmutable;
import edu.uw.cs.utils.composites.Pair;
import edu.uw.cs.utils.log.ILogger;
import edu.uw.cs.utils.log.LoggerFactory;

public class NaviTemplateCoarseGenlex extends TemplateCoarseGenlex<Instruction> {
	public static final ILogger	LOG	= LoggerFactory
											.create(NaviTemplateCoarseGenlex.class);
	
	public NaviTemplateCoarseGenlex(Set<LexicalTemplate> templates,
			Set<Pair<List<Type>, List<LogicalConstant>>> pontetialConstantSeqs,
			Set<List<LogicalConstant>> abstractConstantSeqs, int maxTokens,
			IParser<Sentence, LogicalExpression> parser, int parsingBeam) {
		super(templates, pontetialConstantSeqs, abstractConstantSeqs,
				maxTokens, parser, parsingBeam);
	}
	
	public static class Builder extends
			TemplateCoarseGenlex.Builder<Instruction> {
		
		private final NaviEvaluationConstants	naviConsts;
		
		public Builder(int maxTokens,
				IParser<Sentence, LogicalExpression> parser, int parsingBeam,
				NaviEvaluationConstants naviConsts) {
			super(maxTokens, parser, parsingBeam);
			this.naviConsts = naviConsts;
		}
		
		@Override
		public Builder addTemplate(LexicalTemplate template) {
			super.addTemplate(template);
			
			// Duplicate the template with modified syntax, if necessary
			super.addTemplate(template
					.cloneWithNewSyntax(adverbialToSentenceExpansion(template
							.getTemplateCategory().getSyntax(), template
							.getTemplateCategory().getSem().getType())));
			
			return this;
		}
		
		@Override
		public NaviTemplateCoarseGenlex build() {
			LOG.info(
					"Building NaviTemplatedLexiconGenerator: %d templates, %d constants",
					templates.size(), constants.size());
			return new NaviTemplateCoarseGenlex(templates,
					createPotentialLists(), createAbstractLists(), maxTokens,
					parser, parsingBeam);
		}
		
		private Syntax adverbialToSentenceExpansion(Syntax syntax, Type type) {
			if (syntax.equals(Syntax.S) && isActionToTruth(type)) {
				return Syntax.AP;
			} else if (syntax.equals(Syntax.S) && isActionToTruth(type)) {
				return Syntax.S;
			} else if (syntax instanceof SimpleSyntax) {
				return syntax;
			} else if (syntax instanceof ComplexSyntax && type.isComplex()) {
				return new ComplexSyntax(adverbialToSentenceExpansion(
						((ComplexSyntax) syntax).getLeft(),
						((ComplexType) type).getRange()),
						adverbialToSentenceExpansion(
								((ComplexSyntax) syntax).getRight(),
								((ComplexType) type).getDomain()),
						((ComplexSyntax) syntax).getSlash());
			} else {
				throw new IllegalStateException(
						"invalid combination of syntax (" + syntax
								+ ") and type: " + type);
			}
		}
		
		private boolean isActionToTruth(Type type) {
			return type.isComplex()
					&& ((ComplexType) type).getDomain().equals(
							naviConsts.getActionSeqType())
					&& ((ComplexType) type).getRange().equals(
							LogicLanguageServices.getTypeRepository()
									.getTruthValueType());
		}
		
	}
	
	public static class Creator implements
			IResourceObjectCreator<NaviTemplateCoarseGenlex> {
		
		@SuppressWarnings("unchecked")
		@Override
		public NaviTemplateCoarseGenlex create(Parameters params,
				IResourceRepository repo) {
			final NaviTemplateCoarseGenlex.Builder builder = new NaviTemplateCoarseGenlex.Builder(
					Integer.valueOf(params.get("maxTokens")),
					(IParser<Sentence, LogicalExpression>) repo
							.getResource(params.get("parser")),
					Integer.valueOf(params.get("beam")),
					((NaviEvaluationServicesFactory) repo
							.getResource(NaviExperiment.EVAL_SERVICES_FACTORY))
							.getNaviEvaluationConsts());
			
			builder.addConstants((Iterable<LogicalConstant>) repo
					.getResource(NaviExperiment.DOMAIN_ONTOLOGY_RESOURCE));
			
			if (params.contains("templatesModel")) {
				builder.addTemplatesFromModel((IModelImmutable<?, LogicalExpression>) repo
						.getResource(params.get("model")));
			} else if (params.contains("lexicon")) {
				builder.addTemplatesFromLexicon((ILexicon<LogicalExpression>) repo
						.getResource(params.get("lexicon")));
			} else {
				throw new IllegalStateException("no templates source specified");
			}
			
			return builder.build();
		}
		
		@Override
		public String type() {
			return "genlex.templated.coarse.navi";
		}
		
		@Override
		public ResourceUsage usage() {
			return new ResourceUsage.Builder(type(), TemplateCoarseGenlex.class)
					.setDescription(
							"Lexical generator that uses abstract constants for coarse-to-fine pruning of lexical entries")
					.addParam("maxTokens", "int",
							"Max number of tokens to include in lexical entries")
					.addParam("model", "id",
							"Model to use for inference and obtain the set of templates")
					.addParam("beam", "int", "Beam to use for inference")
					.build();
		}
		
	}
}
