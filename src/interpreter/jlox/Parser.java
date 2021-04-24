package interpreter.jlox;

import java.util.List;

public class Parser {
	
	private static class ParseError extends RuntimeException {
		private static final long serialVersionUID = 1L;
	}
	
	private final List<Token> tokens;
	private int current = 0;
	
	public Parser(List<Token> toks) {
		this.tokens = toks;
	}
	
	@SuppressWarnings({ "incomplete-switch", "unused" })
	private void synchronize() {
		advance();
		
		while(!end()) {
			if(previous().GetType() == TokenType.SEMICOLON) {
				return;
			}
			
			switch(peek().GetType()) {
				case CLASS:
				case FUN:
				case VAR:
				case FOR:
				case IF:
				case WHILE:
				case PRINT:
				case RETURN:
					return;
			}
			advance();
		}
	}
	
	public Expression parse()
	{
		try {
			return expression();
		}
		catch(ParseError err) {
			return null;
		}
	}
	
	
	private Expression expression() {
		return equality();
	}
	
	private Expression equality() {
		Expression exp = comparison();
		
		while (match(TokenType.NOTEQUAL, TokenType.EQUAL_EQUAL)) {
			Token op = previous();
			Expression right = comparison();
			exp = new Expression.Binary(exp, op, right);
		}
		
		return exp;
	}
	
	private Expression comparison() {
		Expression exp = term();
		
		while(match(TokenType.GREATER_THAN, TokenType.GREATER_THAN_EQUAL,  TokenType.LESS_THAN, TokenType.LESS_THAN_EQUAL)) {
			Token op = previous();
			Expression right = term();
			exp = new Expression.Binary(exp, op, right);
		}
		return exp;
	}
	
	private Expression term() {
		Expression exp = factor();
		
		while(match(TokenType.MINUS, TokenType.PLUS)) {
			Token op = previous();
			Expression right = factor();
			exp = new Expression.Binary(exp, op, right);
		}
		return exp;
	}
	
	private Expression factor() {
		Expression exp  = unary();
		
		while (match(TokenType.SLASH, TokenType.STAR)) {
			Token op = previous();
			Expression right = unary();
			exp = new Expression.Binary(exp, op, right);
		}
		return exp;
	}
	
	private Expression unary() {
		if(match(TokenType.NOT, TokenType.MINUS)) {
			Token op = previous();
			Expression right = unary();
			return new Expression.Unary(op, right);
		}
		return primary();
	}

	private Expression primary() {
		if(match(TokenType.TRUE)) return new Expression.Literal(true);
		if(match(TokenType.FALSE)) return new Expression.Literal(false);
		if(match(TokenType.NIL)) return new Expression.Literal(null);
		
		if (match(TokenType.NUMBER, TokenType.STRING)) {
			return new Expression.Literal(previous().GetLiteral());
		}
		
		if(match(TokenType.LEFTPAREN)) {
			Expression exp = expression();
			consume(TokenType.RIGHTPAREN, "Expect ')' after expression");
			return new Expression.Grouping(exp);
		}
		
		throw error(peek(), "Expect expression.");
		
	}
	
	private Token consume(TokenType type, String errmsg) {
		if(check(type)) return advance();
		
		throw error(peek(), errmsg);
	}
	
	private ParseError error(Token tok, String errmsg) {
		jlox.error(tok, errmsg);
		return new ParseError();
	}
	
	private boolean match(TokenType... types) {
		for(TokenType t : types) {
			if(check(t)) {
				advance();
				return true;
			}
		}
		return false;
	}
	
	private Token advance() {
		if(!end()) current++;
		return previous();
	}
	
	private boolean check(TokenType type) {
		if(end()) return false;
		return peek().GetType() == type;
	}
	
	private boolean end() {
		return peek().GetType() == TokenType.EOF;
	}
	
	private Token peek() {
		return this.tokens.get(this.current);
	}
	
	private Token previous() {
		return this.tokens.get(this.current - 1);
	}
		
	
}
