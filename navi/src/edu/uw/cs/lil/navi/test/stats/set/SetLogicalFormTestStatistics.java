package edu.uw.cs.lil.navi.test.stats.set;

import java.util.List;

import edu.uw.cs.lil.navi.data.Trace;
import edu.uw.cs.lil.navi.eval.Task;
import edu.uw.cs.lil.tiny.data.IDataItem;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.test.stats.ITestingStatistics;
import edu.uw.cs.utils.collections.ListUtils;
import edu.uw.cs.utils.composites.Pair;

public class SetLogicalFormTestStatistics<Y> implements
		ITestingStatistics<Pair<List<Sentence>, Task>, List<Pair<Y, Trace>>> {
	
	private final ITestingStatistics<Pair<List<Sentence>, Task>, List<Y>>	baseStats;
	
	public SetLogicalFormTestStatistics(
			ITestingStatistics<Pair<List<Sentence>, Task>, List<Y>> baseStats) {
		this.baseStats = baseStats;
	}
	
	private static <Y> List<Y> getLogicalFormList(List<Pair<Y, Trace>> result) {
		return ListUtils.map(result, new ListUtils.Mapper<Pair<Y, Trace>, Y>() {
			@Override
			public Y process(Pair<Y, Trace> obj) {
				return obj.first();
			}
		});
	}
	
	@Override
	public void recordNoParse(IDataItem<Pair<List<Sentence>, Task>> dataItem,
			List<Pair<Y, Trace>> gold) {
		baseStats.recordNoParse(dataItem, getLogicalFormList(gold));
	}
	
	@Override
	public void recordNoParseWithSkipping(
			IDataItem<Pair<List<Sentence>, Task>> dataItem,
			List<Pair<Y, Trace>> gold) {
		baseStats.recordNoParseWithSkipping(dataItem, getLogicalFormList(gold));
	}
	
	@Override
	public void recordParse(IDataItem<Pair<List<Sentence>, Task>> dataItem,
			List<Pair<Y, Trace>> gold, List<Pair<Y, Trace>> label) {
		baseStats.recordParse(dataItem, getLogicalFormList(gold),
				getLogicalFormList(label));
	}
	
	@Override
	public void recordParses(IDataItem<Pair<List<Sentence>, Task>> dataItem,
			List<Pair<Y, Trace>> gold, List<List<Pair<Y, Trace>>> labels) {
		baseStats.recordNoParse(dataItem, getLogicalFormList(gold));
	}
	
	@Override
	public void recordParsesWithSkipping(
			IDataItem<Pair<List<Sentence>, Task>> dataItem,
			List<Pair<Y, Trace>> gold, List<List<Pair<Y, Trace>>> labels) {
		baseStats.recordNoParseWithSkipping(dataItem, getLogicalFormList(gold));
	}
	
	@Override
	public void recordParseWithSkipping(
			IDataItem<Pair<List<Sentence>, Task>> dataItem,
			List<Pair<Y, Trace>> gold, List<Pair<Y, Trace>> label) {
		baseStats.recordParseWithSkipping(dataItem, getLogicalFormList(gold),
				getLogicalFormList(label));
	}
	
	@Override
	public String toString() {
		return baseStats.toString();
	}
	
	@Override
	public String toTabDelimitedString() {
		return baseStats.toTabDelimitedString();
	}
}
