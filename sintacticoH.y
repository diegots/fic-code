%{
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>

#define FILAS 13
#define COLUMNAS 20
#define TAM_MOCHILA 20
#define POSICION_INICIAL_FILA 7
#define POSICION_INICIAL_COLUMNA 3

/* Constantes de direccion */
#define D_ARRIBA 1
#define D_ABAJO 2
#define D_DERECHA 3
#define D_IZQUIERDA 4

/* Constantes de colores para el mapa
 * Foreground colors
 *        30    Black
 *        31    Red
 *        32    Green
 *        33    Yellow
 *        34    Blue
 *        35    Magenta
 *        36    Cyan
 *        37    White
 *  
 * Background colors
 *        40    Black
 *        41    Red
 *        42    Green
 *        43    Yellow
 *        44    Blue
 *        45    Magenta
 *        46    Cyan
 *        47    White */

/* Estructuras de datos */
struct jugador {
	int posfilas;
	int poscolumnas;
	int vida;
	char * mochila[TAM_MOCHILA];
};
typedef struct jugador tjugador;

/* Variables globales */
int valor_dado = 0; // Valor actual del dado

// Se utiliza para contralar si el dado ya ha sido lanzado en el turno actual
int lanzado = 0; 

// Variables utilizadas para saber cuanto se mueve el personaje en cada 
// direccion
int arriba, abajo, derecha, izquierda = 0; 

// Posicion virtual del jugador mientras se mueve, si no hay colision, si no hay
// colision se actualiza la poscion del personaje con la poscion virtual, en 
// caso contrario se muestra un error y el personaje no se mueve
int posfilasVirtual,poscolumnasVirtual = 0; 

int haycolision = 0;
tjugador prsj; // Personaje
char mapa[FILAS][COLUMNAS];

/* Cabeceras de funciones */
void yyerror (char const *);
int get_random_number();
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
%token LANZAR AVANZAR ARRIBA ABAJO DER IZQ EXIT AYUDA INFO REGLAS FINALTURNO
%token <entero> DIGITO
%type <entero> movimientos accion direccion
%start S
%%
S : 	LANZAR '\n' {
		if (!lanzado){
			printf("Se lanza el dado\n");

			// Generar numero aleatorio entre 1 y 9
			valor_dado = get_random_number();

			printf("Has sacado un %i.", valor_dado);
			lanzado = 1;

			// Se actualiza la posicion virtual
			posfilasVirtual = prsj.posfilas;
			poscolumnasVirtual = prsj. poscolumnas;

		} else {
			yyerror("Ya has lanzado el dado, mueve al personaje.");		
		}		
		return 1;
	}
	| AVANZAR movimientos {
		if (haycolision) {
			printf("Movimiento erroneo\n");
			posfilasVirtual = prsj.posfilas;
			poscolumnasVirtual = prsj.poscolumnas;
			arriba = abajo = izquierda = derecha = 0;
		}else if (arriba+abajo+izquierda+derecha>valor_dado){
			printf("Movimientos demás!\n");
			
			arriba = abajo = izquierda = derecha = 0;			
		} else{
			valor_dado -= (arriba + abajo + derecha + izquierda);
			prsj.posfilas = posfilasVirtual;
			prsj.poscolumnas = poscolumnasVirtual;
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
	| FINALTURNO '\n' {
		valor_dado = 0;
		lanzado = 0;
		printf("El turno ha finalizado\n");
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
movimientos : 	accion movimientos {	
			//return 1;
		}
	      	| accion '\n' {
			//return 1;		
		}
              	;
accion : 	DIGITO direccion {

                        // Esta condicion se hace para que si ya se ha detectado
			// una colision no se vuelva a comprobar, ya que podria
			// darse el caso de que la primera accion diese 
			// colision, por lo tanto la posicion no se 
			// actualizaria, pero despues se comprobaria si la 
			// segunda da colision, y si no da, si se moveria al 
			// personaje. O bien se ejecutan todas las accion, o no
			// se ejectua ninguna
			if (!haycolision) 
				haycolision = colision($1,$2);

			if (!haycolision){
				switch ($2){
					case D_ARRIBA:		
						arriba += $1;
						posfilasVirtual -= $1; 
						break;
					case D_ABAJO:					
						abajo += $1;
						posfilasVirtual += $1;
						break;
					case D_DERECHA:
						derecha += $1;
						poscolumnasVirtual += $1;
						break;
					case D_IZQUIERDA:	
						izquierda += $1;	
						poscolumnasVirtual -= $1;	
						break;
				}
			}		
		}
		;
direccion : ARRIBA {$$ = D_ARRIBA;}
	| ABAJO	{$$ = D_ABAJO;}
	| DER	{$$ = D_DERECHA;}
	| IZQ	{$$ = D_IZQUIERDA;}

%%
int main(int argc, char *argv[]) {
	
	printf("Heroquest\nv 0.01\n"); 
	printf("Si eres nuevo escribe \"ayuda\" en el terminal\n"); 
	
	// Inicializacion de variables
	prsj.posfilas = POSICION_INICIAL_FILA;
	prsj.poscolumnas = POSICION_INICIAL_COLUMNA;
	prsj.vida = 100;
	
	inicializar_mapa(); // Inicializacion del mapa
	mostrar_mapa(); // Mostrar mapa
	srand(time(NULL)); // Inicializar semilla
	
	printf("\n-> ");
	while (yyparse()){
		printf("\n-> ");	
	}
	return 0;
}

void yyerror (char const *message) {
	printf("%s\n", message);
}

/* Returns a pseudo-random integer between 1 and 9 */
int get_random_number(){
	return (rand() % 9) + 1;
}

void mostrar_ayuda(){	
	char msg[] = "\
Información del mapa\n\
	\e[1m\e[38;5;208mP\e[0m: Personaje\n\
	\e[1m\e[92mB\e[0m: Cofre\n\
	E: Enemigo\n\
	T: Trampa (no visible)\n\
	\e[48;5;235m \e[0m: Paredes exteriores\n\
	\e[48;5;239m \e[0m: Paredes interiores\n\
Comandos:\n\
	Ver reglas: reglas | mostrar reglas | ver reglas\n\
	Ver informacion de la partida: info\n\
	Lanzar dados: lanzar | lanzar dado\n\
	Moverse: avanzar [nº pasos] [direccion]\n\
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
	printf("\tPosicion del jugador: (%i,%i)\n\tVida: %i\n", 
		j.poscolumnas, j.posfilas, j.vida);
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
			if ((i==prsj.posfilas) && (j==prsj.poscolumnas)){
				printf("\e[1m\e[38;5;208mP\e[0m"); // Personaje

			} else if (mapa[i][j] == 'e'){
				printf ("\e[48;5;235m \e[0m"); // Paredes exter

			} else if (mapa[i][j] == 'x'){
				printf ("\e[48;5;239m \e[0m"); // Paredes inter

			} else if (mapa[i][j] == 'C'){
				printf("\e[1m\e[92mB\e[0m"); // Baules
			}			
			else {
				printf("%c", mapa[i][j]); // Puertas, etc
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
				if (mapa[posfilasVirtual-i][poscolumnasVirtual] == 'x') 
					return 1;
			break;

		case 2:	// Abajo			
			for(i=1;i<=pasos;i++) 
				if (mapa[posfilasVirtual+i][poscolumnasVirtual] == 'x') 
					return 1;
			break;

		case 3: // Derecha
			for(i=1;i<=pasos;i++)
				if (mapa[posfilasVirtual][poscolumnasVirtual+i] == 'x')
					return 1;
			break;

		case 4:	// Izquierda
			for(i=1;i<=pasos;i++)
				if (mapa[posfilasVirtual][poscolumnasVirtual-i] == 'x') 
					return 1;
			break;
	}

	return 0;
}

