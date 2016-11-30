%{
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>

#define FILAS 13
#define COLUMNAS 20
#define TAM_MOCHILA 5
#define POSICION_INICIAL_FILA 4
#define POSICION_INICIAL_COLUMNA 15

/* Constantes de direccion */
#define D_ARRIBA 1
#define D_ABAJO 2
#define D_DERECHA 3
#define D_IZQUIERDA 4

/* Constantes de objetos */
#define OBJ_POCION 1 // Constante para identificar el objeto pocion
#define VALOR_POCION 20 // Valor de la pocion
#define OBJ_ESPADA 2 // Constante para identificar el objeto espada
#define VALOR_ESPADA 10 // Daño de la espada
#define TOTAL_OBJETOS 2 // Objetos disponibles en el juego
#define RATIO_COFRES_VACIOS 0 /* Numero utilizado para la mayor o menor 
aparicion de objetos vacios, cuando mayor sea la proporcion de este numero 
con el de TOTAL_OBJETOS, mas cofre vacios apareceran */

/* Estructuras de datos */
struct objeto {
	int tipo;
	char *nombre;
	int valor;
	int durabilidad;
};
typedef struct objeto tobjeto;

struct jugador {
	int pos_fila;
	int pos_columna;
	int vida;
	tobjeto mochila[TAM_MOCHILA];
};
typedef struct jugador tjugador;


/* Variables globales */

// Numero de objetos en la mochila
int num_objetos = 0;

// Valor actual del dado
int valor_dado = 0; 

// Se utiliza para contralar si el dado ya ha sido lanzado en el turno actual
int lanzado = 0; 

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
// Array de caracteres que representa el mapa
char mapa[FILAS][COLUMNAS];

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


%}
%union{
	int entero;
}
%token LANZAR AVANZAR ABRIRPUERTA ABRIRCOFRE USAR ESPADA POCION ARRIBA ABAJO 
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
	| ABRIRPUERTA '\n' {
		return regla_abrir_puerta();
	}
	| ABRIRCOFRE '\n' {
		return regla_abrir_cofre();
	}
	| USAR objeto {
		return regla_usar_objeto($2);
	}
	| FINALTURNO '\n' {
		valor_dado = 0;
		lanzado = 0;
		printf("Su turno ha finalizado\n");
		mostrar_info_jugador(prsj);
		mostrar_mapa();
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
objeto:	ESPADA '\n'{ // En este caso es la regla superior la que hace el return
		$$ = OBJ_ESPADA;
	}
	| POCION '\n'{
		$$ = OBJ_POCION;		
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
	}else if (arriba+abajo+izquierda+derecha>valor_dado){
		printf("Movimientos demás!\n");
		
		arriba = abajo = izquierda = derecha = 0;			
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
	}else if (arriba+abajo+izquierda+derecha>valor_dado){
		printf("Movimientos demás!\n");
		
		arriba = abajo = izquierda = derecha = 0;			
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
	}else if (arriba+abajo+izquierda+derecha>valor_dado){
		printf("Movimientos demás!\n");
		
		arriba = abajo = izquierda = derecha = 0;			
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
	}else if (arriba+abajo+izquierda+derecha>valor_dado){
		printf("Movimientos demás!\n");
		
		arriba = abajo = izquierda = derecha = 0;			
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

int regla_abrir_puerta() {
	/* Cuando las puertas puedan estar cerradas con llave y el jugador se 
encuentre con una mostrar por pantalla : !NOOOO.. PUEDES.. PASAAAR! (Necesitas
 una llave).*/ 
	if (mapa[prsj.pos_fila+1][prsj.pos_columna] == '-') {
		mapa[prsj.pos_fila+1][prsj.pos_columna] = ' ';
		printf("Puerta abierta.\n");
		return 1;
	}
	if (mapa[prsj.pos_fila-1][prsj.pos_columna] == '-') {
		mapa[prsj.pos_fila-1][prsj.pos_columna] = ' ';
		printf("Puerta abierta.\n");
		return 1;
	}
	if (mapa[prsj.pos_fila][prsj.pos_columna+1] == '|') {
		mapa[prsj.pos_fila][prsj.pos_columna+1] = ' ';
		printf("Puerta abierta.\n");		
		return 1;
	}
	if (mapa[prsj.pos_fila][prsj.pos_columna-1] == '|') {
		mapa[prsj.pos_fila][prsj.pos_columna-1] = ' ';
		printf("Puerta abierta.\n");
		return 1;
	}
	printf("No tiene ninguna puerta cerca, y aún no has\
 desarrollado la telequinesis, hable con Yoda o pruebe a acercarse, lo siento.\n");
	return 1;
}

int regla_abrir_cofre() {
	// Se comprueba si existe hueco en la mochila
	if (num_objetos==TAM_MOCHILA) {
			printf("Mochila llena. No puede coger más objetos.\n");
			return 1;
		}
	// Se comprueba si el personaje tiene un cofre al lado
	if (mapa[prsj.pos_fila+1][prsj.pos_columna] == 'C') {		
		mapa[prsj.pos_fila+1][prsj.pos_columna] = ' ';
	}else if (mapa[prsj.pos_fila-1][prsj.pos_columna] == 'C') {		
		mapa[prsj.pos_fila-1][prsj.pos_columna] = ' ';
	}else if (mapa[prsj.pos_fila][prsj.pos_columna+1] == 'C') {		
		mapa[prsj.pos_fila][prsj.pos_columna+1] = ' ';
	}else if (mapa[prsj.pos_fila][prsj.pos_columna-1] == 'C') {		
		mapa[prsj.pos_fila][prsj.pos_columna-1] = ' ';
	} else { // No tiene un cofre cerca
		printf("No tiene ningun cofre cerca, y aún no ha\
 desarrollado la telequinesis, hable con Yoda o pruebe a acercarse, lo siento.\n");
		return 1;
	}

	// Tiene un hueco en la mochila, y el cofre cerca, por lo tanto,
	// se abre el cofre
	printf("Cofre abierto.\n");
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
			printf("Has encontrado una %s!\n", objeto.nombre);
			break;
		case OBJ_ESPADA:
			objeto.tipo = tipo;
			objeto.nombre = "Espada";
			objeto.valor = VALOR_ESPADA;
			objeto.durabilidad = 5;
			printf("Has encontrado una %s!\n", objeto.nombre);
			break;
		default: // Cofre vacio, no se guarda el objeto
			printf("Está vacio. Más suerte la próxima vez.\n");
			return 1;
			break;
	}
	prsj.mochila[num_objetos] = objeto;
	num_objetos++;
	return 1;
}

int regla_usar_objeto(int tipo_objeto) {
	for (int i = 0; i<num_objetos; i++) {
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
			}
			if (!(--prsj.mochila[i].durabilidad)){ //Cada vez que se usa el objeto pierde 1 de durabilidad, si esta llega a 0 el objeto se pierde	
				printf("%s gastada\n", prsj.mochila[i].nombre);	
				prsj.mochila[i].tipo = 0;
				prsj.mochila[i].nombre = "";
				prsj.mochila[i].valor = 0;
				/* En la posicion que ha quedado vacia 
				colocamos el ultimo objeto de la mochila para 
				que todos los objetos queden en las primera 
				posiciones. En el caso de que fuese el ultimo 
				objeto de la mochila, ya se habria puesto 
				anteriormente a 0 por lo que no pasa nada, 
				simplemente se reducen el numero de objetos */
				prsj.mochila[i] = prsj.mochila[num_objetos-1]; 
				num_objetos--;		
			}
			break;
		}
	}
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
	if (num_objetos == 0)
		printf("\t\t- No tienes ningún objetos todavía\n");
	else
		for(int i=0;i<num_objetos;i++){
			switch(j.mochila[i].tipo){
				case OBJ_POCION:
					printf("\t\t- Poción\n");
					break;
				case OBJ_ESPADA:
					printf("\t\t- Espada\n");
					break;
			}
		}
}

