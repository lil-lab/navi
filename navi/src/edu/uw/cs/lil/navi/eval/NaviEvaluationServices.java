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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import edu.uw.cs.lil.navi.agent.Action;
import edu.uw.cs.lil.navi.agent.Action.AgentAction;
import edu.uw.cs.lil.navi.agent.Agent;
import edu.uw.cs.lil.navi.agent.Direction;
import edu.uw.cs.lil.navi.data.Step;
import edu.uw.cs.lil.navi.eval.NaviEvaluationConstants.StateFlag;
import edu.uw.cs.lil.navi.eval.literalevaluators.INaviLiteralEvaluator;
import edu.uw.cs.lil.navi.map.Coordinates;
import edu.uw.cs.lil.navi.map.PerceptualCluster;
import edu.uw.cs.lil.navi.map.Position;
import edu.uw.cs.lil.navi.map.PositionSetSingleton;
import edu.uw.cs.lil.navi.map.objects.NaviHall;
import edu.uw.cs.lil.navi.map.objects.NaviWall;
import edu.uw.cs.lil.tiny.mr.lambda.Literal;
import edu.uw.cs.lil.tiny.mr.lambda.LogicLanguageServices;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalConstant;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.mr.lambda.Variable;
import edu.uw.cs.lil.tiny.mr.lambda.exec.naive.AbstractEvaluationServices;
import edu.uw.cs.lil.tiny.mr.language.type.Type;
import edu.uw.cs.utils.collections.CollectionUtils;
import edu.uw.cs.utils.collections.ListUtils;
import edu.uw.cs.utils.collections.SetUtils;
import edu.uw.cs.utils.log.ILogger;
import edu.uw.cs.utils.log.LoggerFactory;

public class NaviEvaluationServices extends AbstractEvaluationServices<State> {
	private static final ILogger			LOG					= LoggerFactory
																		.create(NaviEvaluationServices.class);
	private final Stack<State>				agentCurrentState	= new Stack<State>();
	private final Agent						initAgent;
	private final State						initState;
	private final NaviEvaluationConstants	naviConsts;
	
	/**
	 * Force no implicit actions, despite any prior setting in naviConsts
	 */
	private final boolean					noImplicit;
	
	private final Task						task;
	
	public NaviEvaluationServices(Task task,
			NaviEvaluationConstants naviConsts, boolean noImplicit) {
		this.noImplicit = noImplicit;
		this.initAgent = task.getAgent();
		this.initState = new State(initAgent.getPosition(), false);
		this.naviConsts = naviConsts;
		this.task = task;
	}
	
	/**
	 * The agent doesn't perceive specific positions, but clusters of positions.
	 * The clusters are determined by their content.
	 * 
	 * @param positions
	 * @return
	 */
	public static Set<PerceptualCluster> positionsToPerceptualSets(
			Set<Position> positions) {
		final PerceptualClusterRepo perceptualSets = new PerceptualClusterRepo();
		
		final Set<Position> walls = new HashSet<Position>();
		
		// For each position, create the perceived clusters
		for (final Position position : positions) {
			// Object cluster
			final Set<Position> objectCluster = new HashSet<Position>();
			final Position representingObjectPosition = position
					.getObjectOnlyPosition();
			objectCluster.add(representingObjectPosition);
			objectCluster.add(position.getLeft().getObjectOnlyPosition());
			objectCluster.add(position.getRight().getObjectOnlyPosition());
			objectCluster.add(position.getBack().getObjectOnlyPosition());
			perceptualSets.add(new PerceptualCluster(SetUtils.retainAll(
					objectCluster, positions), Position
					.createPrototype(representingObjectPosition), false));
			
			// Wallpaper cluster(s)
			if (position.getHorizon0().getFrontLeft() == position.getHorizon0()
					.getFrontRight()
					&& position.getHorizon0().getFrontLeft() != NaviWall.END) {
				// Case walls (front-left and front-right) are identical,
				// create a single cluster
				perceptualSets
						.add(new PerceptualCluster(getWallpaperCluster(
								position, new HashSet<Position>(), position
										.getHorizon0().getFrontLeft(),
								positions), Position.createPrototype(position
								.getWallOnlyPosition()), true));
			} else {
				// Case walls differ, create a cluster for each
				
				// Front-left wall cluster
				if (position.getHorizon0().getFrontLeft() != NaviWall.END) {
					perceptualSets.add(new PerceptualCluster(
							getWallpaperCluster(position,
									new HashSet<Position>(), position
											.getHorizon0().getFrontLeft(),
									positions), Position
									.createPrototype(position
											.getLeftWallOnlyPosition()), true));
				}
				
				// Front-right wall cluster
				if (position.getHorizon0().getFrontRight() != NaviWall.END) {
					perceptualSets
							.add(new PerceptualCluster(getWallpaperCluster(
									position, new HashSet<Position>(), position
											.getHorizon0().getFrontRight(),
									positions), Position
									.createPrototype(position
											.getRightWallOnlyPosition()), true));
				}
			}
			
			if (position.getHorizon0().getFront() == NaviHall.WALL) {
				// Case wall, accumulate all such positions
				walls.add(position);
				// Also add a single cluster to present this piece of wall, we
				// need this to compute 'end'
				perceptualSets.add(new PerceptualCluster(CollectionUtils
						.singletonSetOf(position.getUnknownsOnlyPosition()),
						Position.createPrototype(position
								.getUnknownsOnlyPosition()), true));
				
			} else {
				// Hall cluster
				perceptualSets
						.add(new PerceptualCluster(getHallCluster(position,
								new HashSet<Position>(), position.getHorizon0()
										.getFront(), positions),
								Position.createPrototype(position
										.getHallOnlyPosition()), false));
			}
		}
		
		if (!walls.isEmpty()) {
			// Create a cluster for all walls
			perceptualSets.add(new PerceptualCluster(walls, Position
					.createPrototype(walls.iterator().next()
							.getHallOnlyPosition()), true));
		}
		
		return perceptualSets.toSet();
	}
	
