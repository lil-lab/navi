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
package edu.uw.cs.lil.navi.features;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import edu.uw.cs.lil.navi.eval.NaviSingleEvaluator;
import edu.uw.cs.lil.navi.eval.Task;
import edu.uw.cs.lil.navi.map.PositionSet;
import edu.uw.cs.lil.tiny.ccg.categories.Category;
import edu.uw.cs.lil.tiny.data.IDataItem;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.mr.lambda.LogicLanguageServices;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.mr.lambda.exec.naive.ILambdaResult;
import edu.uw.cs.lil.tiny.mr.lambda.visitor.GetConstantsSet;
import edu.uw.cs.lil.tiny.mr.language.type.Type;
import edu.uw.cs.lil.tiny.parser.ccg.ILexicalParseStep;
import edu.uw.cs.lil.tiny.parser.ccg.IParseStep;
import edu.uw.cs.lil.tiny.parser.ccg.model.parse.IParseFeatureSet;
import edu.uw.cs.lil.tiny.parser.joint.model.SituatedDataItemWrapper;
import edu.uw.cs.lil.tiny.utils.hashvector.HashVectorFactory;
import edu.uw.cs.lil.tiny.utils.hashvector.IHashVector;
import edu.uw.cs.lil.tiny.utils.hashvector.IHashVectorImmutable;
import edu.uw.cs.lil.tiny.utils.hashvector.KeyArgs;
import edu.uw.cs.utils.collections.SetUtils;
import edu.uw.cs.utils.composites.Pair;
import edu.uw.cs.utils.composites.Triplet;
import edu.uw.cs.utils.log.ILogger;
import edu.uw.cs.utils.log.LoggerFactory;

/**
 * Execution features that consider only lexical entries, execute their logical
 * expressions (with the Task from the data item) and produce features based on
 * the result (only in the case of evaluation "failures").
 * 
 * @author Yoav Artzi
 */
public class ExecutionFeatureSet implements
		IParseFeatureSet<Sentence, LogicalExpression> {
	private static final String									FEATURE_TAG			= "LEX_EXEC";
	private static final ILogger								LOG					= LoggerFactory
																							.create(ExecutionFeatureSet.class);
	private static final Object									NULL_PLACEHOLDER	= new Object();
	
	private final Cache<Pair<LogicalExpression, Task>, Object>	cache;
	private final NaviSingleEvaluator							evaluator;
	private final double										scale;
	private final Set<Type>										validTypes;
	
	public ExecutionFeatureSet(NaviSingleEvaluator evaluator, int cacheSize,
			double scale) {
		this.evaluator = evaluator;
		this.scale = scale;
		this.cache = CacheBuilder.newBuilder().maximumSize(cacheSize).build();
		final Set<Type> types = new HashSet<Type>();
		types.add(LogicLanguageServices.getTypeRepository().getTruthValueType());
		types.add(LogicLanguageServices.getTypeRepository().getEntityType());
		types.add(LogicLanguageServices.getTypeRepository()
				.getTypeCreateIfNeeded(
						LogicLanguageServices.getTypeRepository()
								.getTruthValueType(),
						LogicLanguageServices.getTypeRepository()
								.getEntityType()));
		this.validTypes = Collections.unmodifiableSet(types);
	}
	
	@Override
	public List<Triplet<KeyArgs, Double, String>> getFeatureWeights(
			IHashVector theta) {
		final List<Triplet<KeyArgs, Double, String>> weights = new LinkedList<Triplet<KeyArgs, Double, String>>();
		for (final Pair<KeyArgs, Double> feature : theta.getAll(FEATURE_TAG)) {
			weights.add(Triplet.of(feature.first(), feature.second(),
					(String) null));
		}
		return weights;
	}
	
	@Override
	public boolean isValidWeightVector(IHashVectorImmutable update) {
		// No protected features
		return true;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public double score(IParseStep<LogicalExpression> obj, IHashVector theta,
			IDataItem<Sentence> dataItem) {
		if (shouldComputeFeatures(obj, dataItem)) {
			return setFeats(
					obj.getRoot(),
					HashVectorFactory.create(),
					((SituatedDataItemWrapper<Sentence, Task>) dataItem)
							.getBaseDataItem()).vectorMultiply(theta);
		} else {
			return 0;
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void setFeats(IParseStep<LogicalExpression> obj, IHashVector feats,
			IDataItem<Sentence> dataItem) {
		if (shouldComputeFeatures(obj, dataItem)) {
			setFeats(obj.getRoot(), feats,
					((SituatedDataItemWrapper<Sentence, Task>) dataItem)
							.getBaseDataItem());
			
		}
	}
	
	private IHashVector setFeats(final Category<LogicalExpression> category,
			IHashVector feats, final IDataItem<Pair<Sentence, Task>> dataItem) {
		final LogicalExpression exp = category.getSem();
		try {
			final boolean evaluable = evaluator.isEvaluable(exp, dataItem
					.getSample().second());
			if (evaluable) {
				// Evaluate only if result is not cached
				final Object evalResult = cache.get(
						Pair.of(exp, dataItem.getSample().second()),
						new Callable<Object>() {
							
							@Override
							public Object call() throws Exception {
								final Object result = evaluator.of(exp,
										dataItem.getSample().second(), true);
								// The cache can't handle null values, so we use
								// a static place-holder
								return result == null ? NULL_PLACEHOLDER
										: result;
							}
						});
				
				// Test of various evaluation failures and create feature as
				// needed
				if (evalResult == NULL_PLACEHOLDER) {
					feats.set(FEATURE_TAG, LogicLanguageServices
							.getTypeRepository().generalizeType(exp.getType())
							.toString(), "null", 1.0 * scale);
				} else if (evalResult instanceof ILambdaResult
						&& ((ILambdaResult) evalResult).size() == 0) {
					feats.set(FEATURE_TAG, LogicLanguageServices
							.getTypeRepository().generalizeType(exp.getType())
							.toString(), "set", "empty", 1.0 * scale);
				} else if (Boolean.FALSE.equals(evalResult)) {
					feats.set(FEATURE_TAG, LogicLanguageServices
							.getTypeRepository().generalizeType(exp.getType())
							.toString(), "boolean", "false", 1.0 * scale);
				} else if (evalResult instanceof PositionSet
						&& ((PositionSet) evalResult).size() == 0) {
					feats.set(FEATURE_TAG, LogicLanguageServices
							.getTypeRepository().generalizeType(exp.getType())
							.toString(), "positionset", "empty", 1.0 * scale);
				}
			}
			return feats;
		} catch (final ExecutionException e) {
			LOG.error("Unexpected exception during evaluation: %s", exp);
			throw new RuntimeException(e);
		}
	}
	
	private boolean shouldComputeFeatures(IParseStep<LogicalExpression> obj,
			IDataItem<Sentence> dataItem) {
		return obj.getRoot().getSem() != null
				&& obj instanceof ILexicalParseStep
				&& dataItem instanceof SituatedDataItemWrapper
				&& validTypes.contains(LogicLanguageServices
						.getTypeRepository().generalizeType(
								obj.getRoot().getSem().getType()))
				&& !SetUtils.isIntersecting(evaluator.getServicesFactory()
						.getNaviEvaluationConsts()
						.getAgentPositionVariantConstants(),
						GetConstantsSet.of(obj.getRoot().getSem()));
	}
}
