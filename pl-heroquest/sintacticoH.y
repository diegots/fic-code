%{
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include <unistd.h>
#include <math.h>
#include "cabeceras.h"


/* Variables globales */

// Mapa
tmapa *tablero;
tpuerta *puerta_salida;
int FILAS, COLUMNAS, NUM_PUERTAS, NUM_ENEMIGOS, NUM_COFRES;

// Numero de objetos en la mochila
int num_objetos_mochila = 0;

// Valor actual del dado
int valor_dado = 0; 

// Se utiliza para controlar si el dado ya ha sido lanzado en el turno actual
int lanzado = 0;

// Se utiliza para controlar si ya se ha hecho un ataque durante el turno actual
int ataque = 0;

/* Variables utilizadas para saber cuanto se mueve el personaje en cada 
direccion */
int arriba, abajo, derecha, izquierda = 0; 

// Posicion virtual del jugador mientras se mueve, si no hay colision, si no hay
// colision se actualiza la poscion del personaje con la poscion virtual, en 
// caso contrario se muestra un error y el personaje no se mueve
int posfilasVirtual,poscolumnasVirtual = 0; 
// Entero utilizado para marcar si se ha producido una colision
int haycolision = 0;
// Personaje
tjugador *prsj;

/* Cabeceras de funciones */
void yyerror (char const *);



%}
%union{
	int entero;
}
%token LANZAR AVANZAR ATACAR EQUIPAR TIRAR MANOIZQ MANODER ABRIRPUERTA ABRIRCOFRE USAR ESPADA POCION LLAVE ARRIBA ABAJO 
DER IZQ EXIT AYUDA INFO REGLAS FINALTURNO
%token <entero> DIGITO
%type <entero> movimientos accion direccion objeto
%start S
%%

/* Inicio reglas */

S : 	LANZAR '\n' {
		return regla_lanzar();
	}
	| ARRIBA '\n'{
		return regla_arriba();		
	}
	| ABAJO '\n'{
		return regla_abajo();	
	}
	| DER '\n'{
		return regla_der();
	}
	| IZQ '\n'{
		return regla_izq();
	}	
	| AVANZAR movimientos {
		return regla_avanzar();
	}
	| ATACAR direccion '\n'{
		return regla_atacar($2);
	}
	| EQUIPAR objeto MANOIZQ '\n'{
		return regla_equipar($2, MANO_IZQUIERDA);
	}
	| EQUIPAR MANOIZQ objeto '\n' {
		return regla_equipar($3, MANO_IZQUIERDA);
	}
	| EQUIPAR objeto MANODER '\n'{
		return regla_equipar($2, MANO_DERECHA);
	}
	| EQUIPAR MANODER objeto '\n' {
		return regla_equipar($3, MANO_DERECHA);
	}
	| TIRAR objeto '\n' {
		// Implementar
		int i;
		for(i=0;i<num_objetos_mochila;i++) {
			if (prsj->mochila[i].tipo == $2) {
				// Eliminar de la mochila
				printf("%s eliminada\n", prsj->mochila[i].nombre);
				/* En la posicion que ha quedado vacia 
				colocamos el ultimo objeto de la mochila para 
				que todos los objetos queden en las primera 
				posiciones. En el caso de que fuese el ultimo 
				objeto de la mochila, ya se habria puesto 
				anteriormente a 0 por lo que no pasa nada, 
				simplemente se reducen el numero de objetos */
				prsj->mochila[i] = prsj->mochila[num_objetos_mochila-1];
					
				prsj->mochila[num_objetos_mochila-1].tipo = 0;
				prsj->mochila[num_objetos_mochila-1].nombre = "";
				prsj->mochila[num_objetos_mochila-1].valor = 0; 
				num_objetos_mochila--;
				break;
			}
		}
		return 1;
	}
	| TIRAR objeto MANOIZQ '\n' {
		// Implementar
		if (prsj->mano_izquierda -> tipo == $2) {
			free(prsj->mano_izquierda);
			prsj->mano_izquierda = NULL;
		}
		return 1;
	}
	| TIRAR objeto MANODER '\n' {
		// Implementar
		if (prsj->mano_izquierda -> tipo == $2) {
			free(prsj->mano_izquierda);
			prsj->mano_izquierda = NULL;
		}
		return 1;
	}
	| ABRIRPUERTA '\n' {
		return regla_abrir_puerta();
	}
	| ABRIRCOFRE '\n' {
		return regla_abrir_cofre();
	}
	| USAR objeto '\n' {
		return regla_usar_objeto($2);
	}
	| FINALTURNO '\n' {
		return regla_fin_turno();
	}
	| AYUDA '\n'{
		mostrar_ayuda();
		return 1;
	}
	| INFO '\n' {		
		clearScreen();
		mostrar_mapa(*tablero);
		mostrar_info_jugador(*prsj);
		return 1;
	}
	| REGLAS '\n' {
		mostrar_reglas();
		return 1;
	}
	| EXIT '\n' {
		animacion_por_pantalla("¡Hasta pronto!\n", DELAY);
		return 0;
	}
	| error '\n' {
		yyerror("Comando desconocido");
		yyclearin;
		mostrar_ayuda(); 
		return 1;
	}
	| error direccion '\n' {
		yyerror("Comando desconocido");
		yyclearin;
		mostrar_ayuda(); 
		return 1;
	}
	;
objeto:	ESPADA { // En este caso es la regla superior la que hace el return
		$$ = OBJ_ESPADA;
	}
	| POCION {
		$$ = OBJ_POCION;		
	}
	| LLAVE {
		$$ = OBJ_LLAVE;
	}
	;
movimientos : 	accion movimientos {}
	      	| accion '\n' {}
              	;
accion : 	DIGITO direccion {
			regla_accion($1, $2);		
		}
		;
direccion : ARRIBA {
		$$ = D_ARRIBA;
	}
	| ABAJO	{
		$$ = D_ABAJO;
	}
	| DER	{
		$$ = D_DERECHA;
	}
	| IZQ	{
		$$ = D_IZQUIERDA;
	}

%%

/* Fin reglas */

/* Funciones principales */
int regla_lanzar(){
	// Se comprueba si se ha lanzado el dado ya
	if (!lanzado){
		animacion_por_pantalla("Se lanza el dado", DELAY);
		animacion_por_pantalla("...\n", DELAY*8);

		// Generar numero aleatorio entre 1 y 9
		valor_dado = get_random_number();

		
		char cadena[100];
		sprintf(cadena, "Has sacado un %i.", valor_dado);
		animacion_por_pantalla(cadena, DELAY);
		lanzado = 1;

		// Se actualiza la posicion virtual
		posfilasVirtual = prsj->pos_fila;
		poscolumnasVirtual = prsj->pos_columna;

	} else if (valor_dado==0){ // Si ya has lanzado y no puedes moverte
		animacion_por_pantalla("Ya has lanzado el dado y no te quedan \
movimientos. Si quieres moverte tienes que finalizar tu turno.",DELAY);
	} else { // Si ya has lanzado y puedes moverte
		animacion_por_pantalla("Ya has lanzado el dado, mueve al personaje.", DELAY);		
	}		
	return 1;
}

int regla_arriba(){
	/* Se llama a la regla de avanzar con 1 solo movimiento y la direccion 
	adecuada */
	regla_accion(1, D_ARRIBA);
	regla_avanzar();
}

int regla_abajo(){
	/* Se llama a la regla de avanzar con 1 solo movimiento y la direccion 
	adecuada */
	regla_accion(1, D_ABAJO);
	regla_avanzar();
}

int regla_der() {
	/* Se llama a la regla de avanzar con 1 solo movimiento y la direccion 
	adecuada */
	regla_accion(1, D_DERECHA);
	regla_avanzar();
}

int regla_izq() {
	/* Se llama a la regla de avanzar con 1 solo movimiento y la direccion 
	adecuada */
	regla_accion(1, D_IZQUIERDA);
	regla_avanzar();
}

