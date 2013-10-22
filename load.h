#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

#include "parse_cli.h"

#define PATH_LENGTH 256

char * CWD; // Current working directory

int init();
int run();
void show_prompt();
