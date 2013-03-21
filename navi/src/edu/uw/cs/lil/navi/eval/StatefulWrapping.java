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

public class StatefulWrapping implements ILogicalExpressionVisitor {
	private LogicalExpression				result	= null;
	private final NaviEvaluationServices	services;
	
	private StatefulWrapping(NaviEvaluationServices services) {
		this.services = services;
	}
	
	public static LogicalExpression of(LogicalExpression exp,
			NaviEvaluationServices services) {
		final StatefulWrapping visitor = new StatefulWrapping(services);
		visitor.visit(exp);
		return visitor.result;
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
		final LogicalExpression newPred = result;
		boolean argsChanged = false;
		final List<LogicalExpression> newArgs = new ArrayList<LogicalExpression>(
				literal.getArguments().size());
		for (final LogicalExpression arg : literal.getArguments()) {
			arg.accept(this);
			newArgs.add(result);
			if (arg != result) {
				argsChanged = true;
			}
		}
		
		if (newPred != literal.getPredicate() || argsChanged) {
			result = new Literal(newPred, argsChanged ? newArgs
					: literal.getArguments(),
					LogicLanguageServices.getTypeComparator(),
					LogicLanguageServices.getTypeRepository());
		} else {
			result = literal;
		}
		
		if (services.isLiteralStateful((Literal) result)) {
			final List<LogicalExpression> args = new ArrayList<LogicalExpression>(
					2);
			args.add(((Literal) result).getArguments().get(0));
			args.add(result);
			result = new Literal(services.getStatefulWrapperPredicate(newPred),
					args, LogicLanguageServices.getTypeComparator(),
					LogicLanguageServices.getTypeRepository());
		}
	}
	
	@Override
	public void visit(LogicalConstant logicalConstant) {
		result = logicalConstant;
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