int regla_avanzar() {		
	clearScreen();
	// Si hay colision..
	if (haycolision) {
		animacion_por_pantalla("No puedes hacer ese movimiento.\n", DELAY);
		// Se reinician las posiciones virtuales
		posfilasVirtual = prsj->pos_fila;
		poscolumnasVirtual = prsj->pos_columna;
		// Se reinician las variables necesarias
		arriba = abajo = izquierda = derecha = 0;
	} else if (!lanzado){ // Ya se ha lanzado el dado
		animacion_por_pantalla("Aún no has lanzado el dado.\n", DELAY);		
		// Se reinician las variables necesarias
		arriba = abajo = izquierda = derecha = 0;
	} else if (arriba+abajo+izquierda+derecha>valor_dado){ // Se han hecho movimientos demás
		animacion_por_pantalla("Movimientos demás!\n", DELAY);
		// Se reinician las variables necesarias
		arriba = abajo = izquierda = derecha = 0;
		// Se reinician las posiciones virtuales		
		posfilasVirtual = prsj->pos_fila;
		poscolumnasVirtual = prsj->pos_columna;			
	} else if (ataque) { // Si ya se ha atacado no se puede mover
		animacion_por_pantalla("Una vez hecho un movimiento de ataque, no puedes moverte hasta el turno siguiente\n", DELAY);
	} else{ // Movimiento correcto
		/* Se le quita al numero de pasos los movimientos que se van a 
		hacer */
		valor_dado -= (arriba + abajo + derecha + izquierda);
		// Se actualiza el mapa
		tablero->mapa[prsj->pos_fila][prsj->pos_columna] = ' ';
		/* Se vuelcan las posiciones virtuales en las del personaje */
		prsj->pos_fila = posfilasVirtual;
		prsj->pos_columna = poscolumnasVirtual;
		tablero->mapa[prsj->pos_fila][prsj->pos_columna] = 'P';
		if (valor_dado>0) { // Aun quedan movimientos
			char cadena[100];
			if (valor_dado==1) {
				sprintf(cadena, "Puedes seguir moviendote si lo deseas.\
				\nTe queda %i movimiento", valor_dado);
			} else {
				sprintf(cadena, "Puedes seguir moviendote si lo deseas.\
				\nTe quedan %i movimientos", valor_dado);
			}
			animacion_por_pantalla(cadena, DELAY);
		} else { // No quedan movimientos
			animacion_por_pantalla("No te quedan más movimientos\n", DELAY);
		}
		
		arriba = abajo = izquierda = derecha = 0;
	}			
	printf("\n");
	mostrar_mapa(*tablero);
	haycolision = 0; 
	return 1;
}

int regla_atacar(int direccion) {
	// Se comprueba que no se haya atacado ya este turno
	if (!ataque) {
		animacion_por_pantalla("Atacando", DELAY);
		animacion_por_pantalla("...\n", DELAY*8);
		/* Se comprueba si en la direccion pasada hay un enemigo */
		switch (direccion){
			case D_ARRIBA:		
				// Si hay un enemigo cerca..
				if (tablero->mapa[prsj->pos_fila-1][prsj->pos_columna] == 'E'){
					// Se simula el ataque
					simula_ataque(prsj->pos_fila-1, prsj->pos_columna);
					/* La variable de ataque se pone a 1 
					para que no se pueda volver a atacar en 
					el mismo turno, pero solo si realmente 
					se ha atacado, si el usuario ataca y no 
					tiene enemigos cerca esta variable no 
					se activa para que pueda atacar de 
					verdad en este mismo turno */
					ataque = 1;
				} else { // Sin enemigo cerca
					animacion_por_pantalla("No tienes enemigos cerca\n", DELAY);
				} 
				break;
			case D_ABAJO:					
				if (tablero->mapa[prsj->pos_fila+1][prsj->pos_columna] == 'E'){
					simula_ataque(prsj->pos_fila+1, prsj->pos_columna);
					ataque = 1;
				} else {
					animacion_por_pantalla("No tienes enemigos cerca\n", DELAY);
				}
				break;
			case D_DERECHA:
				if (tablero->mapa[prsj->pos_fila][prsj->pos_columna+1] == 'E'){
					simula_ataque(prsj->pos_fila, prsj->pos_columna+1);
					ataque = 1;
				} else {
					animacion_por_pantalla("No tienes enemigos cerca\n", DELAY);
				}
				break;
			case D_IZQUIERDA:
				if (tablero->mapa[prsj->pos_fila][prsj->pos_columna-1] == 'E'){
					simula_ataque(prsj->pos_fila, prsj->pos_columna-1);
					ataque = 1;
				} else {
					animacion_por_pantalla("No tienes enemigos cerca\n", DELAY);
				}
				break;
		}
		return 1;
	} else {
		animacion_por_pantalla("Ya ha atacado este turno.\n", DELAY);
	}
}

int regla_equipar(int tipo_objeto, int mano) {
	
	char cadena[100];
	// Si tenemos la mano ocupada mostrarlo y salir
	if (mano_ocupada(mano)) {
		animacion_por_pantalla("Mano ocupada\n", DELAY);
		return 1;
	}
	// Se comprueba si se tiene el objeto
	int i;
	for(i = 0; i<num_objetos_mochila; i++) {
		if (tipo_objeto == prsj->mochila[i].tipo){
			switch(tipo_objeto) {
				case OBJ_ESPADA:
					switch(mano) {
						case MANO_DERECHA:
							sprintf(cadena, "Equipas tu %s en la mano derecha.\n", prsj->mochila[i].nombre);
							animacion_por_pantalla(cadena, DELAY);
							// Equipas la espada
							prsj->mano_derecha = (tobjeto*) malloc(sizeof(struct objeto));
							prsj->mano_derecha -> tipo = prsj->mochila[i].tipo;
							prsj->mano_derecha -> nombre = prsj->mochila[i].nombre;
							prsj->mano_derecha -> durabilidad = prsj->mochila[i].durabilidad;
							prsj->mano_derecha -> valor = prsj->mochila[i].valor;
							break;
						case MANO_IZQUIERDA:
							sprintf(cadena, "Equipas tu %s en la mano izquierda.\n", prsj->mochila[i].nombre);
							animacion_por_pantalla(cadena, DELAY);
							// Equipas la espada
							prsj->mano_izquierda = (tobjeto*) malloc(sizeof(struct objeto));
							prsj->mano_izquierda -> tipo = prsj->mochila[i].tipo;
							prsj->mano_izquierda -> nombre = prsj->mochila[i].nombre;
							prsj->mano_izquierda -> durabilidad = prsj->mochila[i].durabilidad;
							prsj->mano_izquierda -> valor = prsj->mochila[i].valor;
							break;
					}

					// Se borra el objeto de la mochila
					/* En la posicion que ha quedado vacia 
					colocamos el ultimo objeto de la mochila para 
					que todos los objetos queden en las primera 
					posiciones. En el caso de que fuese el ultimo 
					objeto de la mochila, ya se habria puesto 
					anteriormente a 0 por lo que no pasa nada, 
					simplemente se reducen el numero de objetos */
					prsj->mochila[i] = prsj->mochila[num_objetos_mochila-1];
					
					prsj->mochila[num_objetos_mochila-1].tipo = 0;
					prsj->mochila[num_objetos_mochila-1].nombre = "";
					prsj->mochila[num_objetos_mochila-1].valor = 0;
					prsj->mochila[num_objetos_mochila-1].durabilidad = 0; 
					num_objetos_mochila--;

					return 1;
					break;
				case OBJ_POCION: // estos dos casos podrian aglutinarse en default, son obejtos que no pueden ser equipados
					sprintf(cadena, "No puedes equipar un(a) %s.\n", prsj->mochila[i].nombre);
					animacion_por_pantalla(cadena, DELAY);
					return 1;
					break;
				case OBJ_LLAVE:
					sprintf(cadena, "No puedes equipar un(a) %s.\n", prsj->mochila[i].nombre);
					animacion_por_pantalla(cadena, DELAY);
					return 1;
					break;
			}
		}
	}
	return 1;
}

