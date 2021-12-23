grammar ConfigFile;

/*
@header {
    package antlr;
}
*/

start
:
    lines? EOF
;

lines
:
    line
    | lines line
;

line
:
    Key '=' Value
    | Key '=' Value Comment
    | Comment
;

Key
:
    'DATASET_PATH'
    | 'DB_CONNECTION_STRING'
    | 'DB_ENDPOINT'
    | 'DB_PASSWD'
    | 'DB_TYPE'
    | 'DB_USER'
    | 'NEIGHBOR_SIZE'
    | 'NUMBER_RECS'
    | 'USER_ID'
;

Value
:
    [0-9a-zA-Z./:]+
;

Comment
:
    '#' ~[\n\r\t]*
;

WS : [ \r\t\n]+ -> skip ;