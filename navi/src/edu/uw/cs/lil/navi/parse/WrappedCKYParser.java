package edu.uw.cs.lil.navi.parse;

import edu.uw.cs.lil.tiny.ccg.categories.Category;
import edu.uw.cs.lil.tiny.data.IDataItem;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.parser.AbstractParser;
import edu.uw.cs.lil.tiny.parser.IParserOutput;
import edu.uw.cs.lil.tiny.parser.Pruner;
import edu.uw.cs.lil.tiny.parser.ccg.cky.AbstractCKYParser;
import edu.uw.cs.lil.tiny.parser.ccg.cky.genlex.exact.ExactMarkAwareCellFactory;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.ILexicon;
import edu.uw.cs.lil.tiny.parser.ccg.model.IDataItemModel;
import edu.uw.cs.utils.filter.IFilter;

public class WrappedCKYParser extends
		AbstractParser<Sentence, LogicalExpression> {
	
	private final AbstractCKYParser<LogicalExpression>	ckyParser;
	private final IFilter<Category<LogicalExpression>>	completeParseFilter;
	
	public WrappedCKYParser(AbstractCKYParser<LogicalExpression> ckyParser,
			IFilter<Category<LogicalExpression>> completeParseFilter) {
		this.ckyParser = ckyParser;
		this.completeParseFilter = completeParseFilter;
	}
	
	@Override
	public IParserOutput<LogicalExpression> parse(IDataItem<Sentence> dataItem,
			Pruner<Sentence, LogicalExpression> pruner,
			IDataItemModel<LogicalExpression> model, boolean allowWordSkipping,
			ILexicon<LogicalExpression> tempLexicon, Integer beamSize) {
		return ckyParser.parse(dataItem, pruner, model, allowWordSkipping,
				tempLexicon, beamSize, new ExactMarkAwareCellFactory(model,
						dataItem.getSample().getTokens().size(),
						completeParseFilter));
	}
	
}