	private static List<Action> allPossibleActions(Agent agent) {
		final List<Action> actions = new LinkedList<Action>();
		
		Agent current = null;
		final List<Position> intermediatePositions = new LinkedList<Position>();
		
		// Create all possible turn actions (up to length 3)
		// Left turns
		current = agent;
		while ((current = current.turnLeft()) != null
				&& !(agent.getPosition().equals(current.getPosition()))) {
			actions.add(new Action(AgentAction.LEFT, current.getPosition(),
					new ArrayList<Position>(intermediatePositions), agent
							.getPosition()));
			intermediatePositions.add(current.getPosition());
		}
		
		// Right turns
		current = agent;
		intermediatePositions.clear();
		while ((current = current.turnRight()) != null
				&& !(agent.getPosition().equals(current.getPosition()))) {
			actions.add(new Action(AgentAction.RIGHT, current.getPosition(),
					new ArrayList<Position>(intermediatePositions), agent
							.getPosition()));
			intermediatePositions.add(current.getPosition());
		}
		
		// Create all possible move-forward action, as far as valid
		current = agent;
		intermediatePositions.clear();
		while ((current = current.moveForward()) != null) {
			actions.add(new Action(AgentAction.FORWARD, current.getPosition(),
					new ArrayList<Position>(intermediatePositions), agent
							.getPosition()));
			intermediatePositions.add(current.getPosition());
		}
		
		return actions;
	}
	
	private static Set<Position> getHallCluster(Position position,
			Set<Position> hallCluster, NaviHall hall,
			Set<Position> visiblePositions) {
		if (!visiblePositions.contains(position)) {
			return hallCluster;
		}
		
		if (!hallCluster.contains(position)
				&& position.getHorizon0().getFront() == hall) {
			hallCluster.add(position.getHallOnlyPosition());
			getHallCluster(position.getForward().getBack(), hallCluster, hall,
					visiblePositions);
			getHallCluster(position.getForward(), hallCluster, hall,
					visiblePositions);
			getHallCluster(position.getBack(), hallCluster, hall,
					visiblePositions);
		}
		return hallCluster;
	}
	
