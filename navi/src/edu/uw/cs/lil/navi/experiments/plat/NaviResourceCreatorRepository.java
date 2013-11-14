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
package edu.uw.cs.lil.navi.experiments.plat;

import java.util.List;

import edu.uw.cs.lil.navi.data.Instruction;
import edu.uw.cs.lil.navi.data.InstructionSeq;
import edu.uw.cs.lil.navi.data.InstructionSeqTraceDataset;
import edu.uw.cs.lil.navi.data.InstructionTrace;
import edu.uw.cs.lil.navi.data.InstructionTraceDataset;
import edu.uw.cs.lil.navi.data.LabeledInstructionSeqTraceDataset;
import edu.uw.cs.lil.navi.data.LabeledInstructionTraceDataset;
import edu.uw.cs.lil.navi.data.Trace;
import edu.uw.cs.lil.navi.eval.Task;
import edu.uw.cs.lil.navi.exec.NaviNaiveSeqExecutor;
import edu.uw.cs.lil.navi.exec.NaviSeqExecutor;
import edu.uw.cs.lil.navi.exec.NaviSingleExecutor;
import edu.uw.cs.lil.navi.features.ExecutionFeatureSet;
import edu.uw.cs.lil.navi.features.init.ReptFeaturesInit;
import edu.uw.cs.lil.navi.features.init.TemplateCountModelInit;
import edu.uw.cs.lil.navi.learn.lexicalgen.NaviJointTemplatedAbstractLexiconGenerator;
import edu.uw.cs.lil.navi.learn.lexicalgen.NaviTemplateCoarseGenlex;
import edu.uw.cs.lil.navi.learn.lexicalgen.NaviTemplatedLexiconGenerator;
import edu.uw.cs.lil.navi.learn.validation.NaviLearningRelaxedValidator;
import edu.uw.cs.lil.navi.learn.validation.NaviLearningValidator;
import edu.uw.cs.lil.navi.learn.validation.NaviLearningWeakValidator;
import edu.uw.cs.lil.navi.learn.validation.NaviPairValidatorWrapper;
import edu.uw.cs.lil.navi.map.NavigationMap;
import edu.uw.cs.lil.navi.parse.NaviGraphParser;
import edu.uw.cs.lil.navi.parse.WrappedCKYParser;
import edu.uw.cs.lil.tiny.ccg.lexicon.Lexicon;
import edu.uw.cs.lil.tiny.ccg.lexicon.factored.lambda.FactoredLexicon;
import edu.uw.cs.lil.tiny.ccg.lexicon.factored.lambda.Lexeme;
import edu.uw.cs.lil.tiny.data.IDataItem;
import edu.uw.cs.lil.tiny.data.ILabeledDataItem;
import edu.uw.cs.lil.tiny.data.collection.CompositeDataCollection;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.data.singlesentence.SingleSentenceDataset;
import edu.uw.cs.lil.tiny.explat.resources.ResourceCreatorRepository;
import edu.uw.cs.lil.tiny.learn.situated.perceptron.SituatedValidationPerceptron;
import edu.uw.cs.lil.tiny.learn.situated.stocgrad.SituatedValidationStocGrad;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.mr.lambda.ccg.SimpleFullParseFilter;
import edu.uw.cs.lil.tiny.parser.ccg.cky.genlex.MarkAwareCKYBinaryParsingRule;
import edu.uw.cs.lil.tiny.parser.ccg.cky.multi.MultiCKYParser;
import edu.uw.cs.lil.tiny.parser.ccg.factoredlex.features.LexemeFeatureSet;
import edu.uw.cs.lil.tiny.parser.ccg.factoredlex.features.LexicalTemplateFeatureSet;
import edu.uw.cs.lil.tiny.parser.ccg.factoredlex.features.scorers.LexemeCooccurrenceScorer;
import edu.uw.cs.lil.tiny.parser.ccg.factoredlex.features.scorers.LexicalEntryLexemeBasedScorer;
import edu.uw.cs.lil.tiny.parser.ccg.features.basic.LexicalFeatureSet;
import edu.uw.cs.lil.tiny.parser.ccg.features.basic.LexicalFeaturesInit;
import edu.uw.cs.lil.tiny.parser.ccg.features.basic.RuleUsageFeatureSet;
import edu.uw.cs.lil.tiny.parser.ccg.features.basic.scorer.ExpLengthLexicalEntryScorer;
import edu.uw.cs.lil.tiny.parser.ccg.features.basic.scorer.OriginLexicalEntryScorer;
import edu.uw.cs.lil.tiny.parser.ccg.features.basic.scorer.SkippingSensitiveLexicalEntryScorer;
import edu.uw.cs.lil.tiny.parser.ccg.features.basic.scorer.UniformScorer;
import edu.uw.cs.lil.tiny.parser.ccg.features.lambda.LogicalExpressionCooccurrenceFeatureSet;
import edu.uw.cs.lil.tiny.parser.ccg.features.lambda.LogicalExpressionCoordinationFeatureSet;
import edu.uw.cs.lil.tiny.parser.ccg.joint.cky.JointInferenceChartLogger;
import edu.uw.cs.lil.tiny.parser.ccg.model.LexiconModelInit;
import edu.uw.cs.lil.tiny.parser.ccg.model.ModelLogger;
import edu.uw.cs.lil.tiny.parser.ccg.rules.BinaryRulesSet;
import edu.uw.cs.lil.tiny.parser.ccg.rules.OverloadedRulesCreator;
import edu.uw.cs.lil.tiny.parser.ccg.rules.lambda.typeshifting.basic.AdjectiveTypeShifting;
import edu.uw.cs.lil.tiny.parser.ccg.rules.lambda.typeshifting.basic.AdverbialTopicalisationTypeShifting;
import edu.uw.cs.lil.tiny.parser.ccg.rules.lambda.typeshifting.basic.AdverbialTypeShifting;
import edu.uw.cs.lil.tiny.parser.ccg.rules.lambda.typeshifting.basic.PrepositionTypeShifting;
import edu.uw.cs.lil.tiny.parser.ccg.rules.lambda.typeshifting.basic.SententialAdverbialTypeShifting;
import edu.uw.cs.lil.tiny.parser.ccg.rules.primitivebinary.ApplicationCreator;
import edu.uw.cs.lil.tiny.parser.ccg.rules.primitivebinary.CompositionCreator;
import edu.uw.cs.lil.tiny.parser.ccg.rules.skipping.SkippingRuleCreator;
import edu.uw.cs.lil.tiny.parser.joint.model.JointModel;
import edu.uw.cs.lil.tiny.test.exec.ExecTester;
import edu.uw.cs.utils.composites.Pair;

