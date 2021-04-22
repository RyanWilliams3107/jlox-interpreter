package interpreter.jlox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;




public class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    private static final Map<String, TokenType> keywords;
    static{
        keywords = new HashMap<>();
        keywords.put("and",    TokenType.AND);
        keywords.put("else",   TokenType.ELSE);
        keywords.put("false",  TokenType.FALSE);
        keywords.put("class",  TokenType.CLASS);
        keywords.put("for",    TokenType.FOR);
        keywords.put("fun",    TokenType.FUN);
        keywords.put("if",     TokenType.IF);
        keywords.put("nil",    TokenType.NIL);
        keywords.put("or",     TokenType.OR);
        keywords.put("print",  TokenType.PRINT);
        keywords.put("return", TokenType.RETURN);
        keywords.put("super",  TokenType.SUPER);
        keywords.put("this",   TokenType.THIS);
        keywords.put("true",   TokenType.TRUE);
        keywords.put("var",    TokenType.VAR);
        keywords.put("while",  TokenType.WHILE);
    }

    public Scanner(String src) {
        this.source = src;
    }

    public List<Token> scanTokens() {
        
        while(!end())
        {
            start = current;
            scanToken();
        }
        tokens.add(new Token(TokenType.EOF, "", null, line));
        return tokens;
    }

    private char advance() {
        return this.source.charAt(this.current++);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
      }
    
    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
      }

    private void scanToken(){
        char cur = advance();
        switch (cur){
            case '(': addToken(TokenType.LEFTPAREN); break;
            case ')': addToken(TokenType.RIGHTPAREN); break;
            case '{': addToken(TokenType.LEFTBRACE); break;
            case '}': addToken(TokenType.RIGHTBRACE); break;
            case ',': addToken(TokenType.COMMA); break;
            case '.': addToken(TokenType.DOT); break;
            case '-': addToken(TokenType.MINUS); break;
            case '+': addToken(TokenType.PLUS); break;
            case '*': addToken(TokenType.STAR); break;
            case ';': addToken(TokenType.SEMICOLON); break;
            case '!':
                addToken(match('=') ? TokenType.NOTEQUAL : TokenType.NOT);
                break;
            case '=':
                addToken(match('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL);
                break;
            case '<':
                addToken(match('=') ? TokenType.LESS_THAN_EQUAL : TokenType.LESS_THAN);
                break;
            case '>':
                addToken(match('=') ? TokenType.GREATER_THAN_EQUAL : TokenType.GREATER_THAN);
                break;
            case '/':
                if(match('/')){
                    while (peek() != '\n' && !end()) advance();
                }
                else addToken(TokenType.SLASH);
                break;

            case ' ':
            case '\r':
            case '\t':
                // Ignore whitespace.
                break;
            case '\n':
                this.line++;
                break;
            case '"': string(); break;
            default:
                if(isDigit(cur)) number();
                else if (isAlpha(cur)) identifier();
                else jlox.error(line, "Unexpected character: " + cur + "."); 
                break;
        }
    }

    private boolean isDigit(char c){
        return c >= '0' && c <= '9';
    }

    private boolean isAlpha(char c){
        return (c >= 'a' && c <= 'z') ||
               (c >= 'A' && c <= 'Z') ||
                c == '_';
    }

    private boolean isAlphaNumeric(char c){
        return isDigit(c) || isAlpha(c);
    }

    private void number() {
        while(isDigit(peek())){
            advance();
        }

        if (peek() == '.' && isDigit(peekNext())){
            advance();
            while(isDigit(peek())){
                advance();
            }
        }

        addToken(TokenType.NUMBER,
         Double.parseDouble(this.source.substring(this.start, this.current)));
    }

    private void string() {
        while(peek() != '"' && !end()){
            if(peek() == '\n') this.line++;
            advance();
        }
        if(end()){
            jlox.error(this.line, "Unterminated string.");
            return;
        }
        advance();
        String value = this.source.substring(this.start + 1, this.current - 1);
        addToken(TokenType.STRING, value);
    }

    private void identifier(){
        while (isAlphaNumeric(peek())) advance();

        String text = this.source.substring(this.start, this.current);
        TokenType type = keywords.get(text);
        if(type == null) type = TokenType.IDENTIFIER;
        addToken(type);
    }

    private char peek()
    {
        if (end()) return '\0';
        return this.source.charAt(this.current);
    }

    private char peekNext()
    {
        return this.current + 1 >= this.source.length() ? '\0' : this.source.charAt(this.current + 1);
    }

    private boolean match(char expected) {
        if(end()) return false;
        if (this.source.charAt(this.current) != expected) return false;

        this.current++;
        return true;

    }

    private boolean end() {
        return this.current >= this.source.length();
    }
}