	private static Set<Position> getWallpaperCluster(Position position,
			Set<Position> wallCluster, NaviWall wall,
			Set<Position> visiblePositions) {
		if (visiblePositions.contains(position)
				&& !wallCluster.contains(position)) {
			boolean added = false;
			if (position.getHorizon0().getFrontLeft() == wall
					&& position.getHorizon0().getFrontRight() == wall) {
				wallCluster.add(position.getWallOnlyPosition());
				added = true;
			} else if (position.getHorizon0().getFrontLeft() == wall) {
				wallCluster.add(position.getLeftWallOnlyPosition());
				added = true;
			} else if (position.getHorizon0().getFrontRight() == wall) {
				wallCluster.add(position.getRightWallOnlyPosition());
				added = true;
			} else if (position.getHorizon0().getFront() == NaviHall.WALL
					&& ((position.getLeft().getHorizon0().getFrontRight() == wall) || (position
							.getRight().getHorizon0().getFrontLeft() == wall))) {
				wallCluster.add(position.getUnknownsOnlyPosition());
				added = true;
			}
			
			if (added) {
				if (position.getForward() != null) {
					getWallpaperCluster(position.getForward().getBack(),
							wallCluster, wall, visiblePositions);
				}
				getWallpaperCluster(position.getLeft(), wallCluster, wall,
						visiblePositions);
				getWallpaperCluster(position.getRight(), wallCluster, wall,
						visiblePositions);
				getWallpaperCluster(position.getBack(), wallCluster, wall,
						visiblePositions);
			}
		}
		return wallCluster;
	}
	
	private static boolean isActionValid(Action action, Agent initAgent) {
		
		// Verify that the action doesn't contain two consecutive implicit
		// sequences with the same action, or both being 'turn' sequences
		AgentAction prevImplicitAction = null;
		for (final Action implicit : action.getImplicitActions()) {
			if (implicit.getAgentAction().equals(prevImplicitAction)) {
				// Consecutive implicit actions with the same agent action
				// invalidate the action
				return false;
			} else if (prevImplicitAction != null && isTurn(prevImplicitAction)
					&& isTurn(implicit.getAgentAction())) {
				// Two consecutive implicit turn invalidate the action
				return false;
			}
			prevImplicitAction = implicit.getAgentAction();
		}
		
		// Verify that the action doesn't retrace itself and takes the shortest
		// route (minus 'turn's) between the start and end points. Also, verify
		// that there is no implicit FORWARD followed by an explicit one.
		final Set<Position> actionPositions = new HashSet<Position>();
		final List<Step> steps = action.getSteps();
		final Coordinates agentCoordinates = initAgent.getPosition().getPose()
				.getCoordinates();
		final Coordinates endCoordinates = action.getEnd().getPose()
				.getCoordinates();
		final int minDistance = Math.abs(agentCoordinates.getX()
				- endCoordinates.getX())
				+ Math.abs(agentCoordinates.getY() - endCoordinates.getY());
		int accumulatedDistance = 0;
		boolean prevIsImplicitForward = false;
		for (final Step step : steps) {
			if (!actionPositions.add(step.getPosition())) {
				// Case the trace is repeating itself
				return false;
			} else if (step.getAction().equals(AgentAction.FORWARD)) {
				++accumulatedDistance;
				if (prevIsImplicitForward && !step.isImplicit()) {
					// Case implicit FORWARD followed by an explicit FORWARD
					return false;
				} else if (step.isImplicit()) {
					prevIsImplicitForward = true;
				} else {
					prevIsImplicitForward = false;
				}
			} else {
				prevIsImplicitForward = false;
			}
		}
		
		if (accumulatedDistance != minDistance) {
			// Inefficient action
			return false;
		}
		
		return true;
	}
	
	private static boolean isTurn(final AgentAction action) {
		return action.equals(AgentAction.LEFT)
				|| action.equals(AgentAction.RIGHT);
	}
	
	public List<Action> allPossibleActions() {
		final List<Action> actions;
		
		final Agent initialAgent = getCurrentAgent();
		if (naviConsts.getMaxImplicitActionsPerTurn() > 0 && !noImplicit) {
			actions = new ArrayList<Action>();
			for (final Action firstAction : allPossibleActions(initialAgent)) {
				// Action without an implicit prefix
				actions.add(firstAction);
				
				if (canActionBeImplicit(firstAction)) {
					// Case action can be implicit, append it to all possible
					// suffixes
					for (final List<Action> actionList : allPossibleActionsWithImplicit(
							new Agent(firstAction.getEnd()), 1)) {
						final Action explicit = actionList.remove(actionList
								.size() - 1);
						actionList.add(0, firstAction);
						final Action newAction = new Action(explicit,
								actionList);
						
						if (isActionValid(newAction, initialAgent)) {
							actions.add(newAction);
						}
					}
				}
			}
		} else {
			actions = allPossibleActions(initialAgent);
		}
		
		// Create the LEFT turn in place action, to accommodate for weird turns
		// in the data, rising from the random initialization in the original
		// experiments.
		final Position startPosition = initialAgent.getPosition();
		Agent current = initialAgent;
		final List<Position> intermediatePositions = new LinkedList<Position>();
		while ((current = current.turnLeft()) != null
				&& !(startPosition.equals(current.getPosition()))) {
			intermediatePositions.add(current.getPosition());
		}
		actions.add(new Action(AgentAction.LEFT, startPosition,
				new ArrayList<Position>(intermediatePositions), startPosition));
		
		// Sort actions
		Collections.sort(actions);
		
		return actions;
	}
	
