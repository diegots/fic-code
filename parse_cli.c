#include "parse_cli.h"

/* build_command_storage() prepares storage for user input
 * Returns null on error
 */
void * build_command_storage() {
	struct command *c = NULL;

	c = (struct command *) malloc(sizeof(struct command));
	if(c == NULL) return NULL;
	
	c->input = (char *) malloc (sizeof(char) * MAX_ARGUMENT_LENGTH);
	if(c->input == NULL) return NULL;

	return (void *) c;
} /* End build_command_storage() */


void free_command_storage(struct command *c) {
	free(c->input);
	free(c);
}

/* get_cli_argument() gets an argument from stdin. 
 * Returns null on error.
 */
void * get_cli_argument(char * arg) {

	if (fgets(arg, MAX_ARGUMENT_LENGTH, stdin) == NULL) 
		return NULL;

	return arg;
} /* End get_cli_argument() */

/* lookfor_cmd() obtains a index number representating real comd. On 
 * failure return 0.
 */
int lookfor_cmd(char * token) {
	if(!strcmp(token, "exit"))
		return 1;

	if(!strcmp(token, "quit"))
		return 2;

	if(!strcmp(token, "fin"))
		return 3;

	if(!strcmp(token, "autores")) 
		return 4;

	if(!strcmp(token, "getpid"))
		return 5;

	if(!strcmp(token, "cdir"))
		return 6;

	if(!strcmp(token, "list"))
		return 7;
	
	if(!strcmp(token, "delete"))
		return 8;

	if(!strcmp(token, "rdir"))
		return 9;

	return 0;
} /* End lookfor_cmd() */

int parse_cli_argument() {
	struct command *command_line = NULL;	
	int val = 0;

	command_line = (struct command *) build_command_storage();
	if (command_line == NULL)
		return 1;

	command_line->input = (char *) get_cli_argument(command_line->input);
	// get_cli_argument() fails, exit program.
	if (command_line->input == NULL)
		return 1;

	// get_cli_argument() reads nothing
	if (!strcmp(command_line->input, "\n"))
		return 0;

	// Get input tokens
	command_line->number_tokens = 
		TrocearCadena(command_line->input, command_line->tokens);

	// Get an index for builin command
	command_index = lookfor_cmd(command_line->tokens[0]);

	// Run desired command
	val = run_cmd(command_line);

	free_command_storage(command_line);
	return val;
} /* End parse_cli_argument() */

/* run_cmd()
 *
 */
int run_cmd(struct command *c) {
	switch(command_index) {
		case 1: 
		case 2:
		case 3: return 1;
		case 4: author(); break; 	
		case 5: get_pid(c); break;
		case 6: cdir(c); break;
		case 7: list_(c); break;
		case 8: // XXX delete
		case 9: // XXX rdir
		default: printf("%s: command not found\n", 
				c->tokens[COMMAND_PLACE]); break;
	};
	return 0;
} /* End run_cmd() */

int TrocearCadena(char * cadena, char * trozos[]) {
        int i=1;

        if ((trozos[0]=strtok(cadena," \n\t"))==NULL)
                return 0;

        while ((trozos[i]=strtok(NULL," \n\t"))!=NULL)
                i++;

        return i;
}
