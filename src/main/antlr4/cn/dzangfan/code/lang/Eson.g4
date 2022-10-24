grammar Eson;

progLibrary
:
    object
;

progExecutive
:
    lambda
;

value
:
    SYMBOL
    | STRING
    | NUMBER
    | SPECIALVALUE
    | ID
    | object
    | array
    | lambda
    | application
;

array
:
    '[' value
    (
        ',' value
    )*
    (
        ',' '...' ID
    )? ']'
    | '[' ']'
;

object
:
    '{' objectPair
    (
        ',' objectPair
    )*
    (
        ',' '...' ID
    )? '}'
    | '{' '}'
;

lambda
:
    '#' valueMap
    (
        '|' valueMap
    )* '#'
;

application
:
    '(' value+ ')'
;

valueMap
:
    value '=>' value
;

objectPair
:
    ID ':' value
    | ID
;

SYMBOL
:
    '\'' ID
;

STRING
:
    '"'
    (
        ESCAPE
        | ~["\\]
    )* '"'
;

NUMBER
:
    '-'? INTEGER
;

SPECIALVALUE
:
    'true'
    | 'false'
    | 'null'
;

ID
:
    [_a-zA-Z] [_a-zA-Z0-9]*
;

fragment
ESCAPE
:
    '\\'
    (
        ["\\/bfnrt]
        | UNICODE
    )
;

fragment
UNICODE
:
    'u' HEXCHAR HEXCHAR HEXCHAR HEXCHAR
;

fragment
HEXCHAR
:
    [0-9a-fA-F]
;

fragment
INTEGER
:
    '0'
    | [1-9] [0-9]+
;

WS
:
    [ \t\r\n]+ -> skip
;