int regla_abrir_puerta() {
	/* Se comprueba que hay una puerta cerca */
	if (tablero->mapa[prsj->pos_fila+1][prsj->pos_columna] == '-') {
		// Si es la puerta de salida se cambia de mapa
		if (tablero->lista_puertas[NUM_PUERTAS-1].fila == prsj->pos_fila+1 && tablero->lista_puertas[NUM_PUERTAS-1].columna == prsj->pos_columna) {		
			clearScreen();
			animacion_por_pantalla("La puerta está abierta, decides cruzar y llegas a una nueva sala...\n", DELAY);
			// Liberar memoria
			liberar_mapa(&tablero);
			// Cargar mapa
			cargar_mapa(&tablero);
			// Crear puertas			
			crear_entradas(&tablero);
			// Se muestra el nuevo mapa
			mostrar_mapa(*tablero);
			return 1;
		} else { // Si es otra puerta
			// Se comprueba si la puerta esta abierta o cerrada
			if (puerta_cerrada(prsj->pos_fila+1, prsj->pos_columna)) {
				animacion_por_pantalla("Puerta cerrada.\n", DELAY);
				return 1;
			} else {
				// Se abre la puerta
				tablero->mapa[prsj->pos_fila+1][prsj->pos_columna] = ' ';
				animacion_por_pantalla("Puerta abierta.\n", DELAY);
				return 1;
			}
		}
	}
	if (tablero->mapa[prsj->pos_fila-1][prsj->pos_columna] == '-') {
		// Si es la puerta de salida se cambia de mapa
		if (tablero->lista_puertas[NUM_PUERTAS-1].fila == prsj->pos_fila-1 && tablero->lista_puertas[NUM_PUERTAS-1].columna == prsj->pos_columna) {		
			clearScreen();
			animacion_por_pantalla("La puerta está abierta, decides cruzar y llegas a una nueva sala...\n", DELAY);
			// Liberar memoria
			liberar_mapa(&tablero);
			// Cargar mapa
			cargar_mapa(&tablero);			
			crear_entradas(&tablero);
			// Se muestra el nuevo mapa
			mostrar_mapa(*tablero);
			return 1;
		} else { // Si es otra puerta
			// Se comprueba si la puerta esta abierta o cerrada
			if (puerta_cerrada(prsj->pos_fila-1, prsj->pos_columna)) {
				animacion_por_pantalla("Puerta cerrada.\n", DELAY);
				return 1;
			} else {
				// Se abre la puerta
				tablero->mapa[prsj->pos_fila-1][prsj->pos_columna] = ' ';
				animacion_por_pantalla("Puerta abierta.\n", DELAY);
				return 1;
			}
		}
	}
	if (tablero->mapa[prsj->pos_fila][prsj->pos_columna+1] == '|') {
		// Si es la puerta de salida se cambia de mapa
		if (tablero->lista_puertas[NUM_PUERTAS-1].fila == prsj->pos_fila && tablero->lista_puertas[NUM_PUERTAS-1].columna == prsj->pos_columna+1) {		
			clearScreen();	
			animacion_por_pantalla("La puerta está abierta, decides cruzar y llegas a una nueva sala...\n", DELAY);
			// Liberar memoria
			liberar_mapa(&tablero);
			// Cargar mapa
			cargar_mapa(&tablero);			
			crear_entradas(&tablero);
			// Se muestra el nuevo mapa
			mostrar_mapa(*tablero);
			return 1;
		} else { // Si es otra puerta
			// Se comprueba si la puerta esta abierta o cerrada
			if (puerta_cerrada(prsj->pos_fila, prsj->pos_columna+1)) {
				animacion_por_pantalla("Puerta cerrada.\n", DELAY);
				return 1;
			} else {
				// Se abre la puerta
				tablero->mapa[prsj->pos_fila][prsj->pos_columna+1] = ' ';
				animacion_por_pantalla("Puerta abierta.\n", DELAY);
				return 1;
			}
		}
	}
	if (tablero->mapa[prsj->pos_fila][prsj->pos_columna-1] == '|') {
		// Si es la puerta de salida se cambia de mapa
		if (tablero->lista_puertas[NUM_PUERTAS-1].fila == prsj->pos_fila && tablero->lista_puertas[NUM_PUERTAS-1].columna == prsj->pos_columna-1) {		
			clearScreen();
			animacion_por_pantalla("La puerta está abierta, decides cruzar y llegas a una nueva sala...\n", DELAY);
			// Liberar memoria
			liberar_mapa(&tablero);
			// Cargar mapa
			cargar_mapa(&tablero);			
			crear_entradas(&tablero);
			// Se muestra el nuevo mapa
			mostrar_mapa(*tablero);
			return 1;
		} else { // Si es otra puerta
			// Se comprueba si la puerta esta abierta o cerrada
			if (puerta_cerrada(prsj->pos_fila, prsj->pos_columna-1)) {
				animacion_por_pantalla("Puerta cerrada.\n", DELAY);
				return 1;
			} else {
				// Se abre la puerta
				tablero->mapa[prsj->pos_fila][prsj->pos_columna-1] = ' ';
				animacion_por_pantalla("Puerta abierta.\n", DELAY);
				return 1;
			}
		}
	}
	// Si se llega a este punto es que no hay ninguna puerta cerca
	animacion_por_pantalla("No tiene ninguna puerta cerca, y aún no has \
desarrollado la telequinesis, hable con Yoda o pruebe a acercarse, lo siento.\n", DELAY);
	return 1;
}

int regla_abrir_cofre() {
	// Se comprueba si existe hueco en la mochila
	if (num_objetos_mochila==TAM_MOCHILA) {
		animacion_por_pantalla("Mochila llena. No puede coger más objetos.\n", DELAY);
		return 1;
	}
	// Se comprueba si el personaje tiene un cofre al lado
	if (tablero->mapa[prsj->pos_fila+1][prsj->pos_columna] == 'C') {
		/* Si la puerta ya esta abierta termina */		
		if (!abre_cofre(prsj->pos_fila+1, prsj->pos_columna))
			return 1;		
	} else if (tablero->mapa[prsj->pos_fila-1][prsj->pos_columna] == 'C') {		
		if (!abre_cofre(prsj->pos_fila-1, prsj->pos_columna))
			return 1;		
	} else if (tablero->mapa[prsj->pos_fila][prsj->pos_columna+1] == 'C') {		
		if (!abre_cofre(prsj->pos_fila, prsj->pos_columna+1))
			return 1;		
	} else if (tablero->mapa[prsj->pos_fila][prsj->pos_columna-1] == 'C') {		
		if (!abre_cofre(prsj->pos_fila, prsj->pos_columna-1))
			return 1;		
	} else { // No tiene un cofre cerca
		animacion_por_pantalla("No tiene ningun cofre cerca, y aún no ha\
 desarrollado la telequinesis, hable con Yoda o pruebe a acercarse, lo siento.\n", DELAY);
		return 1;
	}

	/* Tiene un hueco en la mochila, y el cofre cerca, por lo tanto,
	se abre el cofre */
	animacion_por_pantalla("Abriendo cofre", DELAY);
	animacion_por_pantalla("...\n", DELAY*8);
	tobjeto objeto;
	// Obtienes el tipo del objeto que vas a crear de manera aleatoria
	int tipo = get_random_object();
	// En base al tipo inicializa el objeto y muestra un mensaje
	switch (tipo){
		case OBJ_POCION:
			objeto.tipo = tipo;
			objeto.nombre = "Pocion";
			objeto.valor = VALOR_POCION;
			objeto.durabilidad = 1;
			break;
		case OBJ_ESPADA:
			objeto.tipo = tipo;
			objeto.nombre = "Espada";
			objeto.valor = VALOR_ESPADA;
			objeto.durabilidad = 5;
			break;
		case OBJ_LLAVE:
			objeto.tipo = tipo;
			objeto.nombre = "Llave";
			objeto.valor = -1; // No se usa
			objeto.durabilidad = 1;
			break;
		default: // Cofre vacio, no se guarda el objeto
			animacion_por_pantalla("Está vacio. Más suerte la próxima vez.\n", DELAY);
			return 1;
			break;
	}
	
	
	char objeto_encontrado[100];
	sprintf(objeto_encontrado, "Has encontrado un(a) %s!\n", objeto.nombre);
	animacion_por_pantalla(objeto_encontrado, DELAY);

	// Se almacena el objeto
	prsj->mochila[num_objetos_mochila] = objeto;
	num_objetos_mochila++;
	return 1;
}

int regla_usar_objeto(int tipo_objeto) {		
	char cadena[100];
	int i;

	// Se recorre la lista de objetos
	for(i = 0; i<num_objetos_mochila; i++) {
		/* Si el tipo de objeto pasado por parametro coincide con uno 
		de la lista... */
		if (tipo_objeto == prsj->mochila[i].tipo) {
			switch(tipo_objeto) {
				case OBJ_ESPADA:
					sprintf(cadena, "La %s no tiene ninguna utilidad por si sola.", prsj->mochila[i].nombre);
					animacion_por_pantalla(cadena, DELAY);
					return 1;
					break;
				case OBJ_POCION:
					sprintf(cadena, "Bebes la %s y recuperas %i de vida.\n", prsj->mochila[i].nombre, prsj->mochila[i].valor);
					animacion_por_pantalla(cadena, DELAY);
					prsj->vida += prsj->mochila[i].valor;
					break;
				case OBJ_LLAVE:
					// Se comprueba si el personaje tiene una puerta al lado
					if (tablero->mapa[prsj->pos_fila+1][prsj->pos_columna] == '-') {		
						if (!abrir_puerta(prsj->pos_fila+1, prsj->pos_columna)) {
						// Si intentas abrir la puerta por la que has entrado no te deja y la llave no se gasta
							animacion_por_pantalla("No consigues abrirla, parece atascada...\n", DELAY);
							return 1;
						}
					}else if (tablero->mapa[prsj->pos_fila-1][prsj->pos_columna] == '-') {
						// Si intentas abrir la puerta por la que has entrado no te deja y la llave no se gasta		
						if (!abrir_puerta(prsj->pos_fila-1, prsj->pos_columna)) {
							animacion_por_pantalla("No consigues abrirla, parece atascada...\n", DELAY);
							return 1;
						}
					}else if (tablero->mapa[prsj->pos_fila][prsj->pos_columna+1] == '|') {
						// Si intentas abrir la puerta por la que has entrado no te deja y la llave no se gasta		
						if (!abrir_puerta(prsj->pos_fila, prsj->pos_columna+1)) {
							animacion_por_pantalla("No consigues abrirla, parece atascada...\n", DELAY);
							return 1;
						}
					}else if (tablero->mapa[prsj->pos_fila][prsj->pos_columna-1] == '|') {
						// Si intentas abrir la puerta por la que has entrado no te deja y la llave no se gasta		
						if (!abrir_puerta(prsj->pos_fila, prsj->pos_columna-1)) {
							animacion_por_pantalla("No consigues abrirla, parece atascada...\n", DELAY);
							return 1;
						}
					} else { // No tiene una puerta cerca
						animacion_por_pantalla("No tiene ninguna puerta cerca, y aún no ha\
desarrollado la telequinesis, hable con Yoda o pruebe a acercarse, lo siento.\n", DELAY);
						return 1;
					}
					sprintf(cadena, "Usas la %s.\n", prsj->mochila[i].nombre);
					animacion_por_pantalla(cadena, DELAY);
			}
			/* Cada vez que se usa el objeto pierde 1 de 
			durabilidad, si esta llega a 0 el objeto se pierde */
			if (!(--prsj->mochila[i].durabilidad)){ 
				sprintf(cadena, "%s gastada\n", prsj->mochila[i].nombre);
				animacion_por_pantalla(cadena, DELAY);
				/* En la posicion que ha quedado vacia 
				colocamos el ultimo objeto de la mochila para 
				que todos los objetos queden en las primera 
				posiciones. En el caso de que fuese el ultimo 
				objeto de la mochila, ya se habria puesto 
				anteriormente a 0 por lo que no pasa nada, 
				simplemente se reducen el numero de objetos */
				prsj->mochila[i] = prsj->mochila[num_objetos_mochila-1];
					
				prsj->mochila[num_objetos_mochila-1].tipo = 0;
				prsj->mochila[num_objetos_mochila-1].nombre = "";
				prsj->mochila[num_objetos_mochila-1].valor = 0; 
				num_objetos_mochila--;		
			}
			return 1;
			break;
		}
	}
	animacion_por_pantalla("No posees ese objeto.\n", DELAY);
	return 1;
}

