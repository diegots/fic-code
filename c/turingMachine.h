typedef char tElemento;

struct celda {
   tElemento elemento;
   struct celda *derecha,*izquierda;
};

typedef struct celda *pcelda;
typedef pcelda cabeza;

struct turingMachine {
	char **conf;
	cabeza cabeza;
};

typedef struct turingMachine *pmachine;
typedef pmachine machine;

machine crear_MT();

void eliminar_MT(machine *mt);

void inicializar_MT(machine *mt, char **array, char *secuencia);

void ejecutar(machine *mt, int lineas);

void escribir_MT(machine mt, char *fichero);