	@Override
	public Object evaluateConstant(LogicalConstant logicalConstant) {
		// Case of direction constant
		final Direction direction = logicalExpressionToDirection(logicalConstant);
		if (direction != null) {
			return direction;
		}
		
		// Case constant referring to current position of the agent
		if (logicalConstant.equals(naviConsts.getCurrentPositionConstant())) {
			// Get the current position of the agent
			return getCurrentAgentPositionSet();
		}
		
		// Case constant referring to the goal position (position x)
		if (logicalConstant.equals(naviConsts.getPositionXConstant())) {
			return task.getPositionX();
		}
		
		// Case constant referring to the start position (position y)
		if (logicalConstant.equals(naviConsts.getPositionYConstant())) {
			// Get the task starting position
			return task.getPositionY();
		}
		
		// Unknown constant, just return null
		return super.evaluateConstant(logicalConstant);
	}
	
	@Override
	public Object evaluateLiteral(LogicalExpression predicate, Object[] args) {
		final INaviLiteralEvaluator evaluator = naviConsts
				.getLiteralEvaluator(predicate);
		if (evaluator == null) {
			// Unknown predicate, just return null
			return null;
		} else {
			return evaluator.evaluate(this, args);
		}
	}
	
	@Override
	public List<?> getAllDenotations(Variable variable) {
		final Type type = variable.getType();
		if (type.equals(naviConsts.getActionSeqType())) {
			return allPossibleActions();
		} else if (type.equals(LogicLanguageServices.getTypeRepository()
				.getTruthValueType())) {
			final List<Object> truthValues = new ArrayList<Object>(2);
			truthValues.add(Boolean.TRUE);
			truthValues.add(Boolean.FALSE);
			return truthValues;
		} else if (type.equals(LogicLanguageServices.getTypeRepository()
				.getEntityType())) {
			// Return a list of all possible entity denotations, basically all
			// observed position sets
			return observedPerceptualClusters();
		} else if (type.equals(naviConsts.getMetaEntityType())) {
			final List<Object> entityDenotations = new ArrayList<Object>();
			// Return a list of all possible meta-entity denotations, these
			// include a few sub-types
			
			// All directions
			entityDenotations.addAll(naviConsts.getAllDirections());
			
			// All supported numbers
			entityDenotations.addAll(naviConsts.getSupportedNumbers());
			
			return entityDenotations;
		} else {
			throw new IllegalStateException("Unhandled type: " + variable);
		}
	}
	
	public LogicalExpression getAQuantifier() {
		return naviConsts.getAQuantifier();
	}
	
	public Position getCurrentAgentPosition() {
		if (agentCurrentState.isEmpty()) {
			return initAgent.getPosition();
		} else {
			return agentCurrentState.peek().getAgentPosition();
		}
	}
	
	public PositionSetSingleton getCurrentAgentPositionSet() {
		return PositionSetSingleton.of(getCurrentAgentPosition());
	}
	
	public Map<Type, LogicalConstant> getEqualsPredicates() {
		return naviConsts.getEqualsPredicates();
	}
	
	public LogicalExpression getExistsQuantifier() {
		return naviConsts.getEntityExistsQuantifier();
	}
	
	public PositionSetSingleton getInitAgentPosition() {
		return initAgent.getPositionAsSet();
	}
	
	public StateFlag getStateFlag(Literal literal) {
		return naviConsts.getStateFlag(literal.getPredicate());
	}
	
	public LogicalConstant getStatefulWrapperPredicate(
			LogicalExpression predicate) {
		return naviConsts.getStatefulWrapper(predicate);
	}
	
	public boolean isActionSeq(Type type) {
		return naviConsts.getActionSeqType().equals(type);
	}
	
	public boolean isAgentInMovement() {
		if (agentCurrentState.isEmpty()) {
			return false;
		} else {
			return agentCurrentState.peek().isInMovement();
		}
	}
	
	public boolean isDefiniteQuantifier(Literal literal) {
		return naviConsts.isDefiniteQuantifier(literal.getPredicate());
	}
	
