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
package edu.uw.cs.lil.navi.eval;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.uw.cs.lil.navi.agent.Direction;
import edu.uw.cs.lil.navi.eval.literalevaluators.INaviLiteralEvaluator;
import edu.uw.cs.lil.tiny.ccg.categories.ICategoryServices;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalConstant;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.mr.language.type.Type;

public class NaviEvaluationConstants {
	public static final StateFlag								POST_ACTION_STATE	= new StateFlag();
	
	public static final StateFlag								PRE_ACTION_STATE	= new StateFlag();
	private final LogicalExpression								actionExistsQuantifier;
	
	private final Type											actionSeqType;
	private final Set<LogicalConstant>							agentPositionVariantConstants;
	private final LogicalExpression								aQuantifier;
	
	private final LogicalConstant								currentPositionConstant;
	private final LogicalExpression								definiteQuantifier;
	/**
	 * Translates :dir constants to direction objects.
	 */
	private final Map<LogicalExpression, Direction>				directions;
	private final LogicalExpression								entityExistsQuantifier;
	private final Map<Type, LogicalConstant>					equalsPredicates;
	private final Map<LogicalConstant, INaviLiteralEvaluator>	literalEvaluators;
	private final int											maxImplicitActionsPerTurn;
	private final Type											metaEntityType;
	private final LogicalConstant								positionXConstant;
	private final LogicalConstant								positionYConstant;
	private final Map<LogicalExpression, StateFlag>				statefulPredicates;
	private final Map<StateFlag, LogicalConstant>				statefulWrappers;
	private final Map<LogicalConstant, StateFlag>				statefulWrappersToFlags;
	private final Set<Double>									supportedNumbers;
	
	private NaviEvaluationConstants(Type actionSeqType,
			LogicalConstant currentPositionConstant,
			Map<LogicalExpression, Direction> directions,
			Map<LogicalConstant, INaviLiteralEvaluator> literalEvaluators,
			LogicalConstant positionXConstant,
			LogicalConstant positionYConstant,
			Map<LogicalExpression, StateFlag> statefulPredicates,
			LogicalExpression definiteQuantifier,
			LogicalExpression existsQuantifier, LogicalExpression aQuantifier,
			Set<Double> supportedNumbers,
			LogicalExpression actionExistsQuantifier,
			int maxImplicitActionsPerTurn,
			ICategoryServices<LogicalExpression> categoryServices,
			Type metaEntityType, Map<Type, LogicalConstant> equalsPredicates,
			Set<LogicalConstant> agentPositionVariantConstants) {
		this.actionSeqType = actionSeqType;
		this.currentPositionConstant = currentPositionConstant;
		this.directions = directions;
		this.literalEvaluators = literalEvaluators;
		this.positionXConstant = positionXConstant;
		this.positionYConstant = positionYConstant;
		this.statefulPredicates = statefulPredicates;
		this.definiteQuantifier = definiteQuantifier;
		this.entityExistsQuantifier = existsQuantifier;
		this.aQuantifier = aQuantifier;
		this.supportedNumbers = supportedNumbers;
		this.actionExistsQuantifier = actionExistsQuantifier;
		this.maxImplicitActionsPerTurn = maxImplicitActionsPerTurn;
		this.metaEntityType = metaEntityType;
		this.equalsPredicates = equalsPredicates;
		this.agentPositionVariantConstants = agentPositionVariantConstants;
		final Map<StateFlag, LogicalConstant> statefuls = new HashMap<StateFlag, LogicalConstant>();
		final Map<LogicalConstant, StateFlag> statefulsToFlag = new HashMap<LogicalConstant, StateFlag>();
		statefuls.put(POST_ACTION_STATE, (LogicalConstant) categoryServices
				.parseSemantics("POST_STATE_WRAPPER:<a,<t,t>>"));
		statefulsToFlag.put((LogicalConstant) categoryServices
				.parseSemantics("POST_STATE_WRAPPER:<a,<t,t>>"),
				POST_ACTION_STATE);
		statefuls.put(PRE_ACTION_STATE, (LogicalConstant) categoryServices
				.parseSemantics("PRE_STATE_WRAPPER:<a,<t,t>>"));
		statefulsToFlag.put((LogicalConstant) categoryServices
				.parseSemantics("PRE_STATE_WRAPPER:<a,<t,t>>"),
				PRE_ACTION_STATE);
		this.statefulWrappers = Collections.unmodifiableMap(statefuls);
		this.statefulWrappersToFlags = Collections
				.unmodifiableMap(statefulsToFlag);
	}
	
	public Type getActionSeqType() {
		return actionSeqType;
	}
	
	public Set<LogicalConstant> getAgentPositionVariantConstants() {
		return agentPositionVariantConstants;
	}
	
	public Collection<Direction> getAllDirections() {
		return directions.values();
	}
	
	public LogicalExpression getAQuantifier() {
		return aQuantifier;
	}
	
	public LogicalConstant getCurrentPositionConstant() {
		return currentPositionConstant;
	}
	
	public Direction getDirection(LogicalExpression exp) {
		return directions.get(exp);
	}
	
	public LogicalExpression getEntityExistsQuantifier() {
		return entityExistsQuantifier;
	}
	
	public Map<Type, LogicalConstant> getEqualsPredicates() {
		return equalsPredicates;
	}
	
	public INaviLiteralEvaluator getLiteralEvaluator(LogicalExpression predicate) {
		return literalEvaluators.get(predicate);
	}
	
