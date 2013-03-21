package edu.uw.cs.lil.navi.eval.literalevaluators.actions;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import edu.uw.cs.lil.navi.agent.Action;
import edu.uw.cs.lil.navi.agent.Action.AgentAction;
import edu.uw.cs.lil.navi.eval.literalevaluators.NaviInvariantLiteralEvaluator;

public class ActionType extends NaviInvariantLiteralEvaluator {
	
	private final Set<AgentAction>	actions;
	
	public ActionType(AgentAction... actions) {
		this.actions = new HashSet<AgentAction>(Arrays.asList(actions));
	}
	
	@Override
	public Object evaluate(Object[] args) {
		if (args.length == 1 && args[0] instanceof Action) {
			return actions.contains(((Action) args[0]).getAgentAction());
		} else {
			return null;
		}
	}
}
