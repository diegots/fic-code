%{
#include <stdlib.h>
#include "sintacticoH.tab.h"
%}
lanzar	lanzar|lanzar" "dado
avanzar mover|avanzar
digito 	[0-9]
arriba 	arriba|up
abajo 	abajo|down
izq	izquierda|izq|left
der	derecha|der|right
salir	salir|exit
ayuda 	ayuda|help
info	info
finTurno	fin" "turno|fin" "de" "turno

%%
{lanzar}	{return LANZAR;}
{avanzar}	{return AVANZAR;}
{digito}	{yylval.entero = atoi(yytext); return DIGITO;}
{arriba}	{return ARRIBA;}
{abajo} 	{return ABAJO;}
{izq}		{return IZQ;}
{der}		{return DER;}
{salir}		{return EXIT;}
{ayuda}		{return AYUDA;}
{info}		{return INFO;}
{finTurno}	{return FINALTURNO;}
\t 			
\n		{return '\n';}	
.
%%
