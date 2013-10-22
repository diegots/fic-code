#include "cdir.h"

void cdir(struct command *c) {
	switch (c->number_tokens) {
		case 1:
			printf("%s\n", CWD);
			break;
		case 2: 
			if(!(chdir(c->tokens[COMMAND_PLACE+1]))) {
				strcpy(CWD, c->tokens[COMMAND_PLACE+1]);
				break;
			} 
		default: 
			printf("newsh: cdir: %s: No such file or directory\n", 
				c->tokens[COMMAND_PLACE+1]);
			break;
	}
} /* End cdir()*/

