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
// Estructuras de datos
struct jugador {
	int posfilas;
	int poscolumnas;
	int vida;
	char * mochila[TAM_MOCHILA];
};
typedef struct jugador tjugador;
// Variables globales
int valor_dado = 0; // Valor actual del dado
int lanzado = 0; // Se utiliza para contralar si el dado ya ha sido lanzado en el turno actual
int arriba, abajo, derecha, izquierda = 0; // Variables utilizadas para saber cuanto se mueve el personaje en cada direccion
int posfilasVirtual,poscolumnasVirtual = 0; // Posicion virtual del jugador mientras se mueve, si no hay colision, si no hay colision se actualiza la poscion del personaje con la poscion virtual, en caso contrario se muestra un error y el personaje no se mueve
int haycolision = 0;
tjugador pepe; // Personaje
char mapa[FILAS][COLUMNAS];

// Cabeceras de funciones
void yyerror (char const *);
int get_random_number();
void mostrar_ayuda();
void mostrar_info_jugador(tjugador);
void inicializar_mapa();
void mostrar_mapa();
%}
%union{
	int entero;
}
%token LANZAR AVANZAR ARRIBA ABAJO DER IZQ EXIT AYUDA INFO FINALTURNO
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
			posfilasVirtual = pepe.posfilas;
			poscolumnasVirtual = pepe. poscolumnas;
		} else {
			yyerror("Ya has lanzado el dado, mueve al personaje.");		
		}		
		return 1;
	}
	| AVANZAR movimientos {
		if (haycolision) {
			printf("Movimiento erroneo\n");
			posfilasVirtual = pepe.posfilas;
			poscolumnasVirtual = pepe.poscolumnas;
			arriba = abajo = izquierda = derecha = 0;
		}else if (arriba+abajo+izquierda+derecha>valor_dado){
			printf("Movimientos demás!\n");
			
			arriba = abajo = izquierda = derecha = 0;			
		} else{
			valor_dado -= (arriba + abajo + derecha + izquierda);
			pepe.posfilas = posfilasVirtual;
			pepe.poscolumnas = poscolumnasVirtual;
			if (valor_dado>0) {
				printf("Puedes seguir moviendote si lo deseas.\nTe quedan %i movimientos", valor_dado);			
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
		return 1;	
	}
	| AYUDA '\n'{
		mostrar_ayuda();
		return 1;
	}
	| INFO '\n' {
		mostrar_info_jugador(pepe);
		mostrar_mapa();
		return 1;
	}
	| EXIT '\n' {
		printf("¡Hasta pronto!\n");
		return 0;
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
			if (!haycolision) // Esta condicion se hace para que si ya se ha detectado una colision no se vuelva a comprobar, ya que podria darse el caso de que la primera accion diese colision, por lo tanto la posicion no se actualizaria, pero despues se comprobaria si la segunda da colision, y si no da, si se moveria al personaje. O bien se ejecutan todas las accion, o no se ejectua ninguna
				haycolision = colision($1,$2);
			if (!haycolision){
				switch ($2){
					case 1:		
						arriba += $1;
						posfilasVirtual -= $1; 
						break;
					case 2:					
						abajo += $1;
						posfilasVirtual += $1;
						break;
					case 3:
						derecha += $1;
						poscolumnasVirtual += $1;
						break;
					case 4:	
						izquierda += $1;	
						poscolumnasVirtual -= $1;	
						break;
				}
			}		
		}
		;
direccion : ARRIBA {$$ = 1;}
	| ABAJO	{$$ = 2;}
	| DER	{$$ = 3;}
	| IZQ	{$$ = 4;}

%%
int main(int argc, char *argv[]) {
	// Información de inicio
	printf("Heroquest\nv 0.01\n");
	// Inicializacion de variables
	pepe.posfilas = POSICION_INICIAL_FILA;
	pepe.poscolumnas = POSICION_INICIAL_COLUMNA;
	pepe.vida = 100;
	// Inicializacion del mapa
	inicializar_mapa();
	// Mostrar mapa
	mostrar_mapa();
	// Inicializar semilla	
	srand(time(NULL));
	
	printf("\n-> ");
	while (yyparse()){
		printf("\n-> ");	
	}
	return 0;
}

void yyerror (char const *message) {
	printf("%s\n", message);
}

int get_random_number(){
	int r = (rand() % 9) + 1;    //returns a pseudo-random integer between 1 and 9
}

void mostrar_ayuda(){	
		printf("\nComandos:\n\tComenzar movimiento: avanzar | mover | lanzar | lanzar dado\n\tMoverse: avanzar [nº pasos] [direccion]\n\tAyuda: ayuda | help | ayuda [comando]\n\tSalir: salir | exit");
}

void mostrar_info_jugador(tjugador j) {
	printf("\tPosicion del jugdaor: (%i,%i)\n\tVida: %i\n", j.poscolumnas, j.posfilas, j.vida);
}

void inicializar_mapa(){
	int i,j = 0;
	// Paredes exteriores
	for (i=0;i<FILAS;i++){
		for (j=0;j<COLUMNAS;j++){
			if ((i==0) || (i==(FILAS-1)) || (j==0) || (j==(COLUMNAS-1))) {
				mapa[i][j] = '.';
			} else {
				mapa[i][j] = ' ';
			}
		}
	}
	// Paredes Habitaciones
	mapa[2][2] = '.';
	mapa[2][3] = '.';
	mapa[2][4] = '.';
	mapa[2][5] = '.';
	mapa[2][6] = '.';
	mapa[2][7] = '.';
	mapa[2][14] = '.';
	mapa[3][2] = '.';
	mapa[3][7] = '.';
	mapa[3][14] = '.';
	mapa[4][2] = '.';
	mapa[4][14] = '.';
	mapa[5][2] = '.';
	mapa[5][7] = '.';
	mapa[5][14] = '.';
	mapa[5][17] = '.';
	mapa[5][18] = '.';
	mapa[5][19] = '.';
	mapa[5][20] = '.';
	mapa[6][2] = '.';
	mapa[6][3] = '.';
	mapa[6][4] = '.';
	mapa[6][5] = '.';
	mapa[6][6] = '.';
	mapa[6][7] = '.';
	mapa[6][10] = '.';
	mapa[6][11] = '.';
	mapa[6][12] = '.';
	mapa[6][13] = '.';
	mapa[6][14] = '.';
	mapa[7][2] = '.';
	mapa[7][6] = '.';
	mapa[7][14] = '.';
	mapa[8][6] = '.';
	mapa[8][8] = '.';
	mapa[8][9] = '.';
	mapa[8][10] = '.';
	mapa[8][14] = '.';
	mapa[8][15] = '.';
	mapa[8][16] = '.';
	mapa[8][17] = '.';
	mapa[9][2] = '.';
	mapa[9][6] = '.';
	mapa[9][8] = '.';
	mapa[9][14] = '.';
	mapa[10][2] = '.';
	mapa[10][3] = '.';
	mapa[10][4] = '.';
	mapa[10][5] = '.';
	mapa[10][6] = '.';
	mapa[10][8] = '.';
	mapa[10][9] = '.';
	mapa[10][10] = '.';
	mapa[10][11] = '.';
	mapa[10][12] = '.';
	mapa[10][13] = '.';
	mapa[10][14] = '.';
	mapa[11][14] = '.';
	// Puertas verticales
	mapa[4][7] = '|';
	mapa[8][2] = '|';
	mapa[14][1] = '|';
	mapa[7][10] = '|';
	// Puertas horizontales
	// Baules
	mapa[2][20] = 'B';
	mapa[3][20] = 'B';
	mapa[4][3] = 'B';
	mapa[8][5] = 'B';
	mapa[9][9] = 'B';
	mapa[9][15] = 'B';
	mapa[10][15] = 'B';
	mapa[11][15] = 'B';
	// 
}

void mostrar_mapa(){
	printf("MAPA:\n");
	for (int i = 0;i<FILAS;i++) {
		for (int j = 0;j<COLUMNAS;j++) {
			if ((i==pepe.posfilas) && (j==pepe.poscolumnas))
				printf("P");
			else
				printf("%c",mapa[i][j]);
		}
		printf("\n");
	}
	
}

int colision(int pasos, int direccion){
	switch(direccion) {
		case 1: // Direccion arriba
			for(int i=1;i<=pasos;i++) {
				if (mapa[posfilasVirtual-i][poscolumnasVirtual] == '.') {
					return 1;
				}
			}	 
			break;
		case 2:	// Abajo			
			for(int i=1;i<=pasos;i++) {
				if (mapa[posfilasVirtual+i][poscolumnasVirtual] == '.') {
					return 1;
				}
			}	
			break;
		case 3: // Derecha
			for(int i=1;i<=pasos;i++) {
				if (mapa[posfilasVirtual][poscolumnasVirtual+i] == '.') {
					return 1;
				}
			}	
			break;
		case 4:	// Izquierda
			for(int i=1;i<=pasos;i++) {
				if (mapa[posfilasVirtual][poscolumnasVirtual-i] == '.') {
					return 1;
				}
			}		
			break;
	}
	return 0;
}