	@Override
	public boolean isDenotable(Variable variable) {
		final Type type = variable.getType();
		if (type.equals(naviConsts.getActionSeqType())
				|| type.equals(LogicLanguageServices.getTypeRepository()
						.getTruthValueType())
				|| type.equals(LogicLanguageServices.getTypeRepository()
						.getEntityType())
				|| type.equals(naviConsts.getMetaEntityType())) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isExistsQuantifier(Literal literal) {
		return naviConsts.isExistsQuantifier(literal.getPredicate());
	}
	
	@Override
	public boolean isInterpretable(LogicalConstant constant) {
		return super.isInterpretable(constant)
				|| evaluateConstant(constant) != null
				|| naviConsts.getLiteralEvaluator(constant) != null
				|| naviConsts.getAQuantifier().equals(constant);
	}
	
	public boolean isLiteralStateful(Literal literal) {
		return naviConsts.isPredicateStateful(literal.getPredicate())
				&& literal.getArguments().size() >= 1
				&& literal.getArguments().get(0).getType()
						.equals(naviConsts.getActionSeqType());
	}
	
	public boolean isStatefulWrapper(Literal literal) {
		return naviConsts.getStateFlag(literal.getPredicate()) != null;
	}
	
	public void popAgentState() {
		final State peek = agentCurrentState.peek();
		if (!peek.getAgentPosition().equals(initState.getAgentPosition())) {
			// Case the popped position is different from the initial position,
			// clean the cache from any logical expression that depends on this
			// state (position)
			clearStateFromCache(peek);
		}
		
		LOG.debug("Popping agent position: %s", peek);
		agentCurrentState.pop();
	}
	
	public void pushAgentPosition(Position position) {
		LOG.debug("Pushing position: %s", position);
		agentCurrentState.push(new State(position, isAgentInMovement()
				|| !position
						.getPose()
						.getCoordinates()
						.equals(initState.getAgentPosition().getPose()
								.getCoordinates())));
	}
	
	public void pushAgentPosition(PositionSetSingleton position) {
		pushAgentPosition(position.get());
	}
	
	private List<List<Action>> allPossibleActionsWithImplicit(Agent agent,
			int actionsSoFar) {
		if (actionsSoFar > naviConsts.getMaxImplicitActionsPerTurn()) {
			return Collections.emptyList();
		}
		
		final List<Action> actions = allPossibleActions(agent);
		
		final List<List<Action>> actionLists = new LinkedList<List<Action>>();
		for (final Action action : actions) {
			actionLists.add(ListUtils.createSingletonList(action));
			
			if (canActionBeImplicit(action)) {
				for (final List<Action> actionList : allPossibleActionsWithImplicit(
						new Agent(action.getEnd()), actionsSoFar + 1)) {
					actionList.add(0, action);
					actionLists.add(actionList);
				}
			}
		}
		
		return actionLists;
	}
	
	private boolean canActionBeImplicit(Action action) {
		if (action.getAgentAction().equals(AgentAction.FORWARD)) {
			return true;
		} else if (action.getAgentAction().equals(AgentAction.LEFT)) {
			return action.length() <= 2;
		} else if (action.getAgentAction().equals(AgentAction.RIGHT)) {
			return action.length() == 1;
		} else {
			return false;
		}
	}
	
	private Set<Position> getAllObservedPositions() {
		final Set<Position> observedPositions = new HashSet<Position>();
		observedPositions.addAll(initAgent.getObservedPositions());
		for (final State state : agentCurrentState) {
			observedPositions.addAll(new Agent(state.getAgentPosition())
					.getObservedPositions());
		}
		return observedPositions;
	}
	
	private Agent getCurrentAgent() {
		if (agentCurrentState.isEmpty()) {
			return initAgent;
		} else {
			return new Agent(agentCurrentState.peek().getAgentPosition());
		}
	}
	
	private Direction logicalExpressionToDirection(
			LogicalExpression logicalConstant) {
		return naviConsts.getDirection(logicalConstant);
	}
	
	private List<PerceptualCluster> observedPerceptualClusters() {
		final Set<Position> observedPositions = getAllObservedPositions();
		final Set<PerceptualCluster> clusters = positionsToPerceptualSets(observedPositions);
		return new ArrayList<PerceptualCluster>(clusters);
	}
	
	@Override
	protected State currentState() {
		if (agentCurrentState.isEmpty()) {
			return initState;
		} else {
			return agentCurrentState.peek();
		}
	}
	
}
