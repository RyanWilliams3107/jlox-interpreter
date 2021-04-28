package interpreter.jlox;

import interpreter.jlox.Expression.Binary;
import interpreter.jlox.Expression.Grouping;
import interpreter.jlox.Expression.Literal;
import interpreter.jlox.Expression.Unary;

public class Interpreter implements Expression.Visitor<Object> {

	
	void interpret(Expression exp) {
		try
		{
			Object value = evaluate(exp);
			System.out.println(stringify(value));
		}
		catch(RuntimeError e) {
			jlox.runtimeError(e);
		}
	}
	
	private String stringify(Object value) {
		if (value == null) return "nil";

	    if (value instanceof Double) {
	      String text = value.toString();
	      if (text.endsWith(".0")) {
	    	  text = text.substring(0, text.length() - 2);
	      }
	      return text;
		}
	    return value.toString();
	}

	@Override
	public Object visitBinaryExpression(Binary expression) {
		Object lhs = evaluate(expression.left);
		Object rhs = evaluate(expression.right);
		
		switch(expression.operator.GetType()){
		case GREATER_THAN:
			checkNumberOperands(expression.operator, lhs, rhs);
			return (double) lhs > (double) rhs;
		case GREATER_THAN_EQUAL:
			checkNumberOperands(expression.operator, lhs, rhs);
			return (double) lhs >= (double) rhs;
		case LESS_THAN:
			checkNumberOperands(expression.operator, lhs, rhs);
			return (double) lhs < (double) rhs;
		case LESS_THAN_EQUAL:
			checkNumberOperands(expression.operator, lhs, rhs);
			return (double) lhs <= (double) rhs;
		
		case MINUS:
			checkNumberOperands(expression.operator, lhs, rhs);
			return (double) lhs - (double) rhs;
		case PLUS:
			if (lhs instanceof Double && rhs instanceof Double) {
				return (double) lhs + (double) rhs;
			}
			if(lhs instanceof String && rhs instanceof String) {
				return (String) lhs + (String) rhs;
			}
			
			throw new RuntimeError(expression.operator, "Operands must be two numbers or two strings");
			
		case SLASH:
			checkNumberOperands(expression.operator, lhs, rhs);
			if ((double)rhs == 0) {
				throw new RuntimeError(expression.operator, "Division by 0 error");
			}
			return (double) lhs / (double) rhs;
		case MOD:
			checkNumberOperands(expression.operator, lhs, rhs);
			if ((double)rhs == 0) {
				throw new RuntimeError(expression.operator, "Mod by 0 error");
			}
			return (double) lhs % (double) rhs;
		case STAR:
			return (double) lhs * (double) rhs;
		case NOT_EQUAL:
			return !equal(lhs, rhs);
		case EQUAL_EQUAL:
			return equal(lhs, rhs);
		default:
			break;
		
		}
		
		return null;
	}

	private void checkNumberOperands(Token operator, Object lhs, Object rhs) {
		if (lhs instanceof Double && rhs instanceof Double) {
			return;
		}
		throw new RuntimeError(operator, "Operands must be numbers");
		
	}

	@Override
	public Object visitGroupingExpression(Grouping expression) {
		return evaluate(expression.expression);
	}

	@Override
	public Object visitLiteralExpression(Literal expression) {
		return expression.value;
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	public Object visitUnaryExpression(Unary expression) {
		Object right = evaluate(expression.right);
		switch(expression.operator.GetType()) {
		case NOT:
			return !truthy(right);
		case MINUS:
			checkNumberOperand(expression.operator, right);
			return -(double)right;
		}
		return null;
	}
	
	private Object evaluate(Expression exp){
		return exp.accept(this);
	}
	
	private void checkNumberOperand(Token operator, Object operand) {
		if(operand instanceof Double) return;
		throw new RuntimeError(operator, "Operand must be a number");
	}
	
	private boolean truthy(Object o) {
		if(o == null) return false;
		if (o instanceof Boolean) return (boolean) o;
		return true;
	}
	
	private boolean equal(Object a, Object b) {
		if(a == null && b == null) return true;
		if(a == null) return false;
		return a.equals(b);		
	}
}
