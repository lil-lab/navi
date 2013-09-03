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
package edu.uw.cs.lil.navi.eval.literalevaluators.actions;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import edu.uw.cs.lil.navi.agent.Action;
import edu.uw.cs.lil.navi.agent.Action.AgentAction;
import edu.uw.cs.lil.navi.eval.literalevaluators.NaviInvariantLiteralEvaluator;

public class ActionType extends NaviInvariantLiteralEvaluator {
	
	private static final long		serialVersionUID	= -5340418737731420887L;
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
