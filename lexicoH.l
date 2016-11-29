%{
#include <stdlib.h>
#include "sintacticoH.tab.h"
%}
lanzar	lanzar|lanzar" "dado?s
avanzar mover|avanzar
abrirPuerta	abrir" "puerta
abrirCofre	abrir" "cofre
usar	usar
espada	espada
pocion	pocion
digito 	[0-9]
arriba 	arriba|up|"[A"
abajo 	abajo|down|"[B"
izq	izquierda|izq|left|"[D"
der	derecha|der|right|"[C"
salir	salir|exit|q
ayuda 	ayuda|help
info	info
reglas	reglas|mostrar" "reglas\ver" "reglas
finTurno	fin" "turno|fin" "de" "turno

%%
{lanzar}	{return LANZAR;}
{avanzar}	{return AVANZAR;}
{abrirPuerta}	{return ABRIRPUERTA;}
{abrirCofre}	{return ABRIRCOFRE;}
{usar}		{return USAR;}
{espada}	{return ESPADA;}
{pocion}	{return POCION;}
{digito}	{yylval.entero = atoi(yytext); return DIGITO;}
{arriba}	{return ARRIBA;}
{abajo} 	{return ABAJO;}
{izq}		{return IZQ;}
{der}		{return DER;}
{salir}		{return EXIT;}
{ayuda}		{return AYUDA;}
{info}		{return INFO;}
{reglas}	{return REGLAS;}
{finTurno}	{return FINALTURNO;}
\t 			
\n		{return '\n';}	
.
%%
