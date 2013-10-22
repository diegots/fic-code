#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define COMMAND_PLACE 0
#define MAX_ARGUMENT_LENGTH 512
#define MAX_ARGUMENT_TOKENS 20

/* command stores user inputs. 
 * number_tokens: number of space separated words on input 
 * tokens: array of pointers to each command 
 */
struct command {
        int number_tokens;
        char * tokens [MAX_ARGUMENT_TOKENS];
        char * input;
};

int command_index;

void * build_command_storage();
void free_command_storage(struct command *c);
void * get_cli_argument(char *arg);
int lookfor_cmd(char *token);
int parse_cli_argument();
int run_cmd(struct command *c);
int TrocearCadena(char *cadena, char *trozos[]);
