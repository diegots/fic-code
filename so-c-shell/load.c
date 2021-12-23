/* Autor: Diego Trabazo Sardón
 * Horario: Jueves 17:30/19:30
 * Curso: 2013/2014 */

#include "load.h"

int init() {
	// Initialize PATH. 
	CWD = (char *) malloc(sizeof(char)*PATH_LENGTH);
	if (CWD == NULL) {
		perror("malloc");
		return 1;
	}

	if((CWD = (char *) getcwd(CWD, PATH_LENGTH)) == NULL) {
		perror("getcwd");
		return 1;
	}
	return 0;
}

int run() {
	// Loops until exit command
	while(1) {
		show_prompt();
		if(parse_cli_argument()) {
			printf("Exiting newsh. Bye, bye.\n");
			return 0;
		}
	}
}

void show_prompt() {
	printf("newsh $ ");
}
