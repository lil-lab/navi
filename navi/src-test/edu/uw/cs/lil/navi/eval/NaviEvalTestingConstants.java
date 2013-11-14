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

import edu.uw.cs.lil.navi.TestingConstants;
import edu.uw.cs.lil.navi.experiments.plat.NaviExperiment;

public class NaviEvalTestingConstants {
	private static final NaviEvalTestingConstants	INSTANCE	= new NaviEvalTestingConstants();
	
	private final NaviEvaluationServicesFactory		servicesFactory;
	
	public NaviEvalTestingConstants() {
		TestingConstants.CATEGORY_SERVICES.parseSemantics("left:dir");
		
		servicesFactory = NaviExperiment.createEvaluationProcedure(2)
				.getServicesFactory();
		
	}
	
	public static NaviEvaluationServicesFactory getServicesFactory() {
		return INSTANCE.servicesFactory;
	}
	
}
