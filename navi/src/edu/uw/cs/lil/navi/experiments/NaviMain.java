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
package edu.uw.cs.lil.navi.experiments;

import edu.uw.cs.lil.navi.experiments.plat.NaviGenericExperiment;
import edu.uw.cs.utils.log.ILogger;
import edu.uw.cs.utils.log.LoggerFactory;

public class NaviMain {
	public static final ILogger	LOG	= LoggerFactory.create(NaviMain.class);
	
	public static void main(String[] args) {
		if (args.length < 1) {
			LOG.error("Missing arguments. Expects a .exp file as argument.");
			System.exit(-1);
		}
		NaviGenericExperiment.run(args[0]);
	}
	
}
