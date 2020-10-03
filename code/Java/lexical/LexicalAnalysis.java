package lexical;

import java.io.FileInputStream;
import java.io.PushbackInputStream;

public class LexicalAnalysis implements AutoCloseable {

	private int line;
	private SymbolTable st;
	private PushbackInputStream input;

	public LexicalAnalysis(String filename) {
		try {
			input = new PushbackInputStream(new FileInputStream(filename));
		} catch (Exception e) {
			throw new LexicalException("Unable to open file: " + filename);
		}

		st = new SymbolTable();
		line = 1;
	}

	public void close() {
		try {
			input.close();
		} catch (Exception e) {
			throw new LexicalException("Unable to close file");
		}
	}

	public int getLine() {
		return this.line;
	}

	public Lexeme nextToken() {
		Lexeme lex = new Lexeme("", TokenType.END_OF_FILE);


		int state = 1;
		while (state != 15 && state != 16) {
			int c = getc();
			switch (state) {
				case 1:
					if (c == ' ' || c == '\r' || c == '\t') {
						state = 1;
					} else if (c == '\n') {
						line++;
						state = 1;
					} else if (c == '#') {
						state = 2;
					} else if (c == '=' || c == '<' || c == '>') {
						lex.token += (char) c;
						state = 3;
					} else if (c == '!') {
						lex.token += (char) c;
						state = 4;
					} else if (c == ';' || c == '+' || c == '-' ||
							   c == '*' || c == '/' || c == '%') {
						lex.token += (char) c;
						state = 7;
					} else if (Character.isLetter(c) || c == '_') {
						lex.token += (char) c;
						state = 5;
					} else if (Character.isDigit(c)) {
						lex.token += (char) c;
						state = 6;
					} else if (c == -1) {
						lex.type = TokenType.END_OF_FILE;
						state = 8;
					} else {
						lex.token += (char) c;
						lex.type = TokenType.INVALID_TOKEN;
						state = 8;
					}

					break;
				case 2:
					if (c == '\n') {
						line++;
						state = 1;
					} else if (c == -1) {
						lex.type = TokenType.END_OF_FILE;
						state = 8;
					} else {
						state = 2;
					}

					break;
				case 3:
					if (c == '=') {
						lex.token += (char) c;
						state = 7;
					} else {
						ungetc(c);
						state = 7;
					}

					break;
				case 4:
					if (c == '=') {
						lex.token += (char) c;
						state = 7;
					} else if (c == -1) {
						lex.type = TokenType.UNEXPECTED_EOF;
						state = 8;
					} else {
						lex.type = TokenType.INVALID_TOKEN;
						state = 8;
					}

					break;
				case 5: //ok
					if (c == '=' || c == '+') {
						lex.token += (char) c;
						state = 15;
					}
					else{
						ungetc(c);
						state = 15;
					}
					break;
				case 6: //ok
					if (c == '=' || c == '-') {
						lex.token += (char) c;
						state = 15;
					}
					else{
						ungetc(c);
						state = 15;
					}
					break;
				case 7: //ok
					if (c == '=') {
						lex.token += (char) c;
						state = 15;
					}
					else{
						ungetc(c);
						state = 15;
					}
					break;
				case 8: //ok
					if (c == '>' || c == '=') {
						lex.token += (char) c;
						state = 15;
					}else{
						ungetc(c);
						state = 15;
					}
					break;
				case 9:  //ok
					if (Character.isLetter(c)) {
						lex.token += (char) c;
						state = 9;
					} else {
						ungetc(c);
						state = 15;
					}
					break;
				case 10: //ok
					if (Character.isLetter(c) ||
						c == '_') {
						lex.token += (char) c;
						state = 11;
					} else {
						ungetc(c);
						state = 15;
					}
					break;
				case 11: //ok
					if (Character.isLetter(c) ||
						Character.isDigit(c) ||
						c == '_') {
						lex.token += (char) c;
						state = 11;
					} else {
						ungetc(c);
						state = 16;
					}
					break;
				case 12: //ok
					if (Character.isDigit(c)) {
						lex.token += (char) c;
						state = 12;
					} else {
						ungetc(c);
						lex.type = TokenType.NUMBER;
						state = 16;
					}
					break;
				case 13: //ok
					if (c == '"') {
						lex.token += (char) c;
						state = 16;
					}else if  (c == '\\'){
						state = 14;
					}else{
						state = 13;
					}
					break;
				case 14:
					if (c == '=') {
						lex.token += (char) c;
						state = 7;
					}
					break;

				case 15:
					if (c == '=') {
						lex.token += (char) c;
						state = 7;
					}
					break;
				case 16:
					if (c == '=') {
						lex.token += (char) c;
						state = 7;
					}
					break;
				default:
					throw new LexicalException("Unreachable");
			}
		}

		if (state == 7)
			lex.type = st.find(lex.token);



		return lex;
	}

	private int getc() {
		try {
			return input.read();
		} catch (Exception e) {
			throw new LexicalException("Unable to read file");
		}
	}

	private void ungetc(int c) {
		if (c != -1) {
			try {
				input.unread(c);
			} catch (Exception e) {
				throw new LexicalException("Unable to ungetc");
			}
		}
	}
}
