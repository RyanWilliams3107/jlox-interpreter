package interpreter.jlox;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class jlox {

    static boolean hadError = false;
    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("Usage: jlox [script]");
            System.exit(64);
        }
        else if (args.length == 1)
        {
            runFile(args[0]);
        }
        else {
            runPrompt();
        }
    }

    private static void runFile(String fpath) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(fpath));
        run(new String(bytes, Charset.defaultCharset()));
        if (hadError) System.exit(65);
    }

    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        while (true){
            System.out.println("jlox > ");
            String line = reader.readLine();
            if(line ==  null) break;
            run(line);
            hadError = false;
        }
    }

    private static void run(String src){
        Scanner sc = new Scanner(src);
        List<Token> tokens = sc.scanTokens();

        Parser parser =  new Parser(tokens);
        Expression expression = parser.parse();
        if(hadError) return;
        System.out.println(new ASTPrinter().print(expression));
    }

    public static void error(int ln, String msg){
        report(ln, "", msg);
    }
    
    public static void error(Token tok, String msg) {
    	if(tok.GetType() == TokenType.EOF) {
    		report(tok.GetLine(), " at end", msg);
    	}
    	else {
    		report(tok.GetLine(), " at '" + tok.GetLexeme() + "'", msg);
    	}
    }

    private static void report(int ln, String loc, String msg){
        System.out.println(
            "[line " + ln + "] Error" + loc + ": " + msg
        );
        hadError  = true;
    }
    
}