int regla_fin_turno() {	
	clearScreen();
	valor_dado = 0;
	lanzado = 0;
	ataque = 0;
	animacion_por_pantalla("Tu turno ha finalizado\n", DELAY);
	/* Se mueve a los enemigos*/
	mover_IA();
	// Se muestran el mapa y la informacion del jugador
	mostrar_mapa(*tablero);
	mostrar_info_jugador(*prsj);
	return 1;	
}

void regla_accion(int digito, int direccion){
	// Esta condicion se hace para que si ya se ha detectado
	// una colision no se vuelva a comprobar, ya que podria
	// darse el caso de que la primera accion diese 
	// colision, por lo tanto la posicion no se 
	// actualizaria, pero despues se comprobaria si la 
	// segunda da colision, y si no da, si se moveria al 
	// personaje. O bien se ejecutan todas las accion, o no
	// se ejectua ninguna
	if (!haycolision) 
		haycolision = colision(digito,direccion);

	if (!haycolision) {
		/* Se actualiza la posicion virtual del personaje y las 
		variables que controlan los pasos en cada direccion */
		switch (direccion){
			case D_ARRIBA:		
				arriba += digito;
				posfilasVirtual -= digito; 
				break;
			case D_ABAJO:					
				abajo += digito;
				posfilasVirtual += digito;
				break;
			case D_DERECHA:
				derecha += digito;
				poscolumnasVirtual += digito;
				break;
			case D_IZQUIERDA:	
				izquierda += digito;	
				poscolumnasVirtual -= digito;	
				break;
		}
	}
}
















/* Funciones auxiliares */

/* Recibe un int para indica que mano comprobar. Devuelve un 1 si la mano esta 
ocupada */
int mano_ocupada(int mano) {
	switch (mano) {
		case MANO_DERECHA:
			if (prsj->mano_derecha != NULL)
				return 1;
			break;
		case MANO_IZQUIERDA:
			if (prsj->mano_izquierda != NULL)
				return 1;
			break;
	}
	return 0;
}

/* Devuelve 1 si la puerta esta cerrada o 0 en casa contrario */
int puerta_cerrada(int fila, int columna) { 
	int i;
	for(i=0;i<NUM_PUERTAS;i++) {
		if (tablero->lista_puertas[i].fila == fila && tablero->lista_puertas[i].columna == columna) {
			return tablero->lista_puertas[i].cerrada;
		}
	}
	return 0;
}

/* Devuelve 1 si abre el cofre y 0 si ya estaba abierto */
int abre_cofre(int fila, int columna) {
	int i;
	// Se recorre la lista de cofres
	for(i = 0; i<NUM_COFRES; i++) {
		if ((tablero->lista_cofres[i].fila == fila) && (tablero->lista_cofres[i].columna == columna)) {
			if (tablero->lista_cofres[i].abierto) {
				animacion_por_pantalla("Cofre ya abierto\n", DELAY);
				return 0;
			} else {
				tablero->lista_cofres[i].abierto = 1;
				return 1;
			}
		}
	}
}

/* Pone la puerta indicada como abierta si existe y devuelve un 1. Si es la 
puerta de entrada del mapa devuelve un 0 porque no se puede abrir */
int abrir_puerta(int fila, int columna) { 
	int i;
	for(i=0;i<NUM_PUERTAS;i++) {
		if (tablero->lista_puertas[i].fila == fila && tablero->lista_puertas[i].columna == columna) {
			if (i==NUM_PUERTAS-2)
				return 0;
			else
				tablero->lista_puertas[i].cerrada = 0;
		}
	}
	return 1;
}

/* Realiza una simulacion de ataque a un enemigo */
void simula_ataque(int pos_fila_enemigo, int pos_columna_enemigo) {
	int ataque = 0, defensa = 0, derecha_roto = 0, izquierda_roto = 0;
	// Lanzamos el dado del jugador
	ataque = get_random_number();
	
	// Lanzamos el dado del enemigo
	defensa = get_random_number();

	// Actualizamos info de jugador y enemigo si es oportuno
	if (ataque>defensa) {
		int danho = (ataque - defensa) * 2;
		// Si hay espadas equipadas el daño se multiplica por 2 por cada espada
		if ((prsj->mano_derecha != NULL) && ((prsj->mano_derecha -> tipo) == OBJ_ESPADA)) {
			danho *= prsj->mano_derecha -> valor;
			prsj->mano_derecha -> durabilidad--;
			if (prsj->mano_derecha -> durabilidad == 0)
				derecha_roto = 1;
		}

		if ((prsj->mano_izquierda != NULL) && ((prsj->mano_izquierda -> tipo) == OBJ_ESPADA)) {
			danho *= prsj->mano_izquierda -> valor;
			prsj->mano_izquierda -> durabilidad--;
			if (prsj->mano_izquierda -> durabilidad == 0)
				izquierda_roto = 1;
		}
		char cadena[100];
		sprintf(cadena, "Has golpeado al enemigo! Recibe %i de daño.\n", danho);
		animacion_por_pantalla(cadena, DELAY);
		actualiza_enemigo(pos_fila_enemigo, pos_columna_enemigo, danho);
	} else {
		animacion_por_pantalla("Has fallado!\n", DELAY);
	}
	// Se comprueba que las espadas no se hayan roto
	if (derecha_roto) {
		char cadena[100];
		sprintf(cadena, "La %s se ha roto\n", prsj->mano_derecha -> nombre);
		animacion_por_pantalla(cadena, DELAY);
		free(prsj->mano_derecha);
		prsj->mano_derecha = NULL;
	}
	
	if (izquierda_roto) {
		char cadena[100];
		sprintf(cadena, "La %s se ha roto\n", prsj->mano_izquierda -> nombre);
		animacion_por_pantalla(cadena, DELAY);
		free(prsj->mano_izquierda);
		prsj->mano_izquierda = NULL;
	}
}

/* Actualiza la vida del enemigo en funcion de los parametros dados */
void actualiza_enemigo(int fila, int columna, int danho) {
	int i;
	for(i = 0; i<NUM_ENEMIGOS; i++) {
		if (tablero->lista_enemigos[i].fila == fila && tablero->lista_enemigos[i].columna == columna) {
			tablero->lista_enemigos[i].vida -= danho;
			if (tablero->lista_enemigos[i].vida <= 0) {
				animacion_por_pantalla("Enemigo eliminado\n", DELAY); // FALLO
				// Para que no lo detecta en actualizaciones posteriores, seria mejor borrarlo de la lista
				tablero->lista_enemigos[i].fila = -1;
				tablero->lista_enemigos[i].columna = -1;
				tablero->mapa[fila][columna] = ' ';
			}
		}
	}
}

/* Realiza una simulacion de ataque de un enemigo actualizando los datos */
void simula_defensa() {
	int ataque = 0, defensa = 0;

	animacion_por_pantalla("\n\nUN ENEMIGO TE ESTÁ ATACANDO", DELAY);
	animacion_por_pantalla("...\n", DELAY*8);
	// Lanzamos el dado del enemigo
	ataque = get_random_number();

	// Lanzamos el dado del jugador
	defensa = get_random_number();

	// Actualizamos info de jugador y enemigo si es oportuno
	if (ataque>defensa) {
		int danho = (ataque - defensa);
		char cadena[100];
		sprintf(cadena, "Recibes %i de daño.\n", danho);
		animacion_por_pantalla(cadena, DELAY);
		prsj->vida -= danho;
	} else {
		animacion_por_pantalla("Has detenido el ataque!\n", DELAY);
	}
	printf("\n");
}

void yyerror (char const *message) {
	if (strcmp(message, "syntax error"))
		printf("%s\n", message);
}

/* Devuelve un numero aleatorio entre 1 y 9 utilizado para simular el 
comportamiento del dado */
int get_random_number(){
	return (rand() % 9) + 1;
}

/* Genera un numero aleatoria que se usa para crear objetos */
int get_random_object(){
	int tipo_objeto = (rand() % (TOTAL_OBJETOS + RATIO_COFRES_VACIOS)) + 1; // Se generan siempre numero entre el 1 y el numero de objetos + un numero que sirve como parametro para que ratio de objetos vacios queremos
	return tipo_objeto;
}

