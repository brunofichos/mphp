package lexical;

import java.util.Map;
import java.util.HashMap;

public class SymbolTable {

	private Map<String, TokenType> st;

	public SymbolTable() {
		st = new HashMap<String, TokenType>();

		// Symbols
		st.put(";", TokenType.SEMICOLON);
		st.put(".", TokenType.CONCATENATE);

		//Assigment operators
		st.put("=", TokenType.ASSIGN);
		st.put("+=", TokenType.INCR_ASSIGN);
		st.put("-=", TokenType.DECR_ASSIGN);
		st.put("*=", TokenType.MUX_ASSIGN);
		st.put("/=", TokenType.DIV_ASSIGN);
		st.put("%=", TokenType.MOD_ASSIGN);
		st.put("++", TokenType.INCREMENT);
		st.put("--", TokenType.DECREMENT);

		// Logic operators
		st.put("==", TokenType.EQUAL);
		st.put("!=", TokenType.NOT_EQUAL);
		st.put("<", TokenType.LOWER);
		st.put("<=", TokenType.LOWER_EQUAL);
		st.put(">", TokenType.GREATER);
		st.put(">=", TokenType.GREATER_EQUAL);
		st.put("and", TokenType.AND);
		st.put("or", TokenType.OR);

		// Arithmetic operators
		st.put("+", TokenType.ADD);
		st.put("-", TokenType.SUB);
		st.put("*", TokenType.MUL);
		st.put("/", TokenType.DIV);
		st.put("%", TokenType.MOD);

		// Keywords
		st.put("while", TokenType.WHILE);
		st.put("if", TokenType.IF);
		st.put("true", TokenType.TRUE);
		st.put("foreach", TokenType.FOREACH);
		st.put("echo", TokenType.ECHO);

		// Others
		st.put("read", TokenType.READ);

	}

	public boolean contains(String token) {
		return st.containsKey(token);
	}

	public TokenType find(String token) {
		return this.contains(token) ?
					st.get(token) : TokenType.VAR;
	}
}
