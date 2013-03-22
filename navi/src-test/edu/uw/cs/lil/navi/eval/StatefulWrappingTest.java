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

import java.util.HashMap;

import junit.framework.Assert;

import org.junit.Test;

import edu.uw.cs.lil.navi.TestingConstants;
import edu.uw.cs.lil.navi.agent.Agent;
import edu.uw.cs.lil.navi.map.PositionSet;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.mr.lambda.visitor.LambdaWrapped;

public class StatefulWrappingTest {
	
	@Test
	public void test() {
		final Task task = new Task(new Agent(TestingConstants.MAPS.get("grid")
				.get(3)), new PositionSet(TestingConstants.MAPS.get("grid")
				.get(3).getAllOrientations(), false), new PositionSet(
				TestingConstants.MAPS.get("grid").get(5).getAllOrientations(),
				false), new HashMap<String, String>(),
				TestingConstants.MAPS.get("grid"));
		
		final LogicalExpression exp = LambdaWrapped
				.of(TestingConstants.CATEGORY_SERVICES
						.parseSemantics("(lambda $0:a (and:<t*,t> (move:<a,t> $0)  (pre:<a,<ps,t>> $0 (io:<<e,t>,e> wood:<ps,t>)) (dir:<a,<dir,t>> $0 forward:dir) (to:<a,<ps,t>> $0 (a:<<e,t>,e> wall:<ps,t>))))"));
		
		final LogicalExpression result = LambdaWrapped
				.of(TestingConstants.CATEGORY_SERVICES
						.parseSemantics("(lambda $0:a (and:<t*,t> (move:<a,t> $0) (PRE_STATE_WRAPPER:<a,<t,t>> $0 (pre:<a,<ps,t>> $0 (io:<<e,t>,e> wood:<ps,t>))) (dir:<a,<dir,t>> $0 forward:dir) (POST_STATE_WRAPPER:<a,<t,t>> $0 (to:<a,<ps,t>> $0 (a:<<e,t>,e> wall:<ps,t>)))))"));
		
		Assert.assertEquals(result, StatefulWrapping.of(exp,
				NaviEvalTestingConstants.getServicesFactory().create(task)));
		
	}
	
}
