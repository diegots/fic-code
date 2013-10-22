#include "load.h"

int main () {

	if(init()) {
		fprintf(stderr, "init failed\n");
		return -1;
	}
	run();

	return 0;
} /* End main() */

