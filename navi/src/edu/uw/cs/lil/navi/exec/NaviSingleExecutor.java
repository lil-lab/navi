package edu.uw.cs.lil.navi.exec;

import edu.uw.cs.lil.navi.data.Trace;
import edu.uw.cs.lil.navi.eval.Task;
import edu.uw.cs.lil.navi.parse.NaviParser;
import edu.uw.cs.lil.tiny.data.IDataItem;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.exec.IExec;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.parser.joint.exec.JointExecutionOutput;
import edu.uw.cs.lil.tiny.parser.joint.model.IJointDataItemModel;
import edu.uw.cs.lil.tiny.parser.joint.model.JointModel;
import edu.uw.cs.utils.composites.Pair;

public class NaviSingleExecutor implements
		IExec<Pair<Sentence, Task>, Pair<LogicalExpression, Trace>> {
	
	private final JointModel<Sentence, Task, LogicalExpression, Trace>	model;
	private final NaviParser											parser;
	private final boolean												pruneFails;
	
	public NaviSingleExecutor(NaviParser parser,
			JointModel<Sentence, Task, LogicalExpression, Trace> model,
			boolean pruneActionless) {
		this.parser = parser;
		this.model = model;
		this.pruneFails = pruneActionless;
	}
	
	@Override
	public JointExecutionOutput<LogicalExpression, Trace> execute(
			IDataItem<Pair<Sentence, Task>> dataItem) {
		return execute(dataItem, false);
	}
	
	@Override
	public JointExecutionOutput<LogicalExpression, Trace> execute(
			IDataItem<Pair<Sentence, Task>> dataItem, boolean sloppy) {
		final IJointDataItemModel<LogicalExpression, Trace> dataItemModel = model
				.createJointDataItemModel(dataItem);
		return new JointExecutionOutput<LogicalExpression, Trace>(parser.parse(
				dataItem, dataItemModel, sloppy), dataItemModel, pruneFails);
	}
	
}
