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
package edu.uw.cs.lil.navi.exec;

import edu.uw.cs.lil.navi.data.Trace;
import edu.uw.cs.lil.navi.eval.Task;
import edu.uw.cs.lil.tiny.data.IDataItem;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.exec.IExec;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.parser.joint.IJointParser;
import edu.uw.cs.lil.tiny.parser.joint.exec.JointExecutionOutput;
import edu.uw.cs.lil.tiny.parser.joint.model.IJointDataItemModel;
import edu.uw.cs.lil.tiny.parser.joint.model.JointModel;
import edu.uw.cs.utils.composites.Pair;

public class NaviSingleExecutor implements
		IExec<Pair<Sentence, Task>, Pair<LogicalExpression, Trace>> {
	
	private final JointModel<IDataItem<Pair<Sentence, Task>>, Task, LogicalExpression, Trace>	model;
	private final IJointParser<Sentence, Task, LogicalExpression, Trace, Trace>					parser;
	private final boolean																		pruneFails;
	
	public NaviSingleExecutor(
			IJointParser<Sentence, Task, LogicalExpression, Trace, Trace> parser,
			JointModel<IDataItem<Pair<Sentence, Task>>, Task, LogicalExpression, Trace> model,
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
