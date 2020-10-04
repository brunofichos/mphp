import lexical.Lexeme;
import lexical.LexicalAnalysis;
import lexical.TokenType;
import syntatic.SyntaticAnalysis;

public class mphp {
	public static void main(String args[]) {
		if (args.length != 1) {
			System.out.println("Usage: java mphp [source file]");
			return;
		}

		try (LexicalAnalysis l = new LexicalAnalysis(args[0])) {
			SyntaticAnalysis s = new SyntaticAnalysis(l);
			s.start();
		} catch (Exception e) {
			System.err.println("Internal error: " + e.getMessage());
		}
	}

}
