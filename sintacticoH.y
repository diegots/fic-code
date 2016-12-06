%{
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include <unistd.h>

/* Constante usada para la animacion de los mensajes por pantalla */
#define DELAY 50000
/* Constantes de direccion */
#define D_ARRIBA 1
#define D_ABAJO 2
#define D_DERECHA 3
#define D_IZQUIERDA 4

/* Constantes de jugador */
#define VIDA_JUDADOR 10
#define TAM_MOCHILA 5
#define POSICION_INICIAL_FILA 10
#define POSICION_INICIAL_COLUMNA 16
#define MANO_DERECHA 1
#define MANO_IZQUIERDA 2

/* Constantes de objetos */
#define OBJ_POCION 1 // Constante para identificar el objeto pocion
#define VALOR_POCION 20 // Valor de la pocion
#define OBJ_ESPADA 2 // Constante para identificar el objeto espada
#define VALOR_ESPADA 3 // Multiplicador de daño de la espada
#define OBJ_LLAVE 3
#define TOTAL_OBJETOS 3 // Objetos disponibles en el juego
#define RATIO_COFRES_VACIOS 0 /* Numero utilizado para la mayor o menor 
aparicion de objetos vacios, cuando mayor sea la proporcion de este numero 
con el de TOTAL_OBJETOS, mas cofre vacios apareceran */

/* Constantes de mapa */
#define FILAS 13
#define COLUMNAS 22
#define NUM_PUERTAS 3
#define NUM_ENEMIGOS 7

/* Estructuras de datos */
struct objeto {
	int tipo;
	char *nombre;
	int valor; // En los objetos de lucha funciona como multiplicador o divisor, en pociones como la cantidad de vida a recuperar
	int durabilidad;
};
typedef struct objeto tobjeto;

struct jugador {
	int pos_fila;
	int pos_columna;
	int vida;
	tobjeto *mano_izquierda;
	tobjeto *mano_derecha;
	tobjeto mochila[TAM_MOCHILA];
};
typedef struct jugador tjugador;

struct puerta {
	int fila;
	int columna;
	int cerrada;
};
typedef struct puerta tpuerta;

struct enemigo {
	int fila;
	int columna;
	int vida;
	int fuerza;
};
typedef struct enemigo tenemigo;

struct mapa {
	char mapa[FILAS][COLUMNAS];
	tpuerta lista_puertas[NUM_PUERTAS];
	tenemigo lista_enemigos[NUM_ENEMIGOS];
};
typedef struct mapa tmapa;

/* Variables globales */

// Mapa
tmapa tablero;

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
tjugador prsj;

/* Cabeceras de funciones */
void yyerror (char const *);
// Funciones principales (llamadas desde el cuerpo de la regla)
// Suelen devolver 1 a no ser que la partida finalice o que sean 
// funciones utilizar en 'subreglas' cuyo caso no devuelven nada
int regla_lanzar();
int regla_arriba();
int regla_abajo();
int regla_der();
int regla_izq();
int regla_avanzar();
int regla_atacar(int);
int regla_equipar(int, int);
int regla_abrir_puerta();
int regla_abrir_cofre();
int regla_usar_objeto(int);
void regla_espada();
void regla_pocion();
void regla_accion(int, int);
// Funciones auxiliares ( usadas por las principales)
int get_random_number();
int get_random_object();
void mostrar_ayuda();
void mostrar_reglas();
void mostrar_info_jugador(tjugador);
void inicializar_mapa();
void mostrar_mapa();
int colision(int, int);
int puerta_cerrada(int, int);
int abrir_puerta(int, int);
void simula_ataque(int, int);
void simula_defensa();
int enemigos_cerca();
void actualiza_enemigo(int, int, int);
void animacion_por_pantalla(char [], int);


%}
%union{
	int entero;
}
%token LANZAR AVANZAR ATACAR EQUIPAR MANOIZQ MANODER ABRIRPUERTA ABRIRCOFRE USAR ESPADA POCION LLAVE ARRIBA ABAJO 
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
		valor_dado = 0;
		lanzado = 0;
		ataque = 0;
		printf("Su turno ha finalizado\n");
		int enemigos = enemigos_cerca();
		for (int i = 0; i<enemigos; i++)
			simula_defensa();
		mostrar_mapa();
		mostrar_info_jugador(prsj);
		return 1;	
	}
	| AYUDA '\n'{
		mostrar_ayuda();
		return 1;
	}
	| INFO '\n' {
		mostrar_info_jugador(prsj);
		mostrar_mapa();
		return 1;
	}
	| REGLAS '\n' {
		mostrar_reglas();
		return 1;
	}
	| EXIT '\n' {
		printf("¡Hasta pronto!\n");
		return 0;
	}
	| error {
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
	if (!lanzado){
		printf("Se lanza el dado\n");

		// Generar numero aleatorio entre 1 y 9
		valor_dado = get_random_number();

		printf("Has sacado un %i.", valor_dado);
		lanzado = 1;

		// Se actualiza la posicion virtual
		posfilasVirtual = prsj.pos_fila;
		poscolumnasVirtual = prsj.pos_columna;

	} else {
		yyerror("Ya has lanzado el dado, mueve al personaje.");		
	}		
	return 1;
}

