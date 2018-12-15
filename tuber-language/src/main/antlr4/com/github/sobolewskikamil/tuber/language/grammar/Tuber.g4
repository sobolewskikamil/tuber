grammar Tuber;

parse
 : functionDeclaration+ EOF
 ;

iterationStatement
 : statement
 | ifIterationStatement
 | Continue Semicolon
 | Break Semicolon
 ;

nonIterationStatement
 : statement
 | ifStatement
 ;

statement
 : assignment
 | functionCall Semicolon
 | whileStatement
 | returnStatement
 ;

functionDeclaration
  : Def Identifier OpenParen idList? CloseParen OpenBrace nonIterationStatement* CloseBrace
  ;

assignment
 : variableAssignment
 | arrayElementAssignment
 ;

variableAssignment
 : Identifier Assign right=expression Semicolon
 ;

arrayElementAssignment
 : arrayElement Assign expression Semicolon
 ;

functionCall
 : Identifier OpenParen (expression ( Comma expression )*)? CloseParen
 ;

ifStatement
 : ifStat thenStat elseStat?
 ;

ifIterationStatement
 : ifStat thenIterStat elseIterStat?
 ;

whileStatement
 : whileStat iterStat
 ;

returnStatement
 : Return expression Semicolon
 ;

idList
  : Identifier ( Comma Identifier )*
  ;

ifStat
 : If OpenParen expression CloseParen
 ;

thenStat
 : OpenBrace nonIterationStatement* CloseBrace
 ;

elseStat
 : Else OpenBrace nonIterationStatement* CloseBrace
 ;

thenIterStat
 : OpenBrace iterationStatement* CloseBrace
 ;

elseIterStat
 : Else OpenBrace iterationStatement* CloseBrace
 ;

whileStat
 : While OpenParen expression CloseParen
 ;

iterStat
 : OpenBrace iterationStatement* CloseBrace
 ;

expression
 : op=Not expression
 | left=expression op=( Add | Subtract ) right=expression
 | left=expression op=( Multiply | Divide | Modulus ) right=expression
 | left=expression op=Exp right=expression
 | left=expression op=( Equal | NotEqual ) right=expression
 | left=expression op=( GreaterOrEqual | LessOrEqual | Greater | Less ) right=expression
 | left=expression op=( And | Or ) right=expression
 | literal
 | functionCall
 | array
 | arrayElement
 | OpenParen expression CloseParen
 ;

array
 : OpenBrace expressionList? CloseBrace
 ;

expressionList
 : expression ( Comma expression )*
 ;

arrayElement
 : Identifier ('[' expression ']')+
 ;

literal
 : Null
 | Long
 | Double
 | Bool
 | Identifier
 | String
 ;

Def            : 'def';
If             : 'if';
Else           : 'else';
Return         : 'return';
For            : 'for';
While          : 'while';
Continue       : 'continue';
Break          : 'break';
Or             : '||';
And            : '&&';
Equal          : '==';
NotEqual       : '!=';
GreaterOrEqual : '>=';
LessOrEqual    : '<=';
Exp            : '^';
Not            : '!';
Greater        : '>';
Less           : '<';
Add            : '+';
Subtract       : '-';
Multiply       : '*';
Divide         : '/';
Modulus        : '%';
OpenBrace      : '{';
CloseBrace     : '}';
OpenParen      : '(';
CloseParen     : ')';
OpenBracket    : '[';
CloseBracket   : ']';
Semicolon      : ';';
Assign         : '=';
Comma          : ',';
Dot            : '.';

Null
 : 'null'
 ;

Bool
 : 'true'
 | 'false'
 ;

Long
 : Int
 ;

Double
 : Int Dot Digit*
 ;

Identifier
 : [a-zA-Z_] [a-zA-Z_0-9]*
 ;

String
 : ["] ( ~["\r\n\\] | '\\' ~[\r\n] )* ["]
 ;

Comment
 : ( '//' ~[\r\n]* | '/*' .*? '*/' ) -> skip
 ;

Whitespace
 : [ \r\n\t]+ -> skip
 ;

fragment Int
 : [1-9] Digit*
 | '0'
 ;

fragment Digit
 : [0-9]
 ;
