package syntatic;

import lexical.Lexeme;
import lexical.LexicalAnalysis;
import lexical.TokenType;

public class SyntaticAnalysis {

    private LexicalAnalysis lex;
    private Lexeme current;

    public SyntaticAnalysis(LexicalAnalysis lex) {
        this.lex = lex;
        this.current = lex.nextToken();
    }

	public void start() {
        procCode();
        eat(TokenType.END_OF_FILE);
    }
    private void advance() {
        current = lex.nextToken();
    }

    private void eat(TokenType type) {
         System.out.println("Expected (..., " + type + "), found (\"" +
             current.token + "\", " + current.type + ")");
        if (type == current.type) {
            advance();
        } else {
            showError();
        }
    }

    private void showError() {
        System.out.printf("%02d: ", lex.getLine());

        switch (current.type) {
            case INVALID_TOKEN:
                System.out.printf("Lexema inválido [%s]\n", current.token);
                break;
            case UNEXPECTED_EOF:
            case END_OF_FILE:
                System.out.printf("Fim de arquivo inesperado\n");
                break;
            default:
                System.out.printf("Lexema não esperado [%s]\n", current.token);
                break;
        }

        System.exit(1);
	}

	//<code> ::= { <statement> }
	private void procCode(){

		while (current.type == TokenType.VAR ||
                current.type == TokenType.ECHO ||
                current.type == TokenType.IF ||
                current.type == TokenType.FOREACH ||
				current.type == TokenType.WHILE ||
                current.type == TokenType.VARVAR
				) {
            procStatement();
		}
	}

	//<statement> ::= <if> | <while> | <foreach> | <echo> | <assign>
	private void procStatement(){

		if (current.type == TokenType.VARVAR)
			procVarVar();
		else if (current.type == TokenType.VAR)
			procAssign();
        else if (current.type == TokenType.ECHO)
            procEcho();
        else if (current.type == TokenType.IF)
            procIf();
		else if (current.type == TokenType.WHILE)
			procWhile();
		else if (current.type == TokenType.FOREACH)
			procForEach();
        else
            showError();

	}

	//<if> ::= if '(' <boolexpr> ')' '{' <code> '}'
	        //{ elseif '(' <boolexpr> ')' '{' <code> '}' } [ else '{' <code> '}' ]
	private void procIf() {
        eat(TokenType.IF);
        eat(TokenType.PARAN_OPEN);
		procBoolExpr();
		eat(TokenType.PARAN_CLOSE);
		eat(TokenType.BRACK_OPEN);
        procCode();
		eat(TokenType.BRACK_CLOSE);
		while (current.type == TokenType.ELSEIF){
			eat(TokenType.PARAN_OPEN);
			procBoolExpr();
			eat(TokenType.PARAN_CLOSE);
			eat(TokenType.BRACK_OPEN);
			procCode();
			eat(TokenType.BRACK_CLOSE);
		}
		if (current.type == TokenType.ELSE) {
			advance();
			eat(TokenType.BRACK_OPEN);
			procCode();
			eat(TokenType.BRACK_CLOSE);
		}

	}

	//<while> ::= while '(' <boolexpr> ')' '{' <code> '}'
	private void procWhile() {
		eat(TokenType.WHILE);
		eat(TokenType.PARAN_OPEN);
		procBoolExpr();
		eat(TokenType.PARAN_CLOSE);
		eat(TokenType.BRACK_OPEN);
		procCode();
		eat(TokenType.PARAN_CLOSE);
	}

	//<foreach> ::= foreach '(' <expr> as <var> [ '=>' <var> ] ')' '{' <code> '}'
	private void procForEach(){
		eat(TokenType.FOREACH);
		eat(TokenType.PARAN_OPEN);
		procExpr();
		eat(TokenType.AS);
		procVar();
		if (current.type == TokenType.ACCESS_ARRAY){
			advance();
			procVar();
		}
		eat(TokenType.PARAN_CLOSE);
		eat(TokenType.BRACK_OPEN);
		procCode();
		eat(TokenType.BRACK_CLOSE);
	}

	//<echo> ::= echo <expr> ';'
	private void procEcho() {
        eat(TokenType.ECHO);
		procExpr();
		eat(TokenType.SEMICOLON);
    }

	//<assign> ::= <value> [ ('=' | '+=' | '-=' | '.=' | '*=' | '/=' | '%=') <expr> ] ';'
	private void procAssign() {
		procValue();

		if (current.type == TokenType.ASSIGN ||
			current.type == TokenType.INCR_ASSIGN ||
			current.type == TokenType.DECR_ASSIGN ||
			current.type == TokenType.CONCAT_ASSIGN ||
			current.type == TokenType.MUX_ASSIGN ||
			current.type == TokenType.DIV_ASSIGN ||
			current.type == TokenType.MOD_ASSIGN
			){
				switch (current.type){
					case ASSIGN:
					case INCR_ASSIGN:
					case DECR_ASSIGN:
					case CONCAT_ASSIGN:
					case MUX_ASSIGN:
					case DIV_ASSIGN:
					case MOD_ASSIGN:
						advance();
						procExpr();
						break;
					default:
						//unreacheable
						break;
				}
			}

        eat(TokenType.SEMICOLON);
    }

