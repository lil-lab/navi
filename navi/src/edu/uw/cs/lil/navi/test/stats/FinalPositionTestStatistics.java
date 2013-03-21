package edu.uw.cs.lil.navi.test.stats;

import java.util.List;

import edu.uw.cs.lil.navi.data.Trace;
import edu.uw.cs.lil.navi.eval.Task;
import edu.uw.cs.lil.navi.map.Position;
import edu.uw.cs.lil.tiny.data.IDataItem;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.test.stats.ITestingStatistics;
import edu.uw.cs.utils.composites.Pair;

public class FinalPositionTestStatistics
		implements
		ITestingStatistics<Pair<Sentence, Task>, Pair<LogicalExpression, Trace>> {
	
	private final ITestingStatistics<Pair<Sentence, Task>, Position>	baseStats;
	
	public FinalPositionTestStatistics(
			ITestingStatistics<Pair<Sentence, Task>, Position> baseStats) {
		this.baseStats = baseStats;
	}
	
	@Override
	public void recordNoParse(IDataItem<Pair<Sentence, Task>> dataItem,
			Pair<LogicalExpression, Trace> gold) {
		baseStats.recordNoParse(dataItem, gold.second().getEndPosition());
		
	}
	
	@Override
	public void recordNoParseWithSkipping(
			IDataItem<Pair<Sentence, Task>> dataItem,
			Pair<LogicalExpression, Trace> gold) {
		baseStats.recordNoParseWithSkipping(dataItem, gold.second()
				.getEndPosition());
	}
	
	@Override
	public void recordParse(IDataItem<Pair<Sentence, Task>> dataItem,
			Pair<LogicalExpression, Trace> gold,
			Pair<LogicalExpression, Trace> label) {
		if (label.second() == null) {
			baseStats.recordNoParse(dataItem, gold.second().getEndPosition());
		} else {
			baseStats.recordParse(dataItem, gold.second().getEndPosition(),
					label.second().getEndPosition());
		}
	}
	
	@Override
	public void recordParses(IDataItem<Pair<Sentence, Task>> dataItem,
			Pair<LogicalExpression, Trace> gold,
			List<Pair<LogicalExpression, Trace>> labels) {
		final Position position = clusterPositions(labels);
		if (position == null) {
			baseStats.recordNoParse(dataItem, gold.second().getEndPosition());
		} else {
			baseStats.recordParse(dataItem, gold.second().getEndPosition(),
					position);
		}
	}
	
	@Override
	public void recordParsesWithSkipping(
			IDataItem<Pair<Sentence, Task>> dataItem,
			Pair<LogicalExpression, Trace> gold,
			List<Pair<LogicalExpression, Trace>> labels) {
		final Position position = clusterPositions(labels);
		if (position == null) {
			baseStats.recordNoParseWithSkipping(dataItem, gold.second()
					.getEndPosition());
		} else {
			baseStats.recordParseWithSkipping(dataItem, gold.second()
					.getEndPosition(), position);
		}
	}
	
	@Override
	public void recordParseWithSkipping(
			IDataItem<Pair<Sentence, Task>> dataItem,
			Pair<LogicalExpression, Trace> gold,
			Pair<LogicalExpression, Trace> label) {
		if (label.second() == null) {
			baseStats.recordNoParseWithSkipping(dataItem, gold.second()
					.getEndPosition());
		} else {
			baseStats.recordParseWithSkipping(dataItem, gold.second()
					.getEndPosition(), label.second().getEndPosition());
		}
	}
	
	@Override
	public String toString() {
		return baseStats.toString();
	}
	
	@Override
	public String toTabDelimitedString() {
		return baseStats.toTabDelimitedString();
	}
	
	private Position clusterPositions(
			List<Pair<LogicalExpression, Trace>> labels) {
		Position position = null;
		for (final Pair<LogicalExpression, Trace> label : labels) {
			if (label.second() == null) {
				return null;
			} else if (position == null) {
				position = label.second().getEndPosition();
			} else if (!position.equals(label.second().getEndPosition())) {
				return null;
			}
		}
		return position;
	}
}
