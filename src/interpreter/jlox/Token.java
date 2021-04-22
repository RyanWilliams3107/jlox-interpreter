package interpreter.jlox;

public class Token {
    private TokenType type;
    private String lexeme;
    private Object literal;
    private int line;

    public Token(TokenType type, String lexeme, Object literal, int line) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
    }

    public void SetType(TokenType newType){
        this.type = newType;
    }

    public void SetLexeme(String newLexeme){
        this.lexeme  = newLexeme;
    }
    public void SetLiteral(Object newLiteral){
        this.literal = newLiteral;
    }
    public void SetLine(int newLine){
        this.line = newLine;
    }

    public TokenType GetType(){
        return this.type;
    }

    public String GetLexeme(){
        return this.lexeme;
    }
    public Object GetLiteral(){
        return this.literal;
    }
    public int GetLine(){
        return this.line;
    }

    public String toString(){
        return "Token(" + type + ", " + lexeme + ", " + literal + ", " + line +  ")";
    }

}
