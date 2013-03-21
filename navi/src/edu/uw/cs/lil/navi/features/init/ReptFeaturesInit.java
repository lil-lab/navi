package edu.uw.cs.lil.navi.features.init;

import edu.uw.cs.lil.tiny.mr.lambda.LogicLanguageServices;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalConstant;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.mr.lambda.Ontology;
import edu.uw.cs.lil.tiny.parser.ccg.model.IModelInit;
import edu.uw.cs.lil.tiny.parser.ccg.model.Model;
import edu.uw.cs.lil.tiny.utils.hashvector.IHashVector;

/**
 * Init conjunction REPT features.
 * 
 * @author Yoav Artzi
 * @param <X>
 * @param <Y>
 */
public class ReptFeaturesInit<X> implements IModelInit<X, LogicalExpression> {
	
	private final String	featureName;
	private final String	featureTag;
	private final double	initWeight;
	private final Ontology	ontology;
	
	public ReptFeaturesInit(String featureTag, String featureName,
			double initWeight, Ontology ontology) {
		this.featureTag = featureTag;
		this.featureName = featureName;
		this.initWeight = initWeight;
		this.ontology = ontology;
	}
	
	@Override
	public void init(Model<X, LogicalExpression> model) {
		final IHashVector theta = model.getTheta();
		for (final LogicalConstant pred : ontology.getAllPredicates()) {
			theta.set(featureTag, featureName, LogicLanguageServices
					.getConjunctionPredicate().toString(), pred.toString(),
					initWeight);
		}
	}
	
}
