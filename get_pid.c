#include "get_pid.h"

/*
 *
 */
void get_pid (struct command *c) {
	int i=0;	
	int parent = 0;
	pid_t pid;

	// XXX Check if "-t" is given more than once.
	while(i<c->number_tokens) {
		if(!strcmp(c->tokens[i],"-p"))	
			parent = 1;
		i++;
	}

	if(parent) {
		pid = getppid();
		printf("newsh's parent uid: %d\n", pid);
	} else {
		pid = getpid();
		printf("newsh uid: %d\n", pid);
	}	
} /* End _getpid */

