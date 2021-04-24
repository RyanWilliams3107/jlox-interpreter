package interpreter.jlox;

import interpreter.jlox.Expression.Binary;
import interpreter.jlox.Expression.Grouping;
import interpreter.jlox.Expression.Literal;
import interpreter.jlox.Expression.Unary;

public class ASTPrinter implements Expression.Visitor<String> {

	String print(Expression exp) {
		return exp.accept(this);
	}
	
	@Override
	public String visitBinaryExpression(Binary expression) {
		return parenthesize(expression.operator.GetLexeme(), 
				expression.left, expression.right);
	}

	@Override
	public String visitGroupingExpression(Grouping expression) {
		return parenthesize("group", expression.expression);
	}

	@Override
	public String visitLiteralExpression(Literal expression) {
		return expression.value == null ? "nil" : expression.value.toString();
	}

	@Override
	public String visitUnaryExpression(Unary expression) {
		return parenthesize(expression.operator.GetLexeme(), expression.right);
	}
	
	private String parenthesize(String name, Expression... expressions) {
		StringBuilder builder = new StringBuilder();
		
		builder.append("(").append(name);
		for (Expression exp : expressions) {
			builder.append(" ");
			builder.append(exp.accept(this));
		}
		builder.append(")");
		return builder.toString();
	}

}
