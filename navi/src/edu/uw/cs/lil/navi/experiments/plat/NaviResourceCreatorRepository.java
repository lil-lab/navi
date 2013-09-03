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

import edu.uw.cs.lil.navi.data.InstructionTrace;
import edu.uw.cs.lil.navi.data.Trace;
import edu.uw.cs.lil.navi.eval.Task;
import edu.uw.cs.lil.navi.experiments.plat.resources.ExecutionFeatureSetCreator;
import edu.uw.cs.lil.navi.experiments.plat.resources.InstructionSeqTraceDatasetCreator;
import edu.uw.cs.lil.navi.experiments.plat.resources.InstructionTraceDatasetCreator;
import edu.uw.cs.lil.navi.experiments.plat.resources.LabeledInstructionSeqTraceDatasetCreator;
import edu.uw.cs.lil.navi.experiments.plat.resources.LabeledInstructionTraceDatasetCreator;
import edu.uw.cs.lil.navi.experiments.plat.resources.NaviJointTemplatedAbstractLexiconGeneratorCreator;
import edu.uw.cs.lil.navi.experiments.plat.resources.NaviLearningRelaxedValidatorCreator;
import edu.uw.cs.lil.navi.experiments.plat.resources.NaviLearningValidatorCreator;
import edu.uw.cs.lil.navi.experiments.plat.resources.NaviLearningWeakValidatorCreator;
import edu.uw.cs.lil.navi.experiments.plat.resources.NaviNaiveSeqExecutorCreator;
import edu.uw.cs.lil.navi.experiments.plat.resources.NaviPairValidatorWrapperCreator;
import edu.uw.cs.lil.navi.experiments.plat.resources.NaviSeqExecutorCreator;
import edu.uw.cs.lil.navi.experiments.plat.resources.NaviSingleExecutorCreator;
import edu.uw.cs.lil.navi.experiments.plat.resources.NaviTemplatedAbstractLexiconGeneratorCreator;
import edu.uw.cs.lil.navi.experiments.plat.resources.NaviTemplatedLexiconGeneratorCreator;
import edu.uw.cs.lil.navi.experiments.plat.resources.ReptFeaturesInitCreator;
import edu.uw.cs.lil.navi.experiments.plat.resources.TemplateCountModelInitCreator;
import edu.uw.cs.lil.navi.map.NavigationMap;
import edu.uw.cs.lil.navi.parse.NaviGraphParser;
import edu.uw.cs.lil.tiny.data.IDataItem;
import edu.uw.cs.lil.tiny.data.ILabeledDataItem;
import edu.uw.cs.lil.tiny.data.resources.CompositeDataCollectionCreator;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.data.singlesentence.resources.SingleSentenceDatasetCreator;
import edu.uw.cs.lil.tiny.explat.resources.ResourceCreatorRepository;
import edu.uw.cs.lil.tiny.learn.situated.resources.SituatedValidationPerceptronCreator;
import edu.uw.cs.lil.tiny.learn.situated.resources.SituatedValidationStocGradCreator;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.mr.lambda.ccg.SimpleFullParseFilter;
import edu.uw.cs.lil.tiny.parser.ccg.cky.genlex.MarkAwareCKYBinaryParsingRule;
import edu.uw.cs.lil.tiny.parser.ccg.cky.multi.MultiCKYParser;
import edu.uw.cs.lil.tiny.parser.ccg.factoredlex.resources.FactoredLexiconCreator;
import edu.uw.cs.lil.tiny.parser.ccg.factoredlex.resources.FactoredUniformScorerCreator;
import edu.uw.cs.lil.tiny.parser.ccg.factoredlex.resources.LexemeCooccurrenceScorerCreator;
import edu.uw.cs.lil.tiny.parser.ccg.factoredlex.resources.LexemeFeatureSetCreator;
import edu.uw.cs.lil.tiny.parser.ccg.factoredlex.resources.LexicalEntryLexemeBasedScorerCreator;
import edu.uw.cs.lil.tiny.parser.ccg.factoredlex.resources.LexicalTemplateFeatureSetCreator;
import edu.uw.cs.lil.tiny.parser.ccg.features.basic.resources.ExpLengthLexicalEntryScorerCreator;
import edu.uw.cs.lil.tiny.parser.ccg.features.basic.resources.LexicalFeatureSetCreator;
import edu.uw.cs.lil.tiny.parser.ccg.features.basic.resources.OriginLexicalEntryScorerCreator;
import edu.uw.cs.lil.tiny.parser.ccg.features.basic.resources.RuleUsageFeatureSetCreator;
import edu.uw.cs.lil.tiny.parser.ccg.features.basic.resources.SkippingSensitiveLexicalEntryScorerCreator;
import edu.uw.cs.lil.tiny.parser.ccg.features.basic.resources.UniformScorerCreator;
import edu.uw.cs.lil.tiny.parser.ccg.features.lambda.resources.LogicalExpressionCooccurrenceFeatureSetCreator;
import edu.uw.cs.lil.tiny.parser.ccg.features.lambda.resources.LogicalExpressionCoordinationFeatureSetCreator;
import edu.uw.cs.lil.tiny.parser.ccg.features.lambda.resources.LogicalExpressionTypeFeatureSetCreator;
import edu.uw.cs.lil.tiny.parser.ccg.joint.cky.resources.ChartLoggerCreator;
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
import edu.uw.cs.lil.tiny.parser.joint.resources.JointModelCreator;
import edu.uw.cs.lil.tiny.parser.resources.LexiconCreator;
import edu.uw.cs.lil.tiny.parser.resources.LexiconModelInitCreator;
import edu.uw.cs.lil.tiny.parser.resources.ModelLoggerCreator;
import edu.uw.cs.lil.tiny.test.exec.resources.ExecTesterCreator;
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
		
		registerResourceCreator(new NavigationMap.Creator());
		registerResourceCreator(new LexiconModelInitCreator<Sentence, LogicalExpression>());
		registerResourceCreator(new SingleSentenceDatasetCreator());
		registerResourceCreator(new TemplateCountModelInitCreator());
		registerResourceCreator(new ReptFeaturesInitCreator<Sentence>());
		registerResourceCreator(new UniformScorerCreator<LogicalExpression>());
		registerResourceCreator(new ExpLengthLexicalEntryScorerCreator<LogicalExpression>());
		registerResourceCreator(new FactoredUniformScorerCreator());
		registerResourceCreator(new LexemeFeatureSetCreator<IDataItem<Pair<Sentence, Task>>>());
		registerResourceCreator(new LexicalFeatureSetCreator<IDataItem<Pair<Sentence, Task>>, LogicalExpression>());
		registerResourceCreator(new LexicalTemplateFeatureSetCreator<IDataItem<Pair<Sentence, Task>>>());
		registerResourceCreator(new LogicalExpressionCoordinationFeatureSetCreator<Sentence>());
		registerResourceCreator(new LogicalExpressionCooccurrenceFeatureSetCreator<Sentence>());
		registerResourceCreator(new LogicalExpressionTypeFeatureSetCreator<Sentence>());
		registerResourceCreator(new LexemeCooccurrenceScorerCreator());
		registerResourceCreator(new SkippingSensitiveLexicalEntryScorerCreator<LogicalExpression>());
		registerResourceCreator(new LexicalEntryLexemeBasedScorerCreator());
		registerResourceCreator(new LabeledInstructionTraceDatasetCreator<LogicalExpression>());
		registerResourceCreator(new InstructionTraceDatasetCreator<LogicalExpression>());
		registerResourceCreator(new JointModelCreator<IDataItem<Pair<Sentence, Task>>, Task, LogicalExpression, Trace>());
		registerResourceCreator(new LexiconCreator<LogicalExpression>());
		registerResourceCreator(new FactoredLexiconCreator());
		registerResourceCreator(new ExecutionFeatureSetCreator());
		registerResourceCreator(new NaviTemplatedLexiconGeneratorCreator());
		registerResourceCreator(new NaviTemplatedAbstractLexiconGeneratorCreator());
		registerResourceCreator(new NaviJointTemplatedAbstractLexiconGeneratorCreator());
		registerResourceCreator(new SituatedValidationPerceptronCreator<Task, LogicalExpression, Trace, Trace, InstructionTrace<LogicalExpression>>(
				"learner.trc"));
		registerResourceCreator(new SituatedValidationStocGradCreator<Task, LogicalExpression, Trace, Trace, InstructionTrace<LogicalExpression>>(
				"learner.stocgrad.trc"));
		registerResourceCreator(new OriginLexicalEntryScorerCreator<LogicalExpression>());
		registerResourceCreator(new ExecTesterCreator<Pair<Sentence, Task>, Pair<LogicalExpression, Trace>>());
		registerResourceCreator(new NaviSingleExecutorCreator());
		registerResourceCreator(new LabeledInstructionSeqTraceDatasetCreator<LogicalExpression>());
		registerResourceCreator(new InstructionSeqTraceDatasetCreator<LogicalExpression>());
		registerResourceCreator(new RuleUsageFeatureSetCreator<IDataItem<Pair<Sentence, Task>>, LogicalExpression>());
		registerResourceCreator(new NaviSeqExecutorCreator());
		registerResourceCreator(new NaviNaiveSeqExecutorCreator());
		registerResourceCreator(new ModelLoggerCreator());
		registerResourceCreator(new NaviLearningValidatorCreator<LogicalExpression>());
		registerResourceCreator(new NaviPairValidatorWrapperCreator<LogicalExpression>());
		registerResourceCreator(new NaviLearningRelaxedValidatorCreator<LogicalExpression>());
		registerResourceCreator(new NaviLearningWeakValidatorCreator<LogicalExpression>());
		registerResourceCreator(new ChartLoggerCreator<Trace, Trace>());
		registerResourceCreator(new ExecTesterCreator<Pair<List<Sentence>, Task>, List<Pair<LogicalExpression, Trace>>>(
				"tester.exec.set"));
		registerResourceCreator(new CompositeDataCollectionCreator<ILabeledDataItem<Pair<Sentence, Task>, Pair<LogicalExpression, Trace>>>(
				"data.composite.ccgtrc"));
		registerResourceCreator(new CompositeDataCollectionCreator<ILabeledDataItem<Pair<List<Sentence>, Task>, List<Pair<LogicalExpression, Trace>>>>(
				"data.composite.ccgsettrc"));
		registerResourceCreator(new CompositeDataCollectionCreator<ILabeledDataItem<Pair<Sentence, Task>, Trace>>(
				"data.composite.trc"));
		registerResourceCreator(new CompositeDataCollectionCreator<ILabeledDataItem<Pair<List<Sentence>, Task>, List<Trace>>>(
				"data.composite.settrc"));
		
	}
}
