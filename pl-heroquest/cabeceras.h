#define pi 3.141592
/* Constante usada para la animacion de los mensajes por pantalla */
#define DELAY 50000
//#define DELAY 1
/* Constantes de direccion */
#define D_ARRIBA 1
#define D_ABAJO 2
#define D_DERECHA 3
#define D_IZQUIERDA 4
#define D_ARRIBA_DERECHA 5
#define D_ABAJO_IZQUIERDA 6
#define D_ABAJO_DERECHA 7
#define D_ARRIBA_IZQUIERDA 8

/* Constantes de jugador */
#define VIDA_JUDADOR 10
#define TAM_MOCHILA 6
#define POSICION_INICIAL_FILA 10
#define POSICION_INICIAL_COLUMNA 16
#define MANO_DERECHA 1
#define MANO_IZQUIERDA 2

/* Constantes de objetos */
#define OBJ_POCION 1 // Constante para identificar el objeto pocion
#define VALOR_POCION 20 // Valor de la pocion
#define OBJ_ESPADA 2 // Constante para identificar el objeto espada
#define VALOR_ESPADA 2 // Multiplicador de daño de la espada
#define OBJ_LLAVE 3
#define TOTAL_OBJETOS 3 // Objetos disponibles en el juego
#define RATIO_COFRES_VACIOS 1 /* Numero utilizado para la mayor o menor 
aparicion de objetos vacios, cuando mayor sea la proporcion de este numero 
con el de TOTAL_OBJETOS, mas cofre vacios apareceran */

/* Constantes de mapa */
//#define FILAS 13
//#define COLUMNAS 22
//#define NUM_PUERTAS 3
//#define NUM_ENEMIGOS 7
//#define NUM_COFRES 8
#define VISIBILIDAD_ENEMIGO 7
#define NUM_MAPAS 7

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
	tobjeto *mochila;
};
typedef struct jugador tjugador;

struct puerta {
	int fila;
	int columna;
	int cerrada;
};
typedef struct puerta tpuerta;

struct cofre {
	int fila;
	int columna;
	int abierto;
};
typedef struct cofre tcofre;

struct enemigo {
	int fila;
	int columna;
	int vida;
	int fuerza;
};
typedef struct enemigo tenemigo;

struct mapa {
	char **mapa;
	tpuerta *lista_puertas;
	tcofre *lista_cofres;
	tenemigo *lista_enemigos;
};
typedef struct mapa tmapa;

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
int regla_fin_turno();
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
void mostrar_mapa(tmapa);
int colision(int, int);
int puerta_cerrada(int, int);
int abrir_puerta(int, int);
void simula_ataque(int, int);
void simula_defensa();
int enemigos_cerca();
void actualiza_enemigo(int, int, int);
void animacion_por_pantalla(char [], int);
int mano_ocupada(int);
void mover_IA();
void cargar_mapa(tmapa**);
int get_random_door();
void crear_entradas(tmapa **);
void liberar_mapa(tmapa **);
void clearScreen();
int abre_cofre(int, int);
int yylex();
