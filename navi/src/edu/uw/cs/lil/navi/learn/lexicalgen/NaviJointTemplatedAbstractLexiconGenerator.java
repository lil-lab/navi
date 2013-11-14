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
import edu.uw.cs.lil.navi.data.InstructionTrace;
import edu.uw.cs.lil.navi.data.Trace;
import edu.uw.cs.lil.navi.eval.NaviEvaluationConstants;
import edu.uw.cs.lil.navi.eval.NaviEvaluationServicesFactory;
import edu.uw.cs.lil.navi.experiments.plat.NaviExperiment;
import edu.uw.cs.lil.tiny.ccg.categories.syntax.ComplexSyntax;
import edu.uw.cs.lil.tiny.ccg.categories.syntax.Syntax;
import edu.uw.cs.lil.tiny.ccg.categories.syntax.Syntax.SimpleSyntax;
import edu.uw.cs.lil.tiny.ccg.lexicon.ILexicon;
import edu.uw.cs.lil.tiny.ccg.lexicon.factored.lambda.LexicalTemplate;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.data.utils.IValidator;
import edu.uw.cs.lil.tiny.explat.IResourceRepository;
import edu.uw.cs.lil.tiny.explat.ParameterizedExperiment.Parameters;
import edu.uw.cs.lil.tiny.explat.resources.IResourceObjectCreator;
import edu.uw.cs.lil.tiny.explat.resources.usage.ResourceUsage;
import edu.uw.cs.lil.tiny.mr.lambda.LogicLanguageServices;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalConstant;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.mr.lambda.Ontology;
import edu.uw.cs.lil.tiny.mr.language.type.ComplexType;
import edu.uw.cs.lil.tiny.mr.language.type.Type;
import edu.uw.cs.lil.tiny.parser.IParser;
import edu.uw.cs.lil.tiny.parser.ccg.joint.genlex.JointTemplatedAbstractLexiconGenerator;
import edu.uw.cs.lil.tiny.parser.ccg.model.IModelImmutable;
import edu.uw.cs.lil.tiny.parser.joint.IJointParser;
import edu.uw.cs.utils.composites.Pair;
import edu.uw.cs.utils.log.ILogger;
import edu.uw.cs.utils.log.LoggerFactory;

public class NaviJointTemplatedAbstractLexiconGenerator
		extends
		JointTemplatedAbstractLexiconGenerator<Trace, Trace, Instruction, InstructionTrace> {
	
	public static final ILogger	LOG	= LoggerFactory
											.create(NaviJointTemplatedAbstractLexiconGenerator.class);
	
	public NaviJointTemplatedAbstractLexiconGenerator(
			Set<LexicalTemplate> templates,
			Set<Pair<List<Type>, List<LogicalConstant>>> pontetialConstantSeqs,
			Set<List<LogicalConstant>> abstractConstantSeqs,
			int maxTokens,
			IParser<Sentence, LogicalExpression> baseParser,
			int generationParsingBeam,
			IJointParser<Instruction, LogicalExpression, Trace, Trace> jointParser,
			double margin,
			IValidator<InstructionTrace, Pair<LogicalExpression, Trace>> validator) {
		super(templates, pontetialConstantSeqs, abstractConstantSeqs,
				maxTokens, baseParser, generationParsingBeam, jointParser,
				margin, validator);
		LOG.info(
				"Init JointNaviAbstractTemplatedLexiconGenerator :: %d templates, %d potential constants sequences",
				templates.size(), pontetialConstantSeqs.size());
	}
	
	public static class Builder
			extends
			JointTemplatedAbstractLexiconGenerator.Builder<Trace, Trace, Instruction, InstructionTrace> {
		
		private final NaviEvaluationConstants	naviConsts;
		
		public Builder(
				int maxTokens,
				IParser<Sentence, LogicalExpression> parser,
				int parsingBeam,
				NaviEvaluationConstants naviConsts,
				IJointParser<Instruction, LogicalExpression, Trace, Trace> jointParser,
				IValidator<InstructionTrace, Pair<LogicalExpression, Trace>> validator) {
			super(maxTokens, parser, parsingBeam, jointParser, validator);
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
		public NaviJointTemplatedAbstractLexiconGenerator build() {
			LOG.info(
					"Building NaviJointTemplatedAbstractLexiconGenerator: %d templates, %d constants",
					templates.size(), constants.size());
			return new NaviJointTemplatedAbstractLexiconGenerator(templates,
					createPotentialLists(), createAbstractLists(), maxTokens,
					baseParser, generationParsingBeam, jointParser, margin,
					validator);
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
	
	public static class Creator
			implements
			IResourceObjectCreator<JointTemplatedAbstractLexiconGenerator<Trace, Trace, Instruction, InstructionTrace>> {
		
		@SuppressWarnings("unchecked")
		@Override
		public JointTemplatedAbstractLexiconGenerator<Trace, Trace, Instruction, InstructionTrace> create(
				Parameters params, IResourceRepository resourceRepo) {
			final JointTemplatedAbstractLexiconGenerator.Builder<Trace, Trace, Instruction, InstructionTrace> builder = new Builder(
					Integer.valueOf(params.get("maxTokens")),
					(IParser<Sentence, LogicalExpression>) resourceRepo
							.getResource(params.get("baseParser")),
					Integer.valueOf(params.get("beam")),
					((NaviEvaluationServicesFactory) resourceRepo
							.getResource(NaviExperiment.EVAL_SERVICES_FACTORY))
							.getNaviEvaluationConsts(),
					(IJointParser<Instruction, LogicalExpression, Trace, Trace>) resourceRepo
							.getResource(params.get("parser")),
					(IValidator<InstructionTrace, Pair<LogicalExpression, Trace>>) resourceRepo
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
					.addParam("beam", "int", "Beam to use for inference")
					.build();
		}
		
	}
}
