package edu.uw.cs.lil.navi.eval;

/**
 * Factory for {@link NaviEvaluationServices}.
 * 
 * @author Yoav Artzi
 */
public class NaviEvaluationServicesFactory {
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
