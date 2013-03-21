package edu.uw.cs.lil.navi.eval;

import java.util.ArrayList;
import java.util.List;

import edu.uw.cs.lil.tiny.mr.lambda.Lambda;
import edu.uw.cs.lil.tiny.mr.lambda.Literal;
import edu.uw.cs.lil.tiny.mr.lambda.LogicLanguageServices;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalConstant;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.mr.lambda.Variable;
import edu.uw.cs.lil.tiny.mr.lambda.visitor.ILogicalExpressionVisitor;

/**
 * If the logical expression contains a reference to X (contains x:ps), will
 * replace it with a variable which will be bound by an outer Lambda operator.
 * The modified expression will be returned. Else, returns null.
 * 
 * @author Yoav Artzi
 */
public class XPositionWrap implements ILogicalExpressionVisitor {
	
	private final NaviEvaluationConstants	naviConsts;
	private LogicalExpression				result		= null;
	private Variable						xVariable	= null;
	
	private XPositionWrap(NaviEvaluationConstants naviConsts) {
		this.naviConsts = naviConsts;
	}
	
	public static LogicalExpression of(LogicalExpression exp,
			NaviEvaluationConstants naviConsts) {
		final XPositionWrap visitor = new XPositionWrap(naviConsts);
		visitor.visit(exp);
		if (visitor.result == exp) {
			return null;
		} else {
			return new Lambda(visitor.xVariable, visitor.result,
					LogicLanguageServices.getTypeRepository());
		}
	}
	
	@Override
	public void visit(Lambda lambda) {
		lambda.getBody().accept(this);
		if (result == lambda.getBody()) {
			result = lambda;
		} else {
			result = new Lambda(lambda.getArgument(), result,
					LogicLanguageServices.getTypeRepository());
		}
	}
	
	@Override
	public void visit(Literal literal) {
		literal.getPredicate().accept(this);
		final LogicalExpression newPredicate = result;
		final List<LogicalExpression> newArgs = new ArrayList<LogicalExpression>(
				literal.getArguments().size());
		boolean argsChanged = false;
		for (final LogicalExpression arg : literal.getArguments()) {
			arg.accept(this);
			if (result == arg) {
				newArgs.add(arg);
			} else {
				newArgs.add(result);
				argsChanged = true;
			}
		}
		if (argsChanged || newPredicate != literal.getPredicate()) {
			result = new Literal(newPredicate, argsChanged ? newArgs
					: literal.getArguments(),
					LogicLanguageServices.getTypeComparator(),
					LogicLanguageServices.getTypeRepository());
		} else {
			result = literal;
		}
	}
	
	@Override
	public void visit(LogicalConstant logicalConstant) {
		if (logicalConstant.equals(naviConsts.getPositionXConstant())) {
			if (xVariable == null) {
				xVariable = new Variable(LogicLanguageServices
						.getTypeRepository().generalizeType(
								naviConsts.getPositionXConstant().getType()));
			}
			result = xVariable;
		} else {
			result = logicalConstant;
		}
	}
	
	@Override
	public void visit(LogicalExpression logicalExpression) {
		logicalExpression.accept(this);
	}
	
	@Override
	public void visit(Variable variable) {
		result = variable;
	}
	
}