int regla_arriba(){
	if (!haycolision) 
		haycolision = colision(1, D_ARRIBA);

	if (!haycolision){	
		arriba += 1;
		posfilasVirtual -= 1; 
			
	}
	if (haycolision) {
		printf("Movimiento erroneo\n");
		posfilasVirtual = prsj.pos_fila;
		poscolumnasVirtual = prsj.pos_columna;
		arriba = abajo = izquierda = derecha = 0;
	} else if (arriba+abajo+izquierda+derecha>valor_dado){
		printf("Movimientos demás!\n");
		
		arriba = abajo = izquierda = derecha = 0;			
	} else if (ataque) {
		printf("Una vez hecho un movimiento de ataque, no puedes moverte hasta el turno siguiente\n");
	} else{
		valor_dado -= (arriba + abajo + derecha + izquierda);
		prsj.pos_fila = posfilasVirtual;
		prsj.pos_columna = poscolumnasVirtual;
		if (valor_dado>0) {
			printf("Puedes seguir moviendote si lo deseas.\
			\nTe quedan %i movimientos", valor_dado);
		}			
		printf("\n");
		mostrar_mapa();
		
		arriba = abajo = izquierda = derecha = 0;
	}
	haycolision = 0; 
	return 1;
}

int regla_abajo(){
	if (!haycolision) 
		haycolision = colision(1,D_ABAJO);

	if (!haycolision){					
		abajo += 1;
		posfilasVirtual += 1;
	}
	if (haycolision) {
		printf("Movimiento erroneo\n");
		posfilasVirtual = prsj.pos_fila;
		poscolumnasVirtual = prsj.pos_columna;
		arriba = abajo = izquierda = derecha = 0;
	}else if (arriba+abajo+izquierda+derecha>valor_dado){
		printf("Movimientos demás!\n");
		
		arriba = abajo = izquierda = derecha = 0;			
	} else if (ataque) {
		printf("Una vez hecho un movimiento de ataque, no puedes moverte hasta el turno siguiente\n");
	} else{
		valor_dado -= (arriba + abajo + derecha + izquierda);
		prsj.pos_fila = posfilasVirtual;
		prsj.pos_columna = poscolumnasVirtual;
		if (valor_dado>0) {
			printf("Puedes seguir moviendote si lo deseas.\
			\nTe quedan %i movimientos", valor_dado);
		}			
		printf("\n");
		mostrar_mapa();
		
		arriba = abajo = izquierda = derecha = 0;
	}
	haycolision = 0; 
	return 1;
}

int regla_der() {
	if (!haycolision) 
		haycolision = colision(1,D_DERECHA);

	if (!haycolision){					
		derecha += 1;
		poscolumnasVirtual += 1;
	}
	if (haycolision) {
		printf("Movimiento erroneo\n");
		posfilasVirtual = prsj.pos_fila;
		poscolumnasVirtual = prsj.pos_columna;
		arriba = abajo = izquierda = derecha = 0;
	} else if (arriba+abajo+izquierda+derecha>valor_dado){
		printf("Movimientos demás!\n");
		
		arriba = abajo = izquierda = derecha = 0;			
	} else if (ataque) {
		printf("Una vez hecho un movimiento de ataque, no puedes moverte hasta el turno siguiente\n");
	} else{
		valor_dado -= (arriba + abajo + derecha + izquierda);
		prsj.pos_fila = posfilasVirtual;
		prsj.pos_columna = poscolumnasVirtual;
		if (valor_dado>0) {
			printf("Puedes seguir moviendote si lo deseas.\
			\nTe quedan %i movimientos", valor_dado);
		}			
		printf("\n");
		mostrar_mapa();
		
		arriba = abajo = izquierda = derecha = 0;
	}
	haycolision = 0; 
	return 1;
}

int regla_izq() {
	if (!haycolision) 
		haycolision = colision(1,D_IZQUIERDA);

	if (!haycolision){					
		izquierda += 1;
		poscolumnasVirtual -= 1;
	}
	if (haycolision) {
		printf("Movimiento erroneo\n");
		posfilasVirtual = prsj.pos_fila;
		poscolumnasVirtual = prsj.pos_columna;
		arriba = abajo = izquierda = derecha = 0;
	} else if (arriba+abajo+izquierda+derecha>valor_dado){
		printf("Movimientos demás!\n");
		
		arriba = abajo = izquierda = derecha = 0;			
	} else if (ataque) {
		printf("Una vez hecho un movimiento de ataque, no puedes moverte hasta el turno siguiente\n");
	} else{
		valor_dado -= (arriba + abajo + derecha + izquierda);
		prsj.pos_fila = posfilasVirtual;
		prsj.pos_columna = poscolumnasVirtual;
		if (valor_dado>0) {
			printf("Puedes seguir moviendote si lo deseas.\
			\nTe quedan %i movimientos", valor_dado);
		}			
		printf("\n");
		mostrar_mapa();
		
		arriba = abajo = izquierda = derecha = 0;
	}
	haycolision = 0; 
	return 1;
}

