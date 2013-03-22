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

import edu.uw.cs.lil.navi.eval.NaviEvaluationConstants;
import edu.uw.cs.lil.tiny.ccg.categories.syntax.ComplexSyntax;
import edu.uw.cs.lil.tiny.ccg.categories.syntax.Syntax;
import edu.uw.cs.lil.tiny.ccg.categories.syntax.Syntax.SimpleSyntax;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.mr.lambda.LogicLanguageServices;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalConstant;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.mr.language.type.ComplexType;
import edu.uw.cs.lil.tiny.mr.language.type.Type;
import edu.uw.cs.lil.tiny.parser.IParser;
import edu.uw.cs.lil.tiny.parser.ccg.factoredlex.LexicalTemplate;
import edu.uw.cs.lil.tiny.parser.ccg.genlex.TemplatedAbstractLexiconGenerator;
import edu.uw.cs.lil.tiny.parser.ccg.model.IModelImmutable;
import edu.uw.cs.utils.composites.Pair;
import edu.uw.cs.utils.log.ILogger;
import edu.uw.cs.utils.log.LoggerFactory;

public class NaviTemplatedAbstractLexiconGenerator extends
		TemplatedAbstractLexiconGenerator {
	private static final ILogger	LOG	= LoggerFactory
												.create(NaviTemplatedAbstractLexiconGenerator.class);
	
	public NaviTemplatedAbstractLexiconGenerator(
			Set<LexicalTemplate> templates,
			Set<Pair<List<Type>, List<LogicalConstant>>> pontetialConstantSeqs,
			Set<List<LogicalConstant>> abstractConstantSeqs, int maxTokens,
			IModelImmutable<Sentence, LogicalExpression> model,
			IParser<Sentence, LogicalExpression> parser, int parsingBeam) {
		super(templates, pontetialConstantSeqs, abstractConstantSeqs,
				maxTokens, model, parser, parsingBeam);
		LOG.info(
				"Init NaviTemplatedLexiconGenerator :: %d templates, %d potential constants sequences",
				templates.size(), pontetialConstantSeqs.size());
	}
	
	public static class Builder extends
			TemplatedAbstractLexiconGenerator.Builder {
		
		private final NaviEvaluationConstants	naviConsts;
		
		public Builder(int maxTokens,
				IModelImmutable<Sentence, LogicalExpression> model,
				IParser<Sentence, LogicalExpression> parser, int parsingBeam,
				NaviEvaluationConstants naviConsts) {
			super(maxTokens, model, parser, parsingBeam);
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
		public NaviTemplatedAbstractLexiconGenerator build() {
			LOG.info(
					"Building NaviTemplatedLexiconGenerator: %d templates, %d constants",
					templates.size(), constants.size());
			return new NaviTemplatedAbstractLexiconGenerator(templates,
					createPotentialLists(), createAbstractLists(), maxTokens,
					model, parser, parsingBeam);
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
}