	//<boolexpr> ::= [ '!' ] <cmpexpr> [ (and | or) <boolexpr> ]
	private void procBoolExpr() {
        if (current.type == TokenType.INVERSE) {
            advance();
		}
		procCmpExpr();
		if (current.type == TokenType.AND ||
			current.type == TokenType.OR){
			switch (current.type) {
				case AND:
				case OR:
					advance();
					break;
				default:
					showError();
					break;
			}
		}
		procBoolExpr();
    }

	//<cmpexpr> ::= <expr> ('==' | '!=' | '<' | '>' | '<=' | '>=') <expr>
	private void procCmpExpr(){
		procExpr();
		if (current.type == TokenType.EQUAL ||
			current.type == TokenType.NOT_EQUAL ||
			current.type == TokenType.LOWER ||
			current.type == TokenType.GREATER ||
			current.type == TokenType.LOWER_EQUAL ||
			current.type == TokenType.GREATER_EQUAL){
			switch (current.type) {
				case EQUAL:
				case NOT_EQUAL:
				case LOWER:
				case GREATER:
				case LOWER_EQUAL:
				case GREATER_EQUAL:
					advance();
					break;
				default:
					showError();
					break;
			}
		}
		procExpr();
	}

	//<expr> ::= <term> { ('+' | '-' | '.') <term> }
	private void procExpr(){
		procTerm();

		while (	current.type == TokenType.ADD ||
				current.type == TokenType.SUB ||
				current.type == TokenType.CONCATENATE){
					switch (current.type){
						case ADD:
						case SUB:
						case CONCATENATE:
							advance();
							break;
						default:
							showError();
							break;
					}
					procTerm();
				}

	}

	//<term> ::= <factor> { ('*' | '/' | '%') <factor> }
	private void procTerm(){
		procFactor();
		while (	current.type == TokenType.MUL ||
				current.type == TokenType.DIV ||
				current.type == TokenType.MOD){
			switch (current.type){
				case MUL:
				case DIV:
				case MOD:
					advance();
					break;
				default:
					showError();
					break;
			}
			procFactor();
		}

	}

	//<factor> ::= <number> | <string> | <array> | <read> | <value>
	private void procFactor(){
		switch (current.type){
			case NUMBER:
				procNumber();
			break;
			case STRING:
				procString();
			break;
			case ARRAY:
				procArray();
			break;
			case READ:
				procRead();
			break;
			case INCREMENT: //<value> ::= [ ('++' | '—-') ] <access> | <access> [ ('++' | '--') ]
			case DECREMENT:
			case VAR:       //<varvar> ::= '$' <varvar> | <var>
			case PARAN_OPEN: //<access> ::= ( <varvar> | '(' <expr> ')' ) [ '[' <expr> ']' ]
			case BRACE_OPEN:
				procValue();
			break;
			default:
				showError();
				break;
		}
	}

	//<array> ::= array '(' [ <expr> '=>' <expr> { ',' <expr> '=>' <expr> } ] ')'
	private void procArray(){
		eat(TokenType.ARRAY);
		eat(TokenType.PARAN_OPEN);
		if(current.type != TokenType.PARAN_CLOSE){
			procExpr();
			eat(TokenType.ACCESS_ARRAY);
			procExpr();
			while ( current.type == TokenType.COMMA){
					eat(TokenType.COMMA);
					procExpr();
					eat(TokenType.ACCESS_ARRAY);
					procExpr();
				}
			}
		eat(TokenType.PARAN_CLOSE);
	}

	//<read> ::= read <expr>
	private void procRead(){
		eat(TokenType.READ);
		procExpr();
	}

	//<value> ::= [ ('++' | '—-') ] <access> | <access> [ ('++' | '--') ]
	private void procValue(){
		if (current.type == TokenType.INCREMENT ||
			current.type == TokenType.DECREMENT){
				advance();
				procAccess();
			}
		else{
			procAccess();
			if (current.type == TokenType.INCREMENT ||
				current.type == TokenType.DECREMENT){
				advance();
			}
		}
	}

	//<access> ::= ( <varvar> | '(' <expr> ')' ) [ '[' <expr> ']' ]
	private void procAccess(){
		if(current.type == TokenType.PARAN_OPEN){
			eat(TokenType.PARAN_OPEN);
			procExpr();
			eat(TokenType.PARAN_CLOSE);
		}else procVarVar();
		if(current.type == TokenType.BRACE_OPEN){
			eat(TokenType.BRACE_OPEN);
			procExpr();
			eat(TokenType.BRACE_CLOSE);
		}
	}

	//<varvar> ::= '$' <varvar> | <var>
	private void procVarVar(){
		while(current.type == TokenType.VARVAR){
			eat(TokenType.VARVAR);
			procVarVar();
		}
		procVar();
	}

	//<var> ::= <var>
	private void procVar(){
		eat(TokenType.VAR);
	}

	//<number> ::= number
    private void procNumber() {
        eat(TokenType.NUMBER);
	}

	//<string> ::= string
    private void procString() {
        eat(TokenType.STRING);
    }


}