int regla_avanzar() {
	if (haycolision) {
		printf("Movimiento erroneo\n");
		posfilasVirtual = prsj.pos_fila;
		poscolumnasVirtual = prsj.pos_columna;
		arriba = abajo = izquierda = derecha = 0;
	} else if (arriba+abajo+izquierda+derecha>valor_dado){
		printf("Movimientos demás!\n");
		
		arriba = abajo = izquierda = derecha = 0;			
	} else if (ataque) {
		printf("Una vez hecho un movimiento de ataque, no puedes moverte hasta el turno siguiente\n");
	} else{
		valor_dado -= (arriba + abajo + derecha + izquierda);
		prsj.pos_fila = posfilasVirtual;
		prsj.pos_columna = poscolumnasVirtual;
		if (valor_dado>0) {
			printf("Puedes seguir moviendote si lo deseas.\
			\nTe quedan %i movimientos", valor_dado);
		}			
		printf("\n");
		mostrar_mapa();
		
		arriba = abajo = izquierda = derecha = 0;
	}
	haycolision = 0; 
	return 1;
}

int regla_atacar(int direccion) {
	if (!ataque) {
		animacion_por_pantalla("Atacando...\n", DELAY);
		switch (direccion){
			case D_ARRIBA:		
				if (tablero.mapa[prsj.pos_fila-1][prsj.pos_columna] == 'E'){
					simula_ataque(prsj.pos_fila-1, prsj.pos_columna);
					// La variable de ataque se pone a 1 para que no se pueda volver a atacar en el mismo turno, pero solo si realmente se ha atacado, si el usuario ataca y no tiene enemigos cerca esta variable no se activa para que pueda atacar de verdad en este mismo turno
					ataque = 1;
				} else {
					printf("No tienes enemigos cerca\n");
				} 
				break;
			case D_ABAJO:					
				if (tablero.mapa[prsj.pos_fila+1][prsj.pos_columna] == 'E'){
					simula_ataque(prsj.pos_fila+1, prsj.pos_columna);
					ataque = 1;
				} else {
					printf("No tienes enemigos cerca\n");
				}
				break;
			case D_DERECHA:
				if (tablero.mapa[prsj.pos_fila][prsj.pos_columna+1] == 'E'){
					simula_ataque(prsj.pos_fila, prsj.pos_columna+1);
					ataque = 1;
				} else {
					printf("No tienes enemigos cerca\n");
				}
				break;
			case D_IZQUIERDA:
				if (tablero.mapa[prsj.pos_fila][prsj.pos_columna-1] == 'E'){
					simula_ataque(prsj.pos_fila, prsj.pos_columna-1);
					ataque = 1;
				} else {
					printf("No tienes enemigos cerca\n");
				}
				break;
		}
		return 1;
	} else {
		printf("Ya ha atacado este turno.\n");
	}
}

