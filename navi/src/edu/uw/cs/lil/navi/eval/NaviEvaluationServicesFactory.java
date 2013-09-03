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

import java.io.Serializable;

/**
 * Factory for {@link NaviEvaluationServices}.
 * 
 * @author Yoav Artzi
 */
public class NaviEvaluationServicesFactory implements Serializable {
	private final NaviEvaluationConstants	naviConsts;
	
	public NaviEvaluationServicesFactory(NaviEvaluationConstants naviConsts) {
		this.naviConsts = naviConsts;
	}
	
	public NaviEvaluationServices create(Task task) {
		return create(task, false);
	}
	
	public NaviEvaluationServices create(Task task, boolean noImplicit) {
		return new NaviEvaluationServices(task, naviConsts, noImplicit);
	}
	
	public NaviEvaluationConstants getNaviEvaluationConsts() {
		return naviConsts;
	}
}
