typedef char tElement;

struct cell {
   tElement element;
   struct cell *right,*left;
};

typedef struct cell *pcell;
typedef pcell head;

struct turingMachine {
	char **conf;
	head head;
};

typedef struct turingMachine *pmachine;
typedef pmachine machine;

machine create_MT();

void delete_MT(machine *mt, int lines);

void initialize_MT(machine *mt, char **array, char *sequence);

void run_MT(machine *mt, int lines);

void mt_to_file(machine mt, char *file);

