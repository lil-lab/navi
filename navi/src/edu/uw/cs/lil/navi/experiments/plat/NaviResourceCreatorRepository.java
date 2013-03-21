package edu.uw.cs.lil.navi.experiments.plat;

import java.util.List;

import edu.uw.cs.lil.navi.data.Trace;
import edu.uw.cs.lil.navi.eval.Task;
import edu.uw.cs.lil.navi.experiments.plat.resources.ExecutionFeatureSetCreator;
import edu.uw.cs.lil.navi.experiments.plat.resources.InstructionSeqTraceDatasetCreator;
import edu.uw.cs.lil.navi.experiments.plat.resources.LabeledInstructionSeqTraceDatasetCreator;
import edu.uw.cs.lil.navi.experiments.plat.resources.LabeledInstructionTraceDatasetCreator;
import edu.uw.cs.lil.navi.experiments.plat.resources.NaviJointTemplatedAbstractLexiconGeneratorCreator;
import edu.uw.cs.lil.navi.experiments.plat.resources.NaviLearningRelaxedValidatorCreator;
import edu.uw.cs.lil.navi.experiments.plat.resources.NaviLearningValidatorCreator;
import edu.uw.cs.lil.navi.experiments.plat.resources.NaviLearningWeakValidatorCreator;
import edu.uw.cs.lil.navi.experiments.plat.resources.NaviNaiveSeqExecutorCreator;
import edu.uw.cs.lil.navi.experiments.plat.resources.NaviSeqExecutorCreator;
import edu.uw.cs.lil.navi.experiments.plat.resources.NaviSingleExecutorCreator;
import edu.uw.cs.lil.navi.experiments.plat.resources.NaviTemplatedAbstractLexiconGeneratorCreator;
import edu.uw.cs.lil.navi.experiments.plat.resources.NaviTemplatedLexiconGeneratorCreator;
import edu.uw.cs.lil.navi.experiments.plat.resources.ReptFeaturesInitCreator;
import edu.uw.cs.lil.navi.experiments.plat.resources.InstructionTraceDatasetCreator;
import edu.uw.cs.lil.navi.experiments.plat.resources.TemplateCountModelInitCreator;
import edu.uw.cs.lil.tiny.data.ILabeledDataItem;
import edu.uw.cs.lil.tiny.data.resources.CompositeDatasetCreator;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.data.singlesentence.resources.SingleSentenceDatasetCreator;
import edu.uw.cs.lil.tiny.explat.resources.ResourceCreatorRepository;
import edu.uw.cs.lil.tiny.learn.weakp.resources.JointValidationSensitivePerceptronCreator;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
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
import edu.uw.cs.lil.tiny.parser.joint.model.JointModelCreator;
import edu.uw.cs.lil.tiny.parser.resources.LexiconCreator;
import edu.uw.cs.lil.tiny.parser.resources.LexiconModelInitCreator;
import edu.uw.cs.lil.tiny.parser.resources.ModelLoggerCreator;
import edu.uw.cs.lil.tiny.test.exec.resources.ExecTesterCreator;
import edu.uw.cs.utils.composites.Pair;