int regla_equipar(int tipo_objeto, int mano) {
	// Si tenemos la mano ocupada mostrarlo y salir
	if (mano_ocupada(mano)) {
		printf("Mano ocupada\n");
		return 1;
	}
	// Si no tenemos el objeto igual
	for (int i = 0; i<num_objetos_mochila; i++) {
		if (tipo_objeto == prsj.mochila[i].tipo){
			switch(tipo_objeto) {
				case OBJ_ESPADA:
					switch(mano) {
						case MANO_DERECHA:
							printf("Equipas tu %s en la mano derecha.\n", prsj.mochila[i].nombre);
							// Equipas la espada
							prsj.mano_derecha = (tobjeto*) malloc(sizeof(struct objeto));
							prsj.mano_derecha -> tipo = prsj.mochila[i].tipo;
							prsj.mano_derecha -> nombre = prsj.mochila[i].nombre;
							prsj.mano_derecha -> durabilidad = prsj.mochila[i].durabilidad;
							prsj.mano_derecha -> valor = prsj.mochila[i].valor;
							break;
						case MANO_IZQUIERDA:
							printf("Equipas tu %s en la mano izquierda.\n", prsj.mochila[i].nombre);
							// Equipas la espada
							prsj.mano_izquierda = (tobjeto*) malloc(sizeof(struct objeto));
							prsj.mano_izquierda -> tipo = prsj.mochila[i].tipo;
							prsj.mano_izquierda -> nombre = prsj.mochila[i].nombre;
							prsj.mano_izquierda -> durabilidad = prsj.mochila[i].durabilidad;
							prsj.mano_izquierda -> valor = prsj.mochila[i].valor;
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
					prsj.mochila[i] = prsj.mochila[num_objetos_mochila-1];
					
					prsj.mochila[num_objetos_mochila-1].tipo = 0;
					prsj.mochila[num_objetos_mochila-1].nombre = "";
					prsj.mochila[num_objetos_mochila-1].valor = 0;
					prsj.mochila[num_objetos_mochila-1].durabilidad = 0; 
					num_objetos_mochila--;


					break;
				case OBJ_POCION:
					printf("No puedes equipar un(a) %s.\n", prsj.mochila[i].nombre);
					break;
				case OBJ_LLAVE:
					printf("No puedes equipar un(a) %s.\n", prsj.mochila[i].nombre);
					break;
			}
		}
	}
	return 1;
}

int mano_ocupada(int mano) {
	switch (mano) {
		case MANO_DERECHA:
			if (prsj.mano_derecha != NULL)
				return 1;
			break;
		case MANO_IZQUIERDA:
			if (prsj.mano_izquierda != NULL)
				return 1;
			break;
	}
	return 0;
}

int regla_abrir_puerta() {
	/* Cuando las puertas puedan estar cerradas con llave y el jugador se 
encuentre con una mostrar por pantalla : !NOOOO.. PUEDES.. PASAAAR! (Necesitas
 una llave).*/ 
	if (tablero.mapa[prsj.pos_fila+1][prsj.pos_columna] == '-') {
		if (puerta_cerrada(prsj.pos_fila+1, prsj.pos_columna)) {
			animacion_por_pantalla("Puerta cerrada.\n", DELAY);
			return 1;
		} else {
			tablero.mapa[prsj.pos_fila+1][prsj.pos_columna] = ' ';
			animacion_por_pantalla("Puerta abierta.\n", DELAY);
			return 1;
		}
	}
	if (tablero.mapa[prsj.pos_fila-1][prsj.pos_columna] == '-') {
		if (puerta_cerrada(prsj.pos_fila-1, prsj.pos_columna)) {
			animacion_por_pantalla("Puerta cerrada.\n", DELAY);
			return 1;
		} else {
			tablero.mapa[prsj.pos_fila-1][prsj.pos_columna] = ' ';
			animacion_por_pantalla("Puerta abierta.\n", DELAY);
			return 1;
		}
	}
	if (tablero.mapa[prsj.pos_fila][prsj.pos_columna+1] == '|') {
		if (puerta_cerrada(prsj.pos_fila, prsj.pos_columna+1)) {
			animacion_por_pantalla("Puerta cerrada.\n", DELAY);
			return 1;
		} else {
			tablero.mapa[prsj.pos_fila][prsj.pos_columna+1] = ' ';
			animacion_por_pantalla("Puerta abierta.\n", DELAY);
			return 1;
		}
	}
	if (tablero.mapa[prsj.pos_fila][prsj.pos_columna-1] == '|') {
		if (puerta_cerrada(prsj.pos_fila, prsj.pos_columna-1)) {
			animacion_por_pantalla("Puerta cerrada.\n", DELAY);
			return 1;
		} else {
			tablero.mapa[prsj.pos_fila][prsj.pos_columna-1] = ' ';
			animacion_por_pantalla("Puerta abierta.\n", DELAY);
			return 1;
		}
	}
	animacion_por_pantalla("No tiene ninguna puerta cerca, y aún no has\
 desarrollado la telequinesis, hable con Yoda o pruebe a acercarse, lo siento.\n", DELAY);
	return 1;
}

int puerta_cerrada(int fila, int columna) { // Mover esta funcion a la parte de funciones auxiliares
	for (int i=0;i<NUM_PUERTAS;i++) {
		if (tablero.lista_puertas[i].fila == fila && tablero.lista_puertas[i].columna == columna) {
			return tablero.lista_puertas[i].cerrada;
		}
	}
	return 0;
}

int regla_abrir_cofre() {
	// Se comprueba si existe hueco en la mochila
	if (num_objetos_mochila==TAM_MOCHILA) {
			animacion_por_pantalla("Mochila llena. No puede coger más objetos.\n", DELAY);
			return 1;
		}
	// Se comprueba si el personaje tiene un cofre al lado
	if (tablero.mapa[prsj.pos_fila+1][prsj.pos_columna] == 'C') {		
		tablero.mapa[prsj.pos_fila+1][prsj.pos_columna] = ' ';
	}else if (tablero.mapa[prsj.pos_fila-1][prsj.pos_columna] == 'C') {		
		tablero.mapa[prsj.pos_fila-1][prsj.pos_columna] = ' ';
	}else if (tablero.mapa[prsj.pos_fila][prsj.pos_columna+1] == 'C') {		
		tablero.mapa[prsj.pos_fila][prsj.pos_columna+1] = ' ';
	}else if (tablero.mapa[prsj.pos_fila][prsj.pos_columna-1] == 'C') {		
		tablero.mapa[prsj.pos_fila][prsj.pos_columna-1] = ' ';
	} else { // No tiene un cofre cerca
		printf("No tiene ningun cofre cerca, y aún no ha\
 desarrollado la telequinesis, hable con Yoda o pruebe a acercarse, lo siento.\n");
		return 1;
	}

	// Tiene un hueco en la mochila, y el cofre cerca, por lo tanto,
	// se abre el cofre
	animacion_por_pantalla("Abriendo cofre", DELAY);
	animacion_por_pantalla("...\n...\n", DELAY*8);
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
	
	// Neceario cuando necesitamos formatear texto
	char objeto_encontrado[100];
	sprintf(objeto_encontrado, "Has encontrado un(a) %s!\n", objeto.nombre);
	animacion_por_pantalla(objeto_encontrado, DELAY);

	prsj.mochila[num_objetos_mochila] = objeto;
	num_objetos_mochila++;
	return 1;
}

int regla_usar_objeto(int tipo_objeto) {
	for (int i = 0; i<num_objetos_mochila; i++) {
		if (tipo_objeto == prsj.mochila[i].tipo){
			switch(tipo_objeto) {
				case OBJ_ESPADA:
					printf("Agarras firmemente tu %s, y la\
comienzas a blandir en todas direcciones como si fueras Conan El Barbaro. Te \
acabas cortando y pierdes %i de vida.\n", prsj.mochila[i].nombre, prsj.mochila[i].valor);
					prsj.vida -= prsj.mochila[i].valor;
					break;
				case OBJ_POCION:
					printf("Bebes la %s y recuperas %i de \
vida.\n", prsj.mochila[i].nombre, prsj.mochila[i].valor);
					prsj.vida += prsj.mochila[i].valor;
					break;
				case OBJ_LLAVE:
					// Se comprueba si el personaje tiene una puerta al lado
					if (tablero.mapa[prsj.pos_fila+1][prsj.pos_columna] == '-') {		
						abrir_puerta(prsj.pos_fila+1, prsj.pos_columna);
					}else if (tablero.mapa[prsj.pos_fila-1][prsj.pos_columna] == '-') {		
						abrir_puerta(prsj.pos_fila-1, prsj.pos_columna);
					}else if (tablero.mapa[prsj.pos_fila][prsj.pos_columna+1] == '|') {		
						abrir_puerta(prsj.pos_fila, prsj.pos_columna+1);
					}else if (tablero.mapa[prsj.pos_fila][prsj.pos_columna-1] == '|') {		
						abrir_puerta(prsj.pos_fila, prsj.pos_columna-1);
					} else { // No tiene una puerta cerca
						printf("No tiene ninguna puerta cerca, y aún no ha\
desarrollado la telequinesis, hable con Yoda o pruebe a acercarse, lo siento.\n");
						return 1;
					}
					animacion_por_pantalla("Has abierto la puerta\n", DELAY);
			}
			if (!(--prsj.mochila[i].durabilidad)){ //Cada vez que se usa el objeto pierde 1 de durabilidad, si esta llega a 0 el objeto se pierde	
				printf("%s gastada\n", prsj.mochila[i].nombre);
				/* En la posicion que ha quedado vacia 
				colocamos el ultimo objeto de la mochila para 
				que todos los objetos queden en las primera 
				posiciones. En el caso de que fuese el ultimo 
				objeto de la mochila, ya se habria puesto 
				anteriormente a 0 por lo que no pasa nada, 
				simplemente se reducen el numero de objetos */
				prsj.mochila[i] = prsj.mochila[num_objetos_mochila-1];
					
				prsj.mochila[num_objetos_mochila-1].tipo = 0;
				prsj.mochila[num_objetos_mochila-1].nombre = "";
				prsj.mochila[num_objetos_mochila-1].valor = 0; 
				num_objetos_mochila--;		
			}
			break;
		}
	}
	return 1;
}

int abrir_puerta(int fila, int columna) { // Mover esta funcion a la parte de funciones auxiliares
	for (int i=0;i<NUM_PUERTAS;i++) {
		if (tablero.lista_puertas[i].fila == fila && tablero.lista_puertas[i].columna == columna) {
			tablero.lista_puertas[i].cerrada = 0;
		}
	}
	return 0;
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

	if (!haycolision){
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

void simula_ataque(int pos_fila_enemigo, int pos_columna_enemigo) {
	int ataque = 0, defensa = 0;
	// Lanzamos el dado del jugador
	ataque = get_random_number();

	// Aplicamos bonus de armas de ataque
	
	// Lanzamos el dado del enemigo
	defensa = get_random_number();

	// Actualizamos info de jugador y enemigo si es oportuno
	if (ataque>defensa) {
		int danho = (ataque - defensa) * 2;
		if ((prsj.mano_derecha != NULL) && ((prsj.mano_derecha -> tipo) == OBJ_ESPADA))
			danho *= prsj.mano_derecha -> valor;
		if ((prsj.mano_izquierda != NULL) && ((prsj.mano_izquierda -> tipo) == OBJ_ESPADA))
			danho *= prsj.mano_izquierda -> valor;
		
		printf("Acierto! El enemigo recibe %i de daño.\n", danho);
		actualiza_enemigo(pos_fila_enemigo, pos_columna_enemigo, danho);
	} else {
		printf("Fallo!\n");
	}
}

void actualiza_enemigo(int fila, int columna, int danho) {
	for (int i = 0; i<NUM_ENEMIGOS; i++) {
		if (tablero.lista_enemigos[i].fila == fila && tablero.lista_enemigos[i].columna == columna) {
			tablero.lista_enemigos[i].vida -= danho;
			if (tablero.lista_enemigos[i].vida <= 0) {
				printf("Enemigo eliminado\n");
				tablero.mapa[fila][columna] = ' ';
			}
		}
	}
}

// Devuelve el numero de enemigos que el jugador tiene cerca
int enemigos_cerca() {
	int enemigos = 0;
	for (int i = 0; i<NUM_ENEMIGOS; i++) {
		if (tablero.lista_enemigos[i].vida>0) { // Si el enemigo esta vivo
			// Si el enemigo esta a una casilla de distancia se cuenta				
			if (((tablero.lista_enemigos[i].fila == prsj.pos_fila) && (abs(tablero.lista_enemigos[i].columna - prsj.pos_columna) == 1 )) || 
			((tablero.lista_enemigos[i].columna == prsj.pos_columna) && (abs(tablero.lista_enemigos[i].fila - prsj.pos_fila) == 1 ))) {
				enemigos++;
			}
		}
	}
	return enemigos;
}

void simula_defensa() {
	int ataque = 0, defensa = 0;

	animacion_por_pantalla("\n\nUN ENEMIGO TE ESTÁ ATACANDO!\n", DELAY);
	// Lanzamos el dado del enemigo
	ataque = get_random_number();

	// Lanzamos el dado del jugador
	defensa = get_random_number();

	// Aplicamos bonus de armas de defensa

	// Actualizamos info de jugador y enemigo si es oportuno
	if (ataque>defensa) {
		int danho = (ataque - defensa);
		printf("Recibes %i de daño.\n", danho);
		prsj.vida -= danho;
	} else {
		printf("Has detenido el ataque!\n");
	}
	printf("\n");
}

void yyerror (char const *message) {
	printf("%s\n", message);
}

/* Returns a pseudo-random integer between 1 and 9 */
int get_random_number(){
	return (rand() % 9) + 1;
}

int get_random_object(){
	int tipo_objeto = (rand() % (TOTAL_OBJETOS + RATIO_COFRES_VACIOS)) + 1; // Se generan siempre numero entre el 1 y el numero de objetos + un numero que sirve como parametro para que ratio de objetos vacios queremos
	return tipo_objeto;
}

void mostrar_ayuda(){	
	char msg[] = "\
Información del mapa\n\
	\e[1m\e[38;5;208mP\e[0m: Personaje\n\
	\e[1m\e[92mC\e[0m: Cofre\n\
	E: Enemigo\n\
	T: Trampa (no visible)\n\
	\e[48;5;235m \e[0m: Paredes exteriores\n\
	\e[48;5;239m \e[0m: Paredes interiores\n\
Comandos:\n\
	Ver reglas: reglas | mostrar reglas | ver reglas\n\
	Ver informacion de la partida: info\n\
	Lanzar dados: lanzar | lanzar dado\n\
	Moverse: avanzar [nº pasos] [direccion]\n\
		direccion puede ser 'arriba', 'abajo', 'izquierda' o 'derecha'\n\
		Ejemplo: avanzar 3 arriba 1 izquierda 3 abajo\n\
	Usar objeto: usar [nombreobjeto]\n\
		Ejemplo: usar pocion\n\
	Abrir puertas: abrir puerta\n\
	Abrir cofres: abrir cofre\n\
	Ayuda: ayuda | help | ayuda [comando] (aun no disponible)\n\
	Salir: salir | exit | q";

	printf ("%s", msg);
}

void mostrar_reglas(){
	char msg[] = "\
Reglas\n\
	- Un dado para moverse con valores del 1 al 9\n\
	- El jugador comienza con 100 de vida y una mochila con capacidad para\
 20 objetos\n\
	- Hay trampas ocultas en el escenario que no se mostrarán hasta que se\
 activen\n\
Secuencia de turno:\n\
	1º Lanzar dado [Opcional] [Obligatorio si se quiere mover al personaje\n\
	2º Atacar, abrir cofre, utilizar objeto... [Opcional] [No disponible]\n\
	3º Moverse [Opcional]\n\
	4º Atacar, abrir cofre, utilizar objeto... [Opcional] [No disponible]\n\
	5º Finalizar turno [Obligatorio]\n";

	printf ("%s", msg);
}

void mostrar_info_jugador(tjugador j) {
	printf("\tVida: %i\n", j.vida);
	printf("\tMochila:\n");
	
	for(int i=0;i<TAM_MOCHILA;i++){
		printf("\t-----------------------------------\n");
		if (prsj.mochila[i].tipo)
			printf("\t|\t\t%s\t\t  |\n", j.mochila[i].nombre);
		else
			printf("\t|\t\tVACIO\t\t  |\n");
	}
	printf("\t-----------------------------------\n");
}

void inicializar_mapa(){
	int i,j = 0;

	/* Paredes exteriores */
	for (i=0;i<FILAS;i++){
		for (j=0;j<COLUMNAS;j++){
			if ((i==0) || (i==(FILAS-1)) 
			 || (j==0) || (j==(COLUMNAS-1))) {
				tablero.mapa[i][j] = 'e';
			} else {
				tablero.mapa[i][j] = ' ';
			}
		}
	}

	/* Paredes Habitaciones */
	tablero.mapa[2][2]   = 'x';
	tablero.mapa[2][3]   = 'x';
	tablero.mapa[2][4]   = 'x';
	tablero.mapa[2][5]   = 'x';
	tablero.mapa[2][6]   = 'x';
	tablero.mapa[2][7]   = 'x';
	tablero.mapa[2][14]  = 'x';
	tablero.mapa[3][2]   = 'x';
	tablero.mapa[3][7]   = 'x';
	tablero.mapa[3][14]  = 'x';
	tablero.mapa[4][2]   = 'x';
	tablero.mapa[4][14]  = 'x';
	tablero.mapa[5][2]   = 'x';
	tablero.mapa[5][7]   = 'x';
	tablero.mapa[5][14]  = 'x';
	tablero.mapa[5][17]  = 'x';
	tablero.mapa[5][18]  = 'x';
	tablero.mapa[5][19]  = 'x';
	tablero.mapa[5][20]  = 'x';
	tablero.mapa[6][2]   = 'x';
	tablero.mapa[6][3]   = 'x';
	tablero.mapa[6][4]   = 'x';
	tablero.mapa[6][5]   = 'x';
	tablero.mapa[6][6]   = 'x';
	tablero.mapa[6][7]   = 'x';
	tablero.mapa[6][10]  = 'x';
	tablero.mapa[6][11]  = 'x';
	tablero.mapa[6][12]  = 'x';
	tablero.mapa[6][13]  = 'x';
	tablero.mapa[6][14]  = 'x';
	tablero.mapa[7][2]   = 'x';
	tablero.mapa[7][6]   = 'x';
	tablero.mapa[7][14]  = 'x';
	tablero.mapa[8][6]   = 'x';
	tablero.mapa[8][8]   = 'x';
	tablero.mapa[8][9]   = 'x';
	tablero.mapa[8][10]  = 'x';
	tablero.mapa[8][14]  = 'x';
	tablero.mapa[8][15]  = 'x';
	tablero.mapa[8][16]  = 'x';
	tablero.mapa[8][17]  = 'x';
	tablero.mapa[9][2]   = 'x';
	tablero.mapa[9][6]   = 'x';
	tablero.mapa[9][8]   = 'x';
	tablero.mapa[9][14]  = 'x';
	tablero.mapa[10][2]  = 'x';
	tablero.mapa[10][3]  = 'x';
	tablero.mapa[10][4]  = 'x';
	tablero.mapa[10][5]  = 'x';
	tablero.mapa[10][6]  = 'x';
	tablero.mapa[10][8]  = 'x';
	tablero.mapa[10][9]  = 'x';
	tablero.mapa[10][10] = 'x';
	tablero.mapa[10][11] = 'x';
	tablero.mapa[10][12] = 'x';
	tablero.mapa[10][13] = 'x';
	tablero.mapa[10][14] = 'x';
	tablero.mapa[11][14] = 'x';

	/* Puertas verticales */ // Em vez de guardarlas directamente en el mapa e imprimir, usar la lista_puertas para imprimir en mostrar_mapa
	tablero.mapa[4][7]   = '|';
	tablero.lista_puertas[0].fila = 4;
	tablero.lista_puertas[0].columna = 7;
	tablero.lista_puertas[0].cerrada = 1; // En un futuro hacerlo aleatoriamente
	tablero.mapa[8][2]   = '|';
	tablero.lista_puertas[1].fila = 8;
	tablero.lista_puertas[1].columna = 2;
	tablero.lista_puertas[1].cerrada = 0;
	tablero.mapa[7][10]  = '|';
	tablero.lista_puertas[2].fila = 7;
	tablero.lista_puertas[2].columna = 10;
	tablero.lista_puertas[2].cerrada = 1;

	/* Puertas horizontales */

	/* Cofres */
	tablero.mapa[2][20]  = 'C';
	tablero.mapa[3][20]  = 'C';
	tablero.mapa[4][3]   = 'C';
	tablero.mapa[8][5]   = 'C';
	tablero.mapa[9][9]   = 'C';
	tablero.mapa[9][15]  = 'C';
	tablero.mapa[10][15] = 'C';
	tablero.mapa[11][15] = 'C';

	/* Enemigos */
	tablero.mapa[2][10] = 'E';
	tablero.lista_enemigos[0].fila = 2;
	tablero.lista_enemigos[0].columna = 10;
	tablero.lista_enemigos[0].vida = 20;
	tablero.lista_enemigos[0].fuerza = 5;
	tablero.mapa[3][12] = 'E';	
	tablero.lista_enemigos[1].fila = 3;
	tablero.lista_enemigos[1].columna = 12;
	tablero.lista_enemigos[1].vida = 20;
	tablero.lista_enemigos[1].fuerza = 5;
	tablero.mapa[4][5] = 'E';
	tablero.lista_enemigos[2].fila = 4;
	tablero.lista_enemigos[2].columna = 5;
	tablero.lista_enemigos[2].vida = 20;
	tablero.lista_enemigos[2].fuerza = 5;
	tablero.mapa[5][11] = 'E';
	tablero.lista_enemigos[3].fila = 5;
	tablero.lista_enemigos[3].columna = 11;
	tablero.lista_enemigos[3].vida = 20;
	tablero.lista_enemigos[3].fuerza = 5;
	tablero.mapa[7][18] = 'E';
	tablero.lista_enemigos[4].fila = 7;
	tablero.lista_enemigos[4].columna = 18;
	tablero.lista_enemigos[4].vida = 20;
	tablero.lista_enemigos[4].fuerza = 5;
	tablero.mapa[8][11] = 'E';
	tablero.lista_enemigos[5].fila = 8;
	tablero.lista_enemigos[5].columna = 11;
	tablero.lista_enemigos[5].vida = 20;
	tablero.lista_enemigos[5].fuerza = 5;
	tablero.mapa[8][19] = 'E';
	tablero.lista_enemigos[6].fila = 8;
	tablero.lista_enemigos[6].columna = 19;
	tablero.lista_enemigos[6].vida = 20;
	tablero.lista_enemigos[6].fuerza = 5;
}

void mostrar_mapa(){
	printf("Tu Mapa:\n");
        int i,j;
	for (i = 0;i<FILAS;i++) {
		printf("\t");
		for (j = 0;j<COLUMNAS;j++) {
			if ((i==prsj.pos_fila) && (j==prsj.pos_columna)){
				printf("\e[1m\e[38;5;208mP\e[0m"); // Personaje
			} else if (tablero.mapa[i][j] == 'e'){
				printf ("\e[48;5;235m \e[0m"); // Paredes exter
			} else if (tablero.mapa[i][j] == 'x'){
				printf ("\e[48;5;239m \e[0m"); // Paredes inter
			} else if (tablero.mapa[i][j] == 'C'){
				printf("\e[1m\e[92mC\e[0m"); // Cofres
			} else if (tablero.mapa[i][j] == '|') {
				printf("\e[38;5;94m|\e[0m"); // Puertas verticales
			} else if (tablero.mapa[i][j] == '-') {
				printf("\e[38;5;94m-\e[0m"); // Puertas horizontales
			} else if (tablero.mapa[i][j] == 'E') {
				printf("\e[38;5;196mE\e[0m"); // Enemigos
			} else {
				printf("%c", tablero.mapa[i][j]); // Otros
			}		
		}
		printf("\n");
	}
}

int colision(int pasos, int direccion){
        int i;

	switch(direccion) {
		case 1: // Direccion arriba
			for(i=1;i<=pasos;i++) 
				if (tablero.mapa[posfilasVirtual-i][poscolumnasVirtual] != ' ')
					return 1;
			break;

		case 2:	// Abajo			
			for(i=1;i<=pasos;i++) 
				if (tablero.mapa[posfilasVirtual+i][poscolumnasVirtual] != ' ')
					return 1;
			break;

		case 3: // Derecha
			for(i=1;i<=pasos;i++)
				if (tablero.mapa[posfilasVirtual][poscolumnasVirtual+i] != ' ')
					return 1;
			break;

		case 4:	// Izquierda
			for(i=1;i<=pasos;i++)
				if (tablero.mapa[posfilasVirtual][poscolumnasVirtual-i] != ' ')
					return 1;
			break;
	}

	return 0;
}

int inicializar_jugador(tjugador jugador) {
	prsj.pos_fila = POSICION_INICIAL_FILA;
	prsj.pos_columna = POSICION_INICIAL_COLUMNA;
	prsj.vida = 100;
	for (int i = 0;i<TAM_MOCHILA;i++){
		prsj.mochila[i].tipo = 0;
		prsj.mochila[i].valor = 0;
	}
}


void animacion_por_pantalla(char cadena[], int delay) {
	for (int i=0; i<strlen(cadena); i++) {
		printf("%c", cadena[i]);
		fflush(NULL);
		usleep(delay);
	}
}

void clearScreen() {
  const char* CLEAR_SCREE_ANSI = "\e[1;1H\e[2J";
  write(STDOUT_FILENO,CLEAR_SCREE_ANSI,12);
}

int main(int argc, char *argv[]) {
	clearScreen();
	animacion_por_pantalla("\n\nPseudoQuest v. 0.1\n", DELAY);
	//printf("PseudoQuest v0.1\n");
	animacion_por_pantalla("Bienvenido a PseudoQuest! Aquí empiezan tus aventuras, que tengas suerte.\n\n", DELAY); 
	/*printf("Bienvenido a PseudoQuest! ");
	sleep(1);
	printf("Aquí empiezan tus aventuras, que tengas suerte.\n");
	sleep(3);*/
	
	// Inicializacion de variables
	
	inicializar_jugador(prsj);
	inicializar_mapa(); // Inicializacion del mapa
	mostrar_mapa(); // Mostrar mapa

	animacion_por_pantalla("\nSi eres nuevo escribe \"ayuda\" en el terminal.\n", DELAY);
	/*sleep(2);	 
	printf("Si eres nuevo escribe \"ayuda\" en el terminal.\n");*/


	srand(time(NULL)); // Inicializar semilla
	
	printf("\n-> ");
	while (yyparse()) {
		if (prsj.vida<=0) {
			animacion_por_pantalla("\tHas MUERTO. Fin de la partida\n", DELAY);
			break;		
		}
		printf("\n-> ");	
	}

	// Liberacion de memoria
	if (prsj.mano_derecha != NULL )
		free(prsj.mano_derecha);
	if (prsj.mano_izquierda != NULL )
		free(prsj.mano_izquierda);

	return 0;
}