/* Genera un numero entre 0 y 1 que se usa para determinar si una puerta 
estara o no cerrada */
int get_random_door(){
	return (rand()%2);	
}


/* Muestra la ayuda por pantalla */
void mostrar_ayuda(){	
	char msg[] = "\
\nInformación del mapa\n\
	\e[1m\e[38;5;208mP\e[0m: Personaje\n\
	\e[30;48;5;52m_\e[0m: Cofre\n\
	\e[38;5;214mE\e[0m: Enemigo\n\
	\e[48;5;235m \e[0m: Paredes exteriores\n\
	\e[48;5;239m \e[0m: Paredes interiores\n\
\nComandos:\n\
	+ Ver reglas: reglas | mostrar reglas | ver reglas\n\
	+ Ver informacion de la partida: informacion | ver informacion | info\n\
	+ Lanzar dados: lanzar | lanzar dado\n\
	+ Moverse: avanzar [nº pasos] [direccion]\n\
		- \"direccion\" puede ser 'arriba', 'abajo', 'izquierda' o 'derecha'\n\
		- Ejemplo: avanzar 3 arriba 1 izquierda 3 abajo\n\
	+ Usar objeto: usar [nombre_objeto]\n\
		- Ejemplo: usar pocion\n\
	+ Tirar objeto: tirar [nombre_objeto]\n\
		- Ejemplo: tirar llave\n\
	+ Equipar objeto: equipar objeto [mano derecha/mano izquierda] \n\
		- Objetos como pociones o llaves no pueden equiparse \n\
		- Ejemplo: equipar espada mano derecha \n\
	+ Abrir puertas: abrir puerta\n\
	+ Abrir cofres: abrir cofre\n\
	+ Atacar: atacar [direccion]\n\
		- Se indica la posicion del enemigo respecto al protagonista, \
que puede ser 'arriba, 'abajo', 'izquierda' o 'derecha'\n\
		- Ejemplo: atacar abajo\n\
	+ Ayuda: ayuda | help\n\
	+ Finalizar turno: finalizar turno | fin turno | fin\n\
	+ Salir: salir | exit | q";

	printf ("%s", msg);
}

/* Muestra las reglas del juego por pantalla */
void mostrar_reglas(){
	char msg[] = "\
Reglas:\n\
	- Juego basado en turnos\n\
	- El jugador comienza con 100 de vida y una mochila con capacidad \
limitada.\n\
	- Secuencia de turno:\n\
		1º Lanzar dado [Opcional] [Obligatorio si se quiere mover al personaje]\n\
		2º Atacar, abrir cofre, utilizar objeto... [Opcional]\n\
		3º Moverse [Opcional]\n\
		4º Atacar, abrir cofre, utilizar objeto... [Opcional] \n\
		5º Finalizar turno [Obligatorio]\n\
	- Solo se puede atacar una vez por turno.\n\
	- Una vez realizado un ataque, no se pueden hacer movimientos hasta el siguiente turno.";

	printf ("%s", msg);
}

/* Visualiza la informacion del jugador */
void mostrar_info_jugador(tjugador j) {
	animacion_por_pantalla("Información del jugador:\n", DELAY/2);
	printf("\tVida: %i\n", j.vida);
	if (j.mano_derecha != NULL)
		printf("\tMano derecha: %s\n", j.mano_derecha -> nombre);
	else
		printf("\tMano derecha: Vacía\n");
	if (j.mano_izquierda != NULL)
		printf("\tMano izquierda: %s\n", j.mano_izquierda -> nombre);
	else
		printf("\tMano izquierda: Vacía\n");
	printf("\nMochila:\n");
	
	int i;
	for(i=0;i<TAM_MOCHILA;i++){
		printf("\t-----------------------------------\n");
		if (prsj->mochila[i].tipo)
			printf("\t|\t\t%s\t\t  |\n", j.mochila[i].nombre);
		else
			printf("\t|\t\tVACIO\t\t  |\n");
	}
	printf("\t-----------------------------------\n");
	printf("\nMovimientos restantes: ");
	if (!lanzado) {
		printf("Aún no has lanzado el dado\n");
	} else {
		if (valor_dado==1) {
			printf("Te queda %i movimiento", valor_dado);
		} else {
			printf("Te quedan %i movimientos", valor_dado);
		}
	}
	printf("\n");
}

/* Imprime el mapa por pantalla */
void mostrar_mapa(tmapa tablero){
	animacion_por_pantalla("Mapa:\n", DELAY/2);
        int i,j;
	for (i = 0;i<FILAS;i++) {
		printf("\t");
		for (j = 0;j<COLUMNAS;j++) {
			if (tablero.mapa[i][j] == 'P') { // Personaje
				printf("\e[1m\e[38;5;208mP\e[0m"); 
			} else if (tablero.mapa[i][j] == 'E') { // Enemigos
				printf("\e[38;5;214mE\e[0m"); 
			} else if (tablero.mapa[i][j] == 'e'){ // Paredes exteriores
				printf ("\e[48;5;235m \e[0m"); 
			} else if (tablero.mapa[i][j] == 'x'){ // Paredes interiores
				printf ("\e[48;5;239m \e[0m"); 
			} else if (tablero.mapa[i][j] == 'C'){ // Cofres
				int k;
				for(k = 0; k<NUM_COFRES; k++) {
					if ((tablero.lista_cofres[k].fila == i) && (tablero.lista_cofres[k].columna == j)) {
						if (tablero.lista_cofres[k].abierto) { // Cofre abierto
							printf("\e[30;48;5;52m0\e[0m"); 
						} else { // Cofre cerrado
							printf("\e[30;48;5;52m_\e[0m"); 
						}
					}
				}
			} else if (tablero.mapa[i][j] == '|') { // Puertas verticales
				printf("\e[38;5;94m|\e[0m"); 
			} else if (tablero.mapa[i][j] == '-') { // Puertas horizontales
				printf("\e[38;5;94m-\e[0m"); 
			} else { // Resto
				printf("%c", tablero.mapa[i][j]); 
			}		
		}
		printf("\n");
	}
	printf("\n");
}

/* Recibe el numero de pasos y una direccion y devuelve un 1 si se va a 
producir una colision o 0 en caso contrario */
int colision(int pasos, int direccion){
        int i;

	switch(direccion) {
		case 1: // Direccion arriba
			for(i=1;i<=pasos;i++) 
				if (tablero->mapa[posfilasVirtual-i][poscolumnasVirtual] != ' ' && 
				tablero->mapa[posfilasVirtual-i][poscolumnasVirtual] != 'P')
					return 1;
			break;

		case 2:	// Abajo			
			for(i=1;i<=pasos;i++) 
				if (tablero->mapa[posfilasVirtual+i][poscolumnasVirtual] != ' '&& 
				tablero->mapa[posfilasVirtual+i][poscolumnasVirtual] != 'P')
					return 1;
			break;

		case 3: // Derecha
			for(i=1;i<=pasos;i++)
				if (tablero->mapa[posfilasVirtual][poscolumnasVirtual+i] != ' '&& 
				tablero->mapa[posfilasVirtual][poscolumnasVirtual+i] != 'P')
					return 1;
			break;

		case 4:	// Izquierda
			for(i=1;i<=pasos;i++)
				if (tablero->mapa[posfilasVirtual][poscolumnasVirtual-i] != ' '&& 
				tablero->mapa[posfilasVirtual][poscolumnasVirtual-i] != 'P')
					return 1;
			break;
	}

	return 0;
}

/* Incializa los datos del personaje y hace las reservas de memoria oportunas */
void inicializar_jugador(tjugador **jugador) {
	if (*jugador == NULL)
		*jugador = (tjugador *) malloc(sizeof(tjugador));
	(*jugador) -> vida = 100;
	if ((*jugador) -> mochila == NULL)
		(*jugador) -> mochila = (tobjeto *) malloc(TAM_MOCHILA*sizeof(struct objeto));
	int i;
	for(i = 0;i<TAM_MOCHILA;i++){
		(*jugador) -> mochila[i].tipo = 0;
		(*jugador) -> mochila[i].valor = 0;
	}
}

/* Muestra el texto simulando una animacion. Recibe el texto a mostrar y un 
entero que define el tiempo entre caracteres */
void animacion_por_pantalla(char cadena[], int delay) {
	int i;
	// Recorre la cadena
	for(i=0; i<strlen(cadena); i++) {
		// Imprime la letra correspondiente
		printf("%c", cadena[i]);
		// Vuelva el buffer del teclado
		fflush(NULL);
		// Hace una pausa con el tiempo pasado por parametro		
		usleep(delay);
	}
}

/* Limpia la pantalla del terminal para que lo que se visualice comience en la 
parte superior de este */
void clearScreen() {
  const char* CLEAR_SCREE_ANSI = "\e[1;1H\e[2J";
  write(STDOUT_FILENO,CLEAR_SCREE_ANSI,12);
}


/* Calcula la distancia manhattan entre dos puntos */
int calcula_manhattan(int ax, int ay, int bx, int by) {
	return (abs(ax-bx) + abs(ay-by));
}


