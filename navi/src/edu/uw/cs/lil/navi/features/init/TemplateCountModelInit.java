package edu.uw.cs.lil.navi.features.init;

import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.parser.ccg.factoredlex.FactoredLexicon.FactoredLexicalEntry;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.ILexicon;
import edu.uw.cs.lil.tiny.parser.ccg.lexicon.LexicalEntry;
import edu.uw.cs.lil.tiny.parser.ccg.model.IModelInit;
import edu.uw.cs.lil.tiny.parser.ccg.model.Model;
import edu.uw.cs.lil.tiny.utils.hashvector.HashVectorFactory;
import edu.uw.cs.lil.tiny.utils.hashvector.IHashVector;

/**
 * Init lexical template feature weights given the number of times these
 * features are used in a given lexicon.
 * 
 * @author Yoav Artzi
 */
public class TemplateCountModelInit implements
		IModelInit<Sentence, LogicalExpression> {
	
	private final String						lexicalTemplateFeatureTag;
	private final ILexicon<LogicalExpression>	lexicon;
	
	public TemplateCountModelInit(ILexicon<LogicalExpression> lexicon,
			String lexicalTemplateFeatureTag) {
		this.lexicon = lexicon;
		this.lexicalTemplateFeatureTag = lexicalTemplateFeatureTag;
	}
	
	@Override
	public void init(Model<Sentence, LogicalExpression> model) {
		final IHashVector theta = model.getTheta();
		
		final IHashVector templateFeatureCounts = HashVectorFactory.create();
		
		// Iterate over the lexical entries
		for (final LexicalEntry<LogicalExpression> entry : lexicon
				.toCollection()) {
			
			// Only handle factored entries, should only get such
			if (entry instanceof FactoredLexicalEntry) {
				// Compute the features of the entry
				final IHashVector features = model.computeFeatures(entry);
				// Extract all lexical template features from the features over
				// the entry
				final IHashVector feats = features
						.getAll(lexicalTemplateFeatureTag);
				// Increase the counters
				feats.addTimesInto(1.0, templateFeatureCounts);
			} else {
				throw new IllegalStateException("expected a factored entry");
			}
		}
		
		// Add the features into theta
		templateFeatureCounts.addTimesInto(1.0, theta);
	}
	
}