	public int getMaxImplicitActionsPerTurn() {
		return maxImplicitActionsPerTurn;
	}
	
	public Type getMetaEntityType() {
		return metaEntityType;
	}
	
	public LogicalConstant getPositionXConstant() {
		return positionXConstant;
	}
	
	public LogicalConstant getPositionYConstant() {
		return positionYConstant;
	}
	
	public StateFlag getStateFlag(LogicalExpression predicate) {
		return statefulWrappersToFlags.get(predicate);
	}
	
	public LogicalConstant getStatefulWrapper(LogicalExpression predicate) {
		final StateFlag flag = statefulPredicates.get(predicate);
		if (flag == null) {
			return null;
		} else {
			return statefulWrappers.get(flag);
		}
	}
	
	public Set<Double> getSupportedNumbers() {
		return supportedNumbers;
	}
	
	public boolean isDefiniteQuantifier(LogicalExpression predicate) {
		return definiteQuantifier.equals(predicate);
	}
	
	public boolean isExistsQuantifier(LogicalExpression predicate) {
		return entityExistsQuantifier.equals(predicate)
				|| actionExistsQuantifier.equals(predicate);
	}
	
	public boolean isPredicateStateful(LogicalExpression predicate) {
		return statefulPredicates.containsKey(predicate);
	}
	
	public static class Builder {
		private final LogicalExpression								actionExistsQuantifier;
		private final Type											actionSeqType;
		private final Set<LogicalConstant>							agentPositionVariantConstants	= new HashSet<LogicalConstant>();
		private final LogicalExpression								aQuantifier;
		private final ICategoryServices<LogicalExpression>			categoryServices;
		private final LogicalConstant								currentPositionConstant;
		private final LogicalExpression								definiteDeterminer;
		private final Map<LogicalExpression, Direction>				directions						= new HashMap<LogicalExpression, Direction>();
		private final Map<Type, LogicalConstant>					equalsPredicates				= new HashMap<Type, LogicalConstant>();
		private final LogicalExpression								existsQuantifier;
		private final Map<LogicalConstant, INaviLiteralEvaluator>	literalEvaluators				= new HashMap<LogicalConstant, INaviLiteralEvaluator>();
		private final int											maxImplicitActionsPerTurn;
		private final Type											metaEntityType;
		private final LogicalConstant								positionXConstant;
		
		private final LogicalConstant								positionYConstant;
		
		private final Map<LogicalExpression, StateFlag>				statefulPredicates				= new HashMap<LogicalExpression, StateFlag>();
		
		private final Set<Double>									supportedNumbers				= new HashSet<Double>();
		
		public Builder(Type actionSeqType, LogicalConstant positionXConstant,
				LogicalConstant positionYConstant,
				LogicalConstant currentPositionConstant,
				LogicalExpression definiteDeterminer,
				LogicalExpression existsQuantifier,
				LogicalExpression aQuantifier,
				LogicalExpression actionExistsQuantifier,
				int maxImplicitActionsPerTurn,
				ICategoryServices<LogicalExpression> categoryServices,
				Type metaEntityType) {
			this.actionSeqType = actionSeqType;
			this.positionXConstant = positionXConstant;
			this.positionYConstant = positionYConstant;
			this.definiteDeterminer = definiteDeterminer;
			this.existsQuantifier = existsQuantifier;
			this.aQuantifier = aQuantifier;
			this.actionExistsQuantifier = actionExistsQuantifier;
			this.maxImplicitActionsPerTurn = maxImplicitActionsPerTurn;
			this.categoryServices = categoryServices;
			this.metaEntityType = metaEntityType;
			this.currentPositionConstant = currentPositionConstant;
			// Except certain predicates (complex constants) only the current
			// agent position depends on the position of the agent as it changes
			agentPositionVariantConstants.add(currentPositionConstant);
		}
		
		public Builder addDirection(LogicalExpression exp, Direction direction) {
			directions.put(exp, direction);
			return this;
		}
		
		public Builder addEquals(Type type, LogicalConstant predicate) {
			equalsPredicates.put(type, predicate);
			return this;
		}
		
		public Builder addEvaluator(LogicalConstant predicate,
				INaviLiteralEvaluator evaluator) {
			literalEvaluators.put(predicate, evaluator);
			if (evaluator.agentDependent()) {
				agentPositionVariantConstants.add(predicate);
			} else {
				agentPositionVariantConstants.remove(predicate);
			}
			return this;
		}
		
		public Builder addNumber(double num) {
			supportedNumbers.add(num);
			return this;
		}
		
		public Builder addStatefulPredicate(LogicalExpression predicate,
				StateFlag stateFlag) {
			statefulPredicates.put(predicate, stateFlag);
			return this;
		}
		
		public NaviEvaluationConstants build() {
			return new NaviEvaluationConstants(actionSeqType,
					currentPositionConstant,
					Collections.unmodifiableMap(directions),
					Collections.unmodifiableMap(literalEvaluators),
					positionXConstant, positionYConstant, statefulPredicates,
					definiteDeterminer, existsQuantifier, aQuantifier,
					Collections.unmodifiableSet(supportedNumbers),
					actionExistsQuantifier, maxImplicitActionsPerTurn,
					categoryServices, metaEntityType,
					Collections.unmodifiableMap(equalsPredicates),
					Collections.unmodifiableSet(agentPositionVariantConstants));
		}
	}
	
	public static class StateFlag {
		private StateFlag() {
		}
	}
	
}