void inicializar_mapa(){
	int i,j = 0;

	/* Paredes exteriores */
	for (i=0;i<FILAS;i++){
		for (j=0;j<COLUMNAS;j++){
			if ((i==0) || (i==(FILAS-1)) 
			 || (j==0) || (j==(COLUMNAS-1))) {
				mapa[i][j] = 'e';
			} else {
				mapa[i][j] = ' ';
			}
		}
	}

	/* Paredes Habitaciones */
	mapa[2][2]   = 'x';
	mapa[2][3]   = 'x';
	mapa[2][4]   = 'x';
	mapa[2][5]   = 'x';
	mapa[2][6]   = 'x';
	mapa[2][7]   = 'x';
	mapa[2][14]  = 'x';
	mapa[3][2]   = 'x';
	mapa[3][7]   = 'x';
	mapa[3][14]  = 'x';
	mapa[4][2]   = 'x';
	mapa[4][14]  = 'x';
	mapa[5][2]   = 'x';
	mapa[5][7]   = 'x';
	mapa[5][14]  = 'x';
	mapa[5][17]  = 'x';
	mapa[5][18]  = 'x';
	mapa[6][2]   = 'x';
	mapa[6][3]   = 'x';
	mapa[6][4]   = 'x';
	mapa[6][5]   = 'x';
	mapa[6][6]   = 'x';
	mapa[6][7]   = 'x';
	mapa[6][10]  = 'x';
	mapa[6][11]  = 'x';
	mapa[6][12]  = 'x';
	mapa[6][13]  = 'x';
	mapa[6][14]  = 'x';
	mapa[7][2]   = 'x';
	mapa[7][6]   = 'x';
	mapa[7][14]  = 'x';
	mapa[8][6]   = 'x';
	mapa[8][8]   = 'x';
	mapa[8][9]   = 'x';
	mapa[8][10]  = 'x';
	mapa[8][14]  = 'x';
	mapa[8][15]  = 'x';
	mapa[8][16]  = 'x';
	mapa[8][17]  = 'x';
	mapa[9][2]   = 'x';
	mapa[9][6]   = 'x';
	mapa[9][8]   = 'x';
	mapa[9][14]  = 'x';
	mapa[10][2]  = 'x';
	mapa[10][3]  = 'x';
	mapa[10][4]  = 'x';
	mapa[10][5]  = 'x';
	mapa[10][6]  = 'x';
	mapa[10][8]  = 'x';
	mapa[10][9]  = 'x';
	mapa[10][10] = 'x';
	mapa[10][11] = 'x';
	mapa[10][12] = 'x';
	mapa[10][13] = 'x';
	mapa[10][14] = 'x';
	mapa[11][14] = 'x';

	/* Puertas verticales */
	mapa[4][7]   = '|';
	mapa[8][2]   = '|';
	mapa[14][1]  = '|';
	mapa[7][10]  = '|';

	/* Puertas horizontales */

	/* Baules */
	mapa[2][18]  = 'C';
	mapa[3][18]  = 'C';
	mapa[4][3]   = 'C';
	mapa[8][5]   = 'C';
	mapa[9][9]   = 'C';
	mapa[9][15]  = 'C';
	mapa[10][15] = 'C';
	mapa[11][15] = 'C';
}

