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
import edu.uw.cs.lil.tiny.ccg.lexicon.factored.lambda.LexicalTemplate;
import edu.uw.cs.lil.tiny.explat.IResourceRepository;
import edu.uw.cs.lil.tiny.explat.ParameterizedExperiment.Parameters;
import edu.uw.cs.lil.tiny.explat.resources.IResourceObjectCreator;
import edu.uw.cs.lil.tiny.explat.resources.usage.ResourceUsage;
import edu.uw.cs.lil.tiny.genlex.ccg.template.TemplateGenlex;
import edu.uw.cs.lil.tiny.mr.lambda.LogicLanguageServices;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalConstant;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.mr.lambda.Ontology;
import edu.uw.cs.lil.tiny.mr.language.type.ComplexType;
import edu.uw.cs.lil.tiny.mr.language.type.Type;
import edu.uw.cs.lil.tiny.parser.ccg.model.IModelImmutable;
import edu.uw.cs.utils.log.ILogger;
import edu.uw.cs.utils.log.LoggerFactory;

public class NaviTemplatedLexiconGenerator extends TemplateGenlex<Instruction> {
	public static final ILogger	LOG	= LoggerFactory
											.create(NaviTemplatedLexiconGenerator.class);
	
	public NaviTemplatedLexiconGenerator(Set<LexicalTemplate> templates,
			Set<List<LogicalConstant>> pontetialConstantSeqs, int maxTokens) {
		super(templates, pontetialConstantSeqs, maxTokens);
		LOG.info(
				"Init NaviTemplatedLexiconGenerator :: %d templates, %d potential constants sequences",
				templates.size(), pontetialConstantSeqs.size());
	}
	
	public static class Builder extends TemplateGenlex.Builder<Instruction> {
		
		private final NaviEvaluationConstants	naviConsts;
		
		public Builder(int maxTokens, NaviEvaluationConstants naviConsts) {
			super(maxTokens);
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
		public NaviTemplatedLexiconGenerator build() {
			LOG.info(
					"Building NaviTemplatedLexiconGenerator: %d templates, %d constants",
					templates.size(), constants.size());
			return new NaviTemplatedLexiconGenerator(templates,
					createPotentialLists(), maxTokens);
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
			IResourceObjectCreator<TemplateGenlex<Instruction>> {
		
		@SuppressWarnings("unchecked")
		@Override
		public TemplateGenlex<Instruction> create(Parameters parameters,
				IResourceRepository resourceRepo) {
			return new NaviTemplatedLexiconGenerator.Builder(
					Integer.valueOf(parameters.get("maxTokens")),
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
			return "genlex.templated.navi";
		}
		
		@Override
		public ResourceUsage usage() {
			return new ResourceUsage.Builder(type(), TemplateGenlex.class)
					.setDescription("Navi templated lexicon generator")
					.addParam("maxTokens", "int",
							"Maximum number of tokens in a generated lexical entry")
					.build();
		}
	}
}
