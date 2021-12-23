/* Autor: Diego Trabazo Sardón
 * Horario: Jueves 17:30/19:30
 * Curso: 2013/2014 */

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

#include "parse_cli.h"

#define PATH_LENGTH 256

char * CWD; // Current working directory

int init();
int run();
void show_prompt();
