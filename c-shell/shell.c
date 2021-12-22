/* Autor: Diego Trabazo Sardón
 * Horario: Jueves 17:30/19:30
 * Curso: 2013/2014 */

#include "load.h"

int main () {

	if(init()) {
		fprintf(stderr, "init failed\n");
		return -1;
	}
	run();

	return 0;
} /* End main() */

