%{
#include <stdlib.h>
#include "sintacticoH.tab.h"
%}
lanzar	(?i:lanzar?(" "([el|los]" ")?dados?)?)
avanzar (?i:mover|avanzar?)
atacar	(?i:atacar?)
equipar	(?i:equipar?)
tirar	(?i:tirar?)
mano_izq	(?i:mano" "izquierda)
mano_der	(?i:mano" "derecha)
abrirPuerta	(?i:abrir" "puerta)
abrirCofre	(?i:abrir" "cofre)
usar	(?i:usar?)
espada	(?i:espada)
pocion	(?i:pocion)
llave	(?i:llave|key)
digito 	[0-9]
arriba 	(?i:arriba|up|"[A")
abajo 	(?i:abajo|down|"[B")
izq	(?i:izquierda|izq|left|"[D")
der	(?i:derecha|der|right|"[C")
salir	(?i:salir|exit|q)
ayuda 	(?i:ayuda|help)
info	(?i:(ver" ")?info(rmacion)?)
reglas	(?i:([mostrar|ver]" ")?reglas)
finTurno	(?i:fin(alizar)?" "(de" ")?turno)

%%
{lanzar}	{return LANZAR;}
{avanzar}	{return AVANZAR;}
{atacar}	{return ATACAR;}
{equipar}	{return EQUIPAR;}
{tirar}		{return TIRAR;}
{mano_izq}	{return MANOIZQ;}
{mano_der}	{return MANODER;}
{abrirPuerta}	{return ABRIRPUERTA;}
{abrirCofre}	{return ABRIRCOFRE;}
{usar}		{return USAR;}
{espada}	{return ESPADA;}
{pocion}	{return POCION;}
{llave}		{return LLAVE;}
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