/* Calcula la orientacion entre un punto A y un punto B. Recibe el parametro x 
e y de cada punto. Devuelve la direccion correspondiente normaliza a 8 
direcciones */
int calcula_orientacion(int ax, int ay, int bx, int by) {
	// Misma fila
	if (ax-bx == 0) {
		// El enemigo esta a la izquierda porque su columna es menor
		if (ay>by) {
			// Moverse hacia la derecha
			return D_DERECHA;
		} else { // Enemigo a la derecha
			
			// Moverse hacia la izquierda
			return D_IZQUIERDA;
		}
	} else if (ay-by == 0) { // Misma columna
		// Enemigo por debajo
		if (ax>bx) { 
			// Moverse hacia abajo
			return D_ABAJO;
		} else { // Enemigo por encima
			// Moverse hacia arriba
			return D_ARRIBA;
		}
	} else {
		// Se calcula la perpendicular a la direccion entre los dos puntos
		double alfa = atan2((by-ay),(bx-ax)); 
		// Conversion a grados
		alfa = alfa * 180/pi;
		// Conversion a valores entre 0 y 360
		if (alfa<0)
			alfa += 360;
		double orientacion = ((int)alfa + 90) % 360;
		
		// Normalizacion
		if (orientacion>337.5 || orientacion<22.5) {
			return D_DERECHA;
		} else if (orientacion>22.5 && orientacion<67.5) {
			return D_ARRIBA_DERECHA;
		} else if (orientacion>67.5 && orientacion<112.5) {
			return D_ARRIBA;
		} else if (orientacion>112.5 && orientacion<157.5){
			return D_ARRIBA_IZQUIERDA;
		} else if (orientacion>157.5 && orientacion<202.5) {
			return D_IZQUIERDA;
		} else if (orientacion>202.5 && orientacion<247.5) {
			return D_ABAJO_IZQUIERDA;
		} else if (orientacion>247.5 && orientacion<292.5) {
			return D_ABAJO;
		} else if (orientacion>292.5 && orientacion<337.5) {
			return D_ABAJO_DERECHA;
		}
	}
}


/* Realiza el movimiento de un enemigo en funcion de la orientacion que se 
calcula. Devuelve un 1 si ha conseguido llegar hasta el protagonista o un 0 
en caso contrario. Ademas actualiza tanto el mapa como la lista de enemigos
mientras lo mueve */
int mover_enemigo(int id_enemigo) {
	int fin = 0;
	
	// Calcula la orientacion del enemigo respecto al protagonista
	int orientacion = calcula_orientacion(prsj->pos_fila, prsj->pos_columna, tablero->lista_enemigos[id_enemigo].fila, tablero->lista_enemigos[id_enemigo].columna);

	while (!fin) {
		switch (orientacion) {
			case D_ARRIBA:
				/* Si en el sentido en el que el enemigo se tiene que mover no hay nada se mueve */
				if (tablero->mapa[tablero->lista_enemigos[id_enemigo].fila-1][tablero->lista_enemigos[id_enemigo].columna] == ' ') {
					tablero->mapa[tablero->lista_enemigos[id_enemigo].fila][tablero->lista_enemigos[id_enemigo].columna] = ' ';
					tablero->lista_enemigos[id_enemigo].fila--;
					tablero->mapa[tablero->lista_enemigos[id_enemigo].fila][tablero->lista_enemigos[id_enemigo].columna] = 'E';
					/* Una vez actualizada la posicion se recalcula la orientacion  */
					orientacion = calcula_orientacion(prsj->pos_fila, prsj->pos_columna, tablero->lista_enemigos[id_enemigo].fila, tablero->lista_enemigos[id_enemigo].columna);
				} else { // Si no se puede mover..
					// Si el enemigo es el que esta al lado devuelve 1 la funcion
					if (tablero->mapa[tablero->lista_enemigos[id_enemigo].fila-1][tablero->lista_enemigos[id_enemigo].columna] == 'P')
						return 1;
					// Si no se indica que hay que finalizar
					fin = 1;
				}		
				break;
			/* Se hace lo mismo en el reto de casos siempre acorde a la direccion que tiene que seguir el enemigo */
			case D_ABAJO:
				if (tablero->mapa[tablero->lista_enemigos[id_enemigo].fila+1][tablero->lista_enemigos[id_enemigo].columna] == ' ') {
					tablero->mapa[tablero->lista_enemigos[id_enemigo].fila][tablero->lista_enemigos[id_enemigo].columna] = ' ';
					tablero->lista_enemigos[id_enemigo].fila++;
					tablero->mapa[tablero->lista_enemigos[id_enemigo].fila][tablero->lista_enemigos[id_enemigo].columna] = 'E';
					orientacion = calcula_orientacion(prsj->pos_fila, prsj->pos_columna, tablero->lista_enemigos[id_enemigo].fila, tablero->lista_enemigos[id_enemigo].columna);
				} else {
					if (tablero->mapa[tablero->lista_enemigos[id_enemigo].fila+1][tablero->lista_enemigos[id_enemigo].columna] == 'P') 
						return 1;
					fin = 1;
				}		
				break;
			case D_DERECHA:
				if (tablero->mapa[tablero->lista_enemigos[id_enemigo].fila][tablero->lista_enemigos[id_enemigo].columna+1] == ' ') {
					tablero->mapa[tablero->lista_enemigos[id_enemigo].fila][tablero->lista_enemigos[id_enemigo].columna] = ' ';		
					tablero->lista_enemigos[id_enemigo].columna++;
					tablero->mapa[tablero->lista_enemigos[id_enemigo].fila][tablero->lista_enemigos[id_enemigo].columna] = 'E';
					orientacion = calcula_orientacion(prsj->pos_fila, prsj->pos_columna, tablero->lista_enemigos[id_enemigo].fila, tablero->lista_enemigos[id_enemigo].columna);
				} else {
					if (tablero->mapa[tablero->lista_enemigos[id_enemigo].fila][tablero->lista_enemigos[id_enemigo].columna+1] == 'P')
						return 1;
					fin = 1;
				}
				break;
			case D_IZQUIERDA:
				if (tablero->mapa[tablero->lista_enemigos[id_enemigo].fila][tablero->lista_enemigos[id_enemigo].columna-1] == ' ') {
					tablero->mapa[tablero->lista_enemigos[id_enemigo].fila][tablero->lista_enemigos[id_enemigo].columna] = ' ';
					tablero->lista_enemigos[id_enemigo].columna--;
					tablero->mapa[tablero->lista_enemigos[id_enemigo].fila][tablero->lista_enemigos[id_enemigo].columna] = 'E';
					orientacion = calcula_orientacion(prsj->pos_fila, prsj->pos_columna, tablero->lista_enemigos[id_enemigo].fila, tablero->lista_enemigos[id_enemigo].columna);
				} else {
					if (tablero->mapa[tablero->lista_enemigos[id_enemigo].fila][tablero->lista_enemigos[id_enemigo].columna-1] == 'P')
						return 1;
					fin = 1;
				}
				break;
			case D_ARRIBA_DERECHA:
				if (tablero->mapa[tablero->lista_enemigos[id_enemigo].fila-1][tablero->lista_enemigos[id_enemigo].columna+1] == ' ') {
					tablero->mapa[tablero->lista_enemigos[id_enemigo].fila][tablero->lista_enemigos[id_enemigo].columna] = ' ';
					tablero->lista_enemigos[id_enemigo].fila--;
					tablero->lista_enemigos[id_enemigo].columna++;
					tablero->mapa[tablero->lista_enemigos[id_enemigo].fila][tablero->lista_enemigos[id_enemigo].columna] = 'E';
					orientacion = calcula_orientacion(prsj->pos_fila, prsj->pos_columna, tablero->lista_enemigos[id_enemigo].fila, tablero->lista_enemigos[id_enemigo].columna);
				} else {
					if (tablero->mapa[tablero->lista_enemigos[id_enemigo].fila-1][tablero->lista_enemigos[id_enemigo].columna+1] == 'P')
						return 1;
					fin = 1;
				}
				break;
			case D_ARRIBA_IZQUIERDA:
				if (tablero->mapa[tablero->lista_enemigos[id_enemigo].fila-1][tablero->lista_enemigos[id_enemigo].columna-1] == ' ') {
					tablero->mapa[tablero->lista_enemigos[id_enemigo].fila][tablero->lista_enemigos[id_enemigo].columna] = ' ';
					tablero->lista_enemigos[id_enemigo].fila--;
					tablero->lista_enemigos[id_enemigo].columna--;
					tablero->mapa[tablero->lista_enemigos[id_enemigo].fila][tablero->lista_enemigos[id_enemigo].columna] = 'E';
					orientacion = calcula_orientacion(prsj->pos_fila, prsj->pos_columna, tablero->lista_enemigos[id_enemigo].fila, tablero->lista_enemigos[id_enemigo].columna);
				} else {
					if (tablero->mapa[tablero->lista_enemigos[id_enemigo].fila-1][tablero->lista_enemigos[id_enemigo].columna-1] == 'P')
						return 1;
					fin = 1;
				}
				break;
			case D_ABAJO_DERECHA:
				if (tablero->mapa[tablero->lista_enemigos[id_enemigo].fila+1][tablero->lista_enemigos[id_enemigo].columna+1] == ' ') {
					tablero->mapa[tablero->lista_enemigos[id_enemigo].fila][tablero->lista_enemigos[id_enemigo].columna] = ' ';
					tablero->lista_enemigos[id_enemigo].fila++;
					tablero->lista_enemigos[id_enemigo].columna++;
					tablero->mapa[tablero->lista_enemigos[id_enemigo].fila][tablero->lista_enemigos[id_enemigo].columna] = 'E';
					orientacion = calcula_orientacion(prsj->pos_fila, prsj->pos_columna, tablero->lista_enemigos[id_enemigo].fila, tablero->lista_enemigos[id_enemigo].columna);
				} else {
					if (tablero->mapa[tablero->lista_enemigos[id_enemigo].fila+1][tablero->lista_enemigos[id_enemigo].columna+1] == 'P')
						return 1;
					fin = 1;
				}
				break;
			case D_ABAJO_IZQUIERDA:
				if (tablero->mapa[tablero->lista_enemigos[id_enemigo].fila+1][tablero->lista_enemigos[id_enemigo].columna-1] == ' ') {
					tablero->mapa[tablero->lista_enemigos[id_enemigo].fila][tablero->lista_enemigos[id_enemigo].columna] = ' ';
					tablero->lista_enemigos[id_enemigo].fila++;
					tablero->lista_enemigos[id_enemigo].columna--;
					tablero->mapa[tablero->lista_enemigos[id_enemigo].fila][tablero->lista_enemigos[id_enemigo].columna] = 'E';
					orientacion = calcula_orientacion(prsj->pos_fila, prsj->pos_columna, tablero->lista_enemigos[id_enemigo].fila, tablero->lista_enemigos[id_enemigo].columna);
				} else {
					if (tablero->mapa[tablero->lista_enemigos[id_enemigo].fila+1][tablero->lista_enemigos[id_enemigo].columna-1] == 'P')
						return 1;
					fin = 1;
				}
				break;
		}
	}
	/* Si ha llegado hasta aqui quiere decir que no ha llegado hasta el 
	protagonista por lo que se devuleve un 0 */
	return 0;
}

