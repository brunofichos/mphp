package lexical;

public enum TokenType {
	// Specials
	UNEXPECTED_EOF,
	INVALID_TOKEN,
	END_OF_FILE,

	// Symbols
	SEMICOLON,     // ;
	CONCATENATE,   // .

	//Assigment operators
	ASSIGN,        // =
	INCR_ASSIGN,   // +=
	DECR_ASSIGN,   // -=
	MUX_ASSIGN,    // *=
	DIV_ASSIGN,    // /=
	MOD_ASSIGN,    // %=
	INCREMENT,     // ++
	DECREMENT,     // --

	// Logic operators
	EQUAL,         // ==
	NOT_EQUAL,     // !=
	LOWER,         // <
	LOWER_EQUAL,   // <=
	GREATER,       // >
	GREATER_EQUAL, // >=
	AND,           // and
	OR,            // or

	// Arithmetic operators
	ADD,           // +
	SUB,           // -
	MUL,           // *
	DIV,           // /
	MOD,           // %

	// Keywords
	WHILE,         // while
	IF,            // if
	TRUE,          // true
	FOREACH,	   // foreach
	ECHO,          // echo

	// Others
	NUMBER,        // number
	READ,          // read
	VAR            // variable

}