public class NaviResourceCreatorRepository extends ResourceCreatorRepository {
	public NaviResourceCreatorRepository() {
		super();
		// Register default available resources
		registerResourceCreator(new LexiconModelInitCreator<Sentence, LogicalExpression>());
		registerResourceCreator(new SingleSentenceDatasetCreator());
		registerResourceCreator(new TemplateCountModelInitCreator());
		registerResourceCreator(new ReptFeaturesInitCreator<Sentence>());
		registerResourceCreator(new UniformScorerCreator<LogicalExpression>());
		registerResourceCreator(new ExpLengthLexicalEntryScorerCreator<LogicalExpression>());
		registerResourceCreator(new FactoredUniformScorerCreator());
		registerResourceCreator(new LexemeFeatureSetCreator<Pair<Sentence, Task>>());
		registerResourceCreator(new LexicalFeatureSetCreator<Pair<Sentence, Task>, LogicalExpression>());
		registerResourceCreator(new LexicalTemplateFeatureSetCreator<Pair<Sentence, Task>>());
		registerResourceCreator(new LogicalExpressionCoordinationFeatureSetCreator<Sentence>());
		registerResourceCreator(new LogicalExpressionCooccurrenceFeatureSetCreator<Sentence>());
		registerResourceCreator(new LogicalExpressionTypeFeatureSetCreator<Sentence>());
		registerResourceCreator(new LexemeCooccurrenceScorerCreator());
		registerResourceCreator(new SkippingSensitiveLexicalEntryScorerCreator<LogicalExpression>());
		registerResourceCreator(new LexicalEntryLexemeBasedScorerCreator());
		registerResourceCreator(new LabeledInstructionTraceDatasetCreator<LogicalExpression>());
		registerResourceCreator(new InstructionTraceDatasetCreator<LogicalExpression>());
		registerResourceCreator(new JointModelCreator<Sentence, Task, LogicalExpression, Trace>());
		registerResourceCreator(new LexiconCreator<LogicalExpression>());
		registerResourceCreator(new FactoredLexiconCreator());
		registerResourceCreator(new ExecutionFeatureSetCreator());
		registerResourceCreator(new NaviTemplatedLexiconGeneratorCreator());
		registerResourceCreator(new NaviTemplatedAbstractLexiconGeneratorCreator());
		registerResourceCreator(new NaviJointTemplatedAbstractLexiconGeneratorCreator());
		registerResourceCreator(new JointValidationSensitivePerceptronCreator<Task, LogicalExpression, Trace, Trace>(
				"learner.trc"));
		registerResourceCreator(new OriginLexicalEntryScorerCreator<LogicalExpression>());
		registerResourceCreator(new ExecTesterCreator<Pair<Sentence, Task>, Pair<LogicalExpression, Trace>>());
		registerResourceCreator(new NaviSingleExecutorCreator());
		registerResourceCreator(new LabeledInstructionSeqTraceDatasetCreator<LogicalExpression>());
		registerResourceCreator(new InstructionSeqTraceDatasetCreator<LogicalExpression>());
		registerResourceCreator(new RuleUsageFeatureSetCreator<Pair<Sentence, Task>, Pair<LogicalExpression, Trace>>());
		registerResourceCreator(new NaviSeqExecutorCreator());
		registerResourceCreator(new NaviNaiveSeqExecutorCreator());
		registerResourceCreator(new ModelLoggerCreator());
		registerResourceCreator(new NaviLearningValidatorCreator());
		registerResourceCreator(new NaviLearningRelaxedValidatorCreator());
		registerResourceCreator(new NaviLearningWeakValidatorCreator());
		registerResourceCreator(new ChartLoggerCreator<Trace, Trace>());
		registerResourceCreator(new ExecTesterCreator<Pair<List<Sentence>, Task>, List<Pair<LogicalExpression, Trace>>>(
				"tester.exec.set"));
		registerResourceCreator(new CompositeDatasetCreator<ILabeledDataItem<Pair<Sentence, Task>, Pair<LogicalExpression, Trace>>>(
				"data.composite.ccgtrc"));
		registerResourceCreator(new CompositeDatasetCreator<ILabeledDataItem<Pair<List<Sentence>, Task>, List<Pair<LogicalExpression, Trace>>>>(
				"data.composite.ccgsettrc"));
		registerResourceCreator(new CompositeDatasetCreator<ILabeledDataItem<Pair<Sentence, Task>, Trace>>(
				"data.composite.trc"));
		registerResourceCreator(new CompositeDatasetCreator<ILabeledDataItem<Pair<List<Sentence>, Task>, List<Trace>>>(
				"data.composite.settrc"));
		
	}
}
