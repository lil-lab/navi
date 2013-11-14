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

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.uw.cs.lil.navi.agent.Direction;
import edu.uw.cs.lil.navi.eval.literalevaluators.INaviLiteralEvaluator;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalConstant;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.mr.language.type.Type;

public class NaviEvaluationConstants implements Serializable {
	private final LogicalConstant								actionExistsQuantifier;
	
	private final Type											actionSeqType;
	private final Set<LogicalConstant>							agentPositionVariantConstants;
	private final LogicalConstant								aQuantifier;
	
	private final LogicalConstant								currentPositionConstant;
	private final LogicalConstant								definiteQuantifier;
	/**
	 * Translates :dir constants to direction objects.
	 */
	private final Map<LogicalExpression, Direction>				directions;
	private final LogicalConstant								entityExistsQuantifier;
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
			LogicalConstant definiteQuantifier,
			LogicalConstant existsQuantifier, LogicalConstant aQuantifier,
			Set<Double> supportedNumbers,
			LogicalConstant actionExistsQuantifier,
			int maxImplicitActionsPerTurn, Type metaEntityType,
			Map<Type, LogicalConstant> equalsPredicates,
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
		statefuls.put(StateFlag.POST,
				LogicalConstant.parse("POST_STATE_WRAPPER:<a,<t,t>>"));
		statefulsToFlag.put(
				LogicalConstant.parse("POST_STATE_WRAPPER:<a,<t,t>>"),
				StateFlag.POST);
		statefuls.put(StateFlag.PRE,
				LogicalConstant.parse("PRE_STATE_WRAPPER:<a,<t,t>>"));
		statefulsToFlag.put(
				LogicalConstant.parse("PRE_STATE_WRAPPER:<a,<t,t>>"),
				StateFlag.PRE);
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
	
	public LogicalConstant getAQuantifier() {
		return aQuantifier;
	}
	
	public LogicalConstant getCurrentPositionConstant() {
		return currentPositionConstant;
	}
	
	public Direction getDirection(LogicalExpression exp) {
		return directions.get(exp);
	}
	
	public LogicalConstant getEntityExistsQuantifier() {
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
		private final LogicalConstant								actionExistsQuantifier;
		private final Type											actionSeqType;
		private final Set<LogicalConstant>							agentPositionVariantConstants	= new HashSet<LogicalConstant>();
		private final LogicalConstant								aQuantifier;
		private final LogicalConstant								currentPositionConstant;
		private final LogicalConstant								definiteDeterminer;
		private final Map<LogicalExpression, Direction>				directions						= new HashMap<LogicalExpression, Direction>();
		private final Map<Type, LogicalConstant>					equalsPredicates				= new HashMap<Type, LogicalConstant>();
		private final LogicalConstant								existsQuantifier;
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
				LogicalConstant definiteDeterminer,
				LogicalConstant existsQuantifier, LogicalConstant aQuantifier,
				LogicalConstant actionExistsQuantifier,
				int maxImplicitActionsPerTurn, Type metaEntityType) {
			this.actionSeqType = actionSeqType;
			this.positionXConstant = positionXConstant;
			this.positionYConstant = positionYConstant;
			this.definiteDeterminer = definiteDeterminer;
			this.existsQuantifier = existsQuantifier;
			this.aQuantifier = aQuantifier;
			this.actionExistsQuantifier = actionExistsQuantifier;
			this.maxImplicitActionsPerTurn = maxImplicitActionsPerTurn;
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
					metaEntityType,
					Collections.unmodifiableMap(equalsPredicates),
					Collections.unmodifiableSet(agentPositionVariantConstants));
		}
	}
	
	public static class StateFlag implements Serializable {
		
		public static final StateFlag				POST	= new StateFlag(
																	"post");
		
		public static final StateFlag				PRE		= new StateFlag(
																	"pre");
		
		private static final Map<String, StateFlag>	STRING_MAPPING;
		
		private static final List<StateFlag>		VALUES;
		
		private final String						label;
		
		private StateFlag(String label) {
			this.label = label;
		}
		
		static {
			final Map<String, StateFlag> stringMapping = new HashMap<String, StateFlag>();
			
			stringMapping.put(POST.label, POST);
			stringMapping.put(PRE.label, PRE);
			
			STRING_MAPPING = Collections.unmodifiableMap(stringMapping);
			VALUES = Collections.unmodifiableList(new ArrayList<StateFlag>(
					stringMapping.values()));
		}
		
		public static StateFlag valueOf(String string) {
			return STRING_MAPPING.get(string);
		}
		
		public static List<StateFlag> values() {
			return VALUES;
		}
		
		@Override
		public boolean equals(Object obj) {
			return obj == this;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((label == null) ? 0 : label.hashCode());
			return result;
		}
		
		@Override
		public String toString() {
			return label;
		}
		
		private Object readResolve() throws ObjectStreamException {
			return Direction.valueOf(toString());
		}
	}
	
}