public class NaviResourceCreatorRepository extends ResourceCreatorRepository {
	public NaviResourceCreatorRepository() {
		super();
		// Register default available resources
		
		// Parser creators
		registerResourceCreator(new OverloadedRulesCreator<LogicalExpression>());
		registerResourceCreator(new ApplicationCreator<LogicalExpression>());
		registerResourceCreator(new CompositionCreator<LogicalExpression>());
		registerResourceCreator(new PrepositionTypeShifting.Creator());
		registerResourceCreator(new AdjectiveTypeShifting.Creator());
		registerResourceCreator(new AdverbialTypeShifting.Creator());
		registerResourceCreator(new AdverbialTopicalisationTypeShifting.Creator());
		registerResourceCreator(new SententialAdverbialTypeShifting.Creator());
		registerResourceCreator(new BinaryRulesSet.Creator<LogicalExpression>());
		registerResourceCreator(new SkippingRuleCreator<LogicalExpression>());
		registerResourceCreator(new MultiCKYParser.Creator<LogicalExpression>());
		registerResourceCreator(new SimpleFullParseFilter.Creator());
		registerResourceCreator(new MarkAwareCKYBinaryParsingRule.Creator<LogicalExpression>());
		registerResourceCreator(new NaviGraphParser.Creator());
		registerResourceCreator(new WrappedCKYParser.Creator());
		
		registerResourceCreator(new NavigationMap.Creator());
		registerResourceCreator(new LexiconModelInit.Creator<Sentence, LogicalExpression>());
		registerResourceCreator(new SingleSentenceDataset.Creator());
		registerResourceCreator(new TemplateCountModelInit.Creator());
		registerResourceCreator(new ReptFeaturesInit.Creator<Sentence>());
		registerResourceCreator(new LexicalFeaturesInit.Creator<Sentence, LogicalExpression>());
		registerResourceCreator(new UniformScorer.Creator<LogicalExpression>());
		registerResourceCreator(new ExpLengthLexicalEntryScorer.Creator<LogicalExpression>());
		registerResourceCreator(new UniformScorer.Creator<Lexeme>(
				"scorer.uniform.factored"));
		registerResourceCreator(new LexemeFeatureSet.Creator<IDataItem<Pair<Sentence, Task>>>());
		registerResourceCreator(new LexicalFeatureSet.Creator<IDataItem<Pair<Sentence, Task>>, LogicalExpression>());
		registerResourceCreator(new LexicalTemplateFeatureSet.Creator<IDataItem<Pair<Sentence, Task>>>());
		registerResourceCreator(new LogicalExpressionCoordinationFeatureSet.Creator<Sentence>());
		registerResourceCreator(new LogicalExpressionCooccurrenceFeatureSet.Creator<Sentence>());
		registerResourceCreator(new LexemeCooccurrenceScorer.Creator());
		registerResourceCreator(new SkippingSensitiveLexicalEntryScorer.Creator<LogicalExpression>());
		registerResourceCreator(new LexicalEntryLexemeBasedScorer.Creator());
		registerResourceCreator(new LabeledInstructionTraceDataset.Creator<LogicalExpression>());
		registerResourceCreator(new InstructionTraceDataset.Creator());
		registerResourceCreator(new JointModel.Creator<Instruction, LogicalExpression, Trace>());
		registerResourceCreator(new Lexicon.Creator<LogicalExpression>());
		registerResourceCreator(new FactoredLexicon.Creator());
		registerResourceCreator(new ExecutionFeatureSet.Creator());
		registerResourceCreator(new NaviTemplatedLexiconGenerator.Creator());
		registerResourceCreator(new NaviTemplateCoarseGenlex.Creator());
		registerResourceCreator(new NaviJointTemplatedAbstractLexiconGenerator.Creator());
		registerResourceCreator(new SituatedValidationPerceptron.Creator<Instruction, LogicalExpression, Trace, Trace, InstructionTrace>(
				"learner.trc"));
		registerResourceCreator(new SituatedValidationStocGrad.Creator<Instruction, LogicalExpression, Trace, Trace, InstructionTrace>(
				"learner.stocgrad.trc"));
		registerResourceCreator(new OriginLexicalEntryScorer.Creator<LogicalExpression>());
		registerResourceCreator(new ExecTester.Creator<Instruction, Pair<LogicalExpression, Trace>>());
		registerResourceCreator(new NaviSingleExecutor.Creator());
		registerResourceCreator(new LabeledInstructionSeqTraceDataset.Creator<LogicalExpression>());
		registerResourceCreator(new InstructionSeqTraceDataset.Creator());
		registerResourceCreator(new RuleUsageFeatureSet.Creator<IDataItem<Pair<Sentence, Task>>, LogicalExpression>());
		registerResourceCreator(new NaviSeqExecutor.Creator());
		registerResourceCreator(new NaviNaiveSeqExecutor.Creator());
		registerResourceCreator(new ModelLogger.Creator());
		registerResourceCreator(new NaviLearningValidator.Creator());
		registerResourceCreator(new NaviPairValidatorWrapper.Creator<LogicalExpression>());
		registerResourceCreator(new NaviLearningRelaxedValidator.Creator<LogicalExpression>());
		registerResourceCreator(new NaviLearningWeakValidator.Creator<LogicalExpression>());
		registerResourceCreator(new JointInferenceChartLogger.Creator<Trace, Trace>());
		registerResourceCreator(new ExecTester.Creator<InstructionSeq, List<Pair<LogicalExpression, Trace>>>(
				"tester.exec.set"));
		registerResourceCreator(new CompositeDataCollection.Creator<ILabeledDataItem<Pair<Sentence, Task>, Pair<LogicalExpression, Trace>>>(
				"data.composite.ccgtrc"));
		registerResourceCreator(new CompositeDataCollection.Creator<ILabeledDataItem<Pair<List<Sentence>, Task>, List<Pair<LogicalExpression, Trace>>>>(
				"data.composite.ccgsettrc"));
		registerResourceCreator(new CompositeDataCollection.Creator<ILabeledDataItem<Pair<Sentence, Task>, Trace>>(
				"data.composite.trc"));
		registerResourceCreator(new CompositeDataCollection.Creator<ILabeledDataItem<Pair<List<Sentence>, Task>, List<Trace>>>(
				"data.composite.settrc"));
		
	}
}
