grammar Eson;

value
:
    condValue # valueCondValue
    | lambda # valueLambda
    | application # valueApplication
;

condValue
:
    SYMBOL # valueSymbol
    | STRING # valueString
    | number # valueNumber
    | SPECIALVALUE # valueSpecial
    | ID # valueId
    | object # valueObject
    | array # valueArray
;

array
:
    '[' values+=value
    (
        ','? values+=value
    )*
    (
        ','? '...' rest=value
    )? ']' # loadedArray
    | '[' ']' # emptyArray
;

object
:
    '{' objectPair
    (
        ','? objectPair
    )*
    (
        ','? '...' rest=value
    )? '}' # loadedObject
    | '{' '}' # emptyObject
;

lambda
:
    '#' valueMap
    (
        '|' valueMap
    )* '#'
;

valueMap
:
    condValue '=>' value # valueMapNormal
    | '(' params += condValue params += condValue+ ')' '=>' result = value #
    valueMapShortcut
;

application
:
    '(' value value+ ')'
;

objectPair
:
    ID
    (
        ':' value
    )?
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

number
:
    '-'? INTEGER # integerNumber
    | '-'? DOUBLE # doubleNumber
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

INTEGER
:
    '0'
    | [1-9] [0-9]*
;

fragment
DIGITS
:
    [0-9]+
;

DOUBLE
:
    INTEGER FRACTION
    | INTEGER EXPONENT
    | INTEGER FRACTION EXPONENT
;

fragment
FRACTION
:
    '.' DIGITS
;

fragment
EXPONENT
:
    [eE] [-+]? DIGITS
;

WS
:
    [ \t\r\n]+ -> skip
;