void mostrar_mapa(){
	printf("MAPA:\n");
        int i,j;
	for (i = 0;i<FILAS;i++) {
		for (j = 0;j<COLUMNAS;j++) {
			if ((i==prsj.pos_fila) && (j==prsj.pos_columna)){
				printf("\e[1m\e[38;5;208mP\e[0m"); // Personaje

			} else if (mapa[i][j] == 'e'){
				printf ("\e[48;5;235m \e[0m"); // Paredes exter

			} else if (mapa[i][j] == 'x'){
				printf ("\e[48;5;239m \e[0m"); // Paredes inter

			} else if (mapa[i][j] == 'C'){
				printf("\e[1m\e[92mC\e[0m"); // Cofres
			} else if (mapa[i][j] == '|') {
				printf("\e[38;5;94m|\e[0m"); // Puertas verticales
			} else if (mapa[i][j] == '-') {
				printf("\e[38;5;94m-\e[0m"); // Puertas horizontales
			}			
			else {
				printf("%c", mapa[i][j]); // Otros
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
				if (mapa[posfilasVirtual-i][poscolumnasVirtual] != ' ')
					return 1;
			break;

		case 2:	// Abajo			
			for(i=1;i<=pasos;i++) 
				if (mapa[posfilasVirtual+i][poscolumnasVirtual] != ' ')
					return 1;
			break;

		case 3: // Derecha
			for(i=1;i<=pasos;i++)
				if (mapa[posfilasVirtual][poscolumnasVirtual+i] != ' ')
					return 1;
			break;

		case 4:	// Izquierda
			for(i=1;i<=pasos;i++)
				if (mapa[posfilasVirtual][poscolumnasVirtual-i] != ' ')
					return 1;
			break;
	}

	return 0;
}


int main(int argc, char *argv[]) {
	
	printf("PseudoQuest v0.02\n"); 
	printf("Bienvenido a PseudoQuest! Si eres nuevo escribe \"ayuda\" en el terminal.\n"); 
	
	// Inicializacion de variables
	prsj.pos_fila = POSICION_INICIAL_FILA;
	prsj.pos_columna = POSICION_INICIAL_COLUMNA;
	prsj.vida = 100;
	for (int i = 0;i<TAM_MOCHILA;i++){
		prsj.mochila[i].tipo = 0;
		prsj.mochila[i].valor = 0;
	}
	
	inicializar_mapa(); // Inicializacion del mapa
	mostrar_mapa(); // Mostrar mapa
	srand(time(NULL)); // Inicializar semilla
	
	printf("\n-> ");
	while (yyparse()){
		if (prsj.vida<=0) {
			printf("\tHas MUERTO. Fin de la partida\n");
			break;		
		}
		printf("\n-> ");	
	}
	return 0;
}