/* Se encarga de los movimientos de los enemigos */
void mover_IA() {
	int dist_manhattan = 0, orientacion = 0;
	int i;
	for(i=0;i<NUM_ENEMIGOS;i++) {
		// Siempre y cuando el enemigo este vivo...
		if (tablero->lista_enemigos[i].vida > 0) {
			/* Se llama a la funcion auxiliar que hace el calculo 
			de la distancia manhattan */
			dist_manhattan = calcula_manhattan(prsj->pos_fila, prsj->pos_columna, tablero->lista_enemigos[i].fila, tablero->lista_enemigos[i].columna);
			// Si es 1 el enemigo ataca
			if (dist_manhattan == 1) {
				simula_defensa();
			} else if (dist_manhattan > VISIBILIDAD_ENEMIGO) { // Si es mayor que visibilidad el enemigo no hace nada
				continue;
			} else { // Si esta en el rango de movimiento
				/* Si mover enemigo devuelve 1 quiere decir que
				ha llegado hasta el progonista por lo que se 
				hace la simulacion de defensa */
				if (mover_enemigo(i))
					simula_defensa();
			}
		}
	}
}


/* Carga un archivo de manera aleatoria de la carpeta '/maps' en una variable de tipo tmapa */
void cargar_mapa(tmapa **tab) {
	// Varibles locales
	int fila = 0, columna = 0, enemigos = 0, cofres = 0, puertas = 0;
	int num_mapa = 0;
	char char_aux;
	FILE *file;
	char path[] = "maps/mapx.txt";

	//  Se reserva memoria para la variable si es necesario
	if (*tab == NULL)
		*tab = (tmapa *) malloc(sizeof(struct mapa));
	// Se calcula un numero de mapa de manera aletoria
	num_mapa = rand() % NUM_MAPAS + 1;
	path[8] = num_mapa + '0';
	/* Se abre el fichero en un primer momento para hacer un conteo de las
	 filas, columnas, enemigos, puertas, etc.. */
	file = fopen(path, "r");
	if (file == NULL) {
		printf("El mapa no ha podido ser cargado\n");
		exit(1);
	}
	// Se reinician las variables que llevan el conteo de enemigos, cofres, etc...
	NUM_ENEMIGOS = 0;
	NUM_COFRES = 0;
	NUM_PUERTAS = 0;
	
	// Conteo de filas, columnas, enemigos, etc...
	while (feof(file) == 0) {
		char_aux = fgetc(file);	
		
		switch(char_aux) {
			case '\n': 
				columna = 0;
				fila++;
				break;
			case 'C':
				columna++;
				NUM_COFRES++;
				break;
			case 'E':
				columna++;
				NUM_ENEMIGOS++;
				break;
			case '-':
				columna++;
				NUM_PUERTAS++;
				break;
			case '|':
				columna++;
				NUM_PUERTAS++;
				break;
			case 'f':	
				FILAS = fila+1;
				COLUMNAS = columna;
				break;
			default:
				columna++;
				break;
		}
			
	}

	/* Se vuelve al principio del archivo */
	rewind(file);
	/* Reservas de memoria*/
	(*tab)->mapa = (char **) malloc(FILAS * sizeof(char*));
	int i;
	for(i=0;i<FILAS;i++)
		(*tab)->mapa[i] = (char *) malloc(COLUMNAS*sizeof(char));
	(*tab)->lista_enemigos = (tenemigo *) malloc(NUM_ENEMIGOS*sizeof(struct enemigo));
	/* Como se van a crear una puerta de entrada y una de salida, hay que sumarle al numero de puertas contadas un 2 */
	NUM_PUERTAS += 2; 
	(*tab)->lista_puertas = (tpuerta *) malloc((NUM_PUERTAS)*sizeof(struct puerta)); 
	(*tab)->lista_cofres = (tcofre *) malloc(NUM_COFRES*sizeof(struct cofre));

	// Reinicio de las variables locales
	fila = 0;
	columna = 0;
	cofres = 0;
	puertas= 0;
	enemigos = 0;	

	// Se inicializa el mapa con los dados leidos en el fichero
	while (feof(file) == 0) {
		char_aux = fgetc(file);	
		
		switch(char_aux) {
			case '\n': 
				columna = 0;
				fila++;
				break;
			case 'E':	
				(*tab)->mapa[fila][columna] = char_aux;
				(*tab)->lista_enemigos[enemigos].fila = fila;
				(*tab)->lista_enemigos[enemigos].columna = columna;
				(*tab)->lista_enemigos[enemigos].vida = 20;
				(*tab)->lista_enemigos[enemigos].fuerza = 5;
				enemigos++;
				columna++;
				break;
			case 'C':
				(*tab)->mapa[fila][columna] = char_aux;
				(*tab)->lista_cofres[cofres].fila = fila;
				(*tab)->lista_cofres[cofres].columna = columna;
				(*tab)->lista_cofres[cofres].abierto = 0;
				cofres++;
				columna++;		
				break;
			case '-':
				(*tab)->mapa[fila][columna] = char_aux;
				(*tab)->lista_puertas[puertas].fila = fila;
				(*tab)->lista_puertas[puertas].columna = columna;
				(*tab)->lista_puertas[puertas].cerrada = !get_random_door();
				puertas++;
				columna++;
				break;
			case '|':
				(*tab)->mapa[fila][columna] = char_aux;
				(*tab)->lista_puertas[puertas].fila = fila;
				(*tab)->lista_puertas[puertas].columna = columna;
				(*tab)->lista_puertas[puertas].cerrada = !get_random_door();
				puertas++;
				columna++;
				break;
			case 'f': // Variable utilizada como final de fichero
				fclose(file);
				return;
				break;
			default:
				(*tab)->mapa[fila][columna] = char_aux;
				columna++;
				break;
		}
			
	}
	/* Si se llega a este punto es que falta el fichero esta mal formado, seguramente falte 'f' */
	printf("Error: Archivo de mapa mal formado\n");
	fclose(file);	
	return;
}

/* Crea una puerta en una pared exterior del mapa. Si la puerta se crea en un 
sitio incorrecto (por ejemplo, podría crearse en una pared exterior pero al 
lado de un cofre que imposibilitaria su acceso) devuelve NULL, en caso 
contrario devuelve la puerta. Si se le indica, también posiciona al personaje
 al lado de la puerta creada. */
tpuerta* crear_puerta(tmapa **tablero, int coloca_personaje){
	/* foc (fila o columna) y pIopF (posicion inicial o posicion final)*/
	int foc, pIopF; 
	tpuerta *puerta = (tpuerta *) malloc(sizeof(struct puerta));
	/* Primero se decide si va a ir en los bordes exteriores superiores o inferiores,
	o en los bordes exteriores derecho o izquierdo */
	foc = rand() % 2;
	
	if (foc) { // Si va en los bordes izquierdo o derecho
		// Se decide en que fila	
		puerta->fila = rand() % FILAS;
		// Se decide si en el borde izquierdo o en el borde derecho
		pIopF = rand() % 2;
		// Borde izquierdo 
		if (pIopF) {
			puerta->columna = 0;
		} else{ // Borde derecho 
			puerta->columna = COLUMNAS-1;
		}
		/* Una vez calculada la posicion de la puerta se comprueba que
		es una posicion correcta */
		if (pIopF) { // En este caso se mira a la derecha de la puerta porque la puerta se ha creado en el borde izquierdo
			/* Si al lado de la puerta no hay una casilla vacia se 
			devuelve NULL*/
			if ((*tablero)->mapa[puerta->fila][puerta->columna+1] != ' '){
				free(puerta);
				return NULL;
			} else { // En caso contrario se pinta la puerta en el mapa
				(*tablero)->mapa[puerta->fila][puerta->columna] = '|';
				/* Si ademas hay que colocar el personaje se 
				coloca en la posicion correspondiente */
				if (coloca_personaje){ 
					prsj->pos_fila = puerta->fila;
					prsj->pos_columna = puerta->columna+1;
					/* Se reinician las posiciones 
					virtuales por si el jugador aun tiene 
					pendiente movimientos para ejecutar en
					este turno */
					posfilasVirtual = prsj->pos_fila;
					poscolumnasVirtual = prsj->pos_columna; 

					(*tablero)->mapa[puerta->fila][puerta->columna+1] = 'P';
				}
				return puerta;
			}
		} else { // Se mira si a la izquierda de la puerta la casilla esta vacia porque la puerta se ha creado en el borde derecho
			/* Si al lado de la puerta no hay una casilla vacia se 
			devuelve NULL*/
			if ((*tablero)->mapa[puerta->fila][puerta->columna-1] != ' '){
				free(puerta);
				return NULL;
			} else { // En caso contrario se pinta la puerta en el mapa
				(*tablero)->mapa[puerta->fila][puerta->columna] = '|';
				/* Si ademas hay que colocar el personaje se 
				coloca en la posicion correspondiente */
				if (coloca_personaje){
					prsj->pos_fila = puerta->fila;
					prsj->pos_columna = puerta->columna-1;
					/* Se reinician las posiciones 
					virtuales por si el jugador aun tiene 
					pendiente movimientos para ejecutar en
					este turno */
					posfilasVirtual = prsj->pos_fila;
					poscolumnasVirtual = prsj->pos_columna; 

					(*tablero)->mapa[puerta->fila][puerta->columna-1] = 'P';
				}
				return puerta;
			}
		}
		
	} else { // Si va en el borde superior o inferior
		// Se decide en que columna
		puerta->columna = rand() % COLUMNAS;
		// Se decide si va en el borde superior o en el borde inferior
		pIopF = rand() % 2;
		// Borde superior 
		if (pIopF) {
			puerta->fila = 0;
		} else{ // Borde inferior
			puerta->fila = FILAS-1;
		}
		/* Una vez calculada la posicion de la puerta se comprueba que
		es una posicion correcta */
		if (pIopF) { // Se mira si debajo de la puerta la casilla esta vacia porque la puerta se ha creado en el borde superior
			/* Si al lado de la puerta no hay una casilla vacia se 
			devuelve NULL*/
			if ((*tablero)->mapa[puerta->fila+1][puerta->columna] != ' '){
				free(puerta);
				return NULL;
			} else { // En caso contrario se pinta la puerta en el mapa
				(*tablero)->mapa[puerta->fila][puerta->columna] = '-';
				/* Si ademas hay que colocar el personaje se 
				coloca en la posicion correspondiente */
				if (coloca_personaje){
					prsj->pos_fila = puerta->fila+1;
					prsj->pos_columna = puerta->columna;
					/* Se reinician las posiciones 
					virtuales por si el jugador aun tiene 
					pendiente movimientos para ejecutar en
					este turno */
					posfilasVirtual = prsj->pos_fila;
					poscolumnasVirtual = prsj->pos_columna;
 
					(*tablero)->mapa[puerta->fila+1][puerta->columna] = 'P';
				}
				return puerta;
			}
		} else { // Mirar si encima de la puerta la casilla esta vacia porque la puerta se ha creado en el borde inferior
			/* Si al lado de la puerta no hay una casilla vacia se 
			devuelve NULL*/
			if ((*tablero)->mapa[puerta->fila-1][puerta->columna] != ' '){
				free(puerta);
				return NULL;
			} else { // En caso contrario se pinta la puerta en el mapa
				(*tablero)->mapa[puerta->fila][puerta->columna] = '-';
				/* Si ademas hay que colocar el personaje se 
				coloca en la posicion correspondiente */
				if (coloca_personaje){
					prsj->pos_fila = puerta->fila-1;
					prsj->pos_columna = puerta->columna;
					/* Se reinician las posiciones 
					virtuales por si el jugador aun tiene 
					pendiente movimientos para ejecutar en
					este turno */
					posfilasVirtual = prsj->pos_fila;
					poscolumnasVirtual = prsj->pos_columna;
 
					(*tablero)->mapa[puerta->fila-1][puerta->columna] = 'P';
				}
				return puerta;
			}
		}
	}
}

/* Crea en las paredes exteriores del mapa de manera aleatoria una puerta de 
entrada y otra de salida y las añade a la lista de puertas del tablero junto a 
las ya creadas al cargar el mapa.*/
void crear_entradas(tmapa **tablero) {
	tpuerta *puerta_entrada, *puerta_salida;
	puerta_entrada = crear_puerta(tablero, 1);
	/* Mientras la funcion crear_puerta no consiga crear correctamente 
	la puerta no continua */
	while (puerta_entrada == NULL){
		/* Como es la puerta de entrada se indica que hay que colocar
		 al personaje a su lado */
		puerta_entrada = crear_puerta(tablero, 1);
	}
	/* La puerta se almacena en la penultima posicion de la lista (ya 
	reservada para la puerta de entrada previamente en la funcion 
	cargar_mapa */
	(*tablero)->lista_puertas[NUM_PUERTAS-2] = *puerta_entrada;
	/* La puerta de entrada siempre se inicializa como cerrada y ademas se 
	evitara que pueda ser abierta en la funcion abrir puerta para evitar 
	que el protagonista pueda volver hacia atras*/
	(*tablero)->lista_puertas[NUM_PUERTAS-2].cerrada = 1;
	/* Se libera la puerta auxiliar creada una vez se ha almacenado en la 
	lista */
	free(puerta_entrada);
	puerta_salida = crear_puerta(tablero, 0);
	/* Mientras la funcion crear_puerta no consiga crear correctamente 
	la puerta no continua */
	while (puerta_salida == NULL){
		puerta_salida = crear_puerta(tablero, 0);
	}
	/* La puerta se almacena en la ultima posicion de la lista (ya 
	reservada para la puerta de salida previamente en la funcion 
	cargar_mapa */
	(*tablero)->lista_puertas[NUM_PUERTAS-1] = *puerta_salida;
	/* La puerta de salida siempre se inicializa como abierta para permitir
	 al personaje avanzar al mapa siguiente aunque no tenga una llave */
	(*tablero)->lista_puertas[NUM_PUERTAS-1].cerrada = 0;
	/* Se libera la puerta auxiliar creada una vez se ha almacenado en la 
	lista */
	free(puerta_salida);
	
}

/* Recibe un variable de tipo tmapa y libera la memoria asignada a dicha 
variable */
void liberar_mapa(tmapa **tablero) {
	// Liberar mapa
	int i;
	for(i=0;i<FILAS;i++){
		free((*tablero)->mapa[i]);
	}
	free((*tablero)->mapa);
	// Liberar puertas
	free((*tablero)->lista_puertas);
	// Liberar enemigos
	free((*tablero)->lista_enemigos);
	// Liberar cofres
	free((*tablero)->lista_cofres);
	// Liberar tablero
	free(*tablero);
	*tablero = NULL;
}

/* Funcion principal del programa */
int main(int argc, char *argv[]) {
	// Se limpia el terminal
	clearScreen();
	// Mensajes de bienvenida
	animacion_por_pantalla("\n\nPseudoQuest v. 0.8\n", DELAY);
	animacion_por_pantalla("Bienvenido a PseudoQuest! Aquí empiezan \
tus aventuras, que tengas suerte.\n\n", DELAY);
	
	// Inicializar semilla
	srand(time(NULL)); 
	// Inicializacion de variables
	inicializar_jugador(&prsj);
	// Se carga un mapa aleatorio
	cargar_mapa(&tablero);
	/* Se crean aleatoriamente una puerta de entrada, y otra de salida 
	que posibilidad cambiar de mapa. */
	crear_entradas(&tablero);
	// Se muestra el mapa
	mostrar_mapa(*tablero); 
	// Mensaje de ayuda
	animacion_por_pantalla("\nSi eres nuevo escribe \"ayuda\" en el terminal.\n", DELAY);

	// Comienza el bucle del juego
	printf("\n-> ");
	while (yyparse()) {
		// Si el personaje muere el juego finaliza
		if (prsj->vida<=0) {
			animacion_por_pantalla("\tHas MUERTO. Fin de la partida\n", DELAY);
			break;		
		}
		printf("\n-> ");	
	}
	/* Liberacion de memoria */
	// Liberar mochila
	free(prsj->mochila);
	// Liberar manos
	if (prsj->mano_derecha != NULL )
		free(prsj->mano_derecha);
	if (prsj->mano_izquierda != NULL )
		free(prsj->mano_izquierda);
	// Liberar memoria relacionada con el mapa
	liberar_mapa(&tablero);

	return 0;
}

