/* Autor: Diego Trabazo Sardón
 * Horario: Jueves 17:30/19:30
 * Curso: 2013/2014 */

#include <dirent.h>
#include <grp.h>
#include <pwd.h>
#include <stdio.h>
#include <time.h>

#include <sys/stat.h>

#include "fifo_list.h"
#include "load.h"

char * ConvierteModo2 (mode_t m);
struct list_args *get_list_args(struct command *c);
char *gid_to_name( gid_t gid );
int its_parent(char *filename);
int its_me(char *filename);
void list_(struct command *c);
void list_args_set_default(struct list_args *_list_args);
void list_do_ls(struct list_args *args);
void list_do_stat(char *filename, struct list_args *args);
void show_file_info(struct list_args *args, char *filename, struct stat *info_p);
char TipoFichero (mode_t m);
char *uid_to_name(uid_t uid);

struct list_args {
	int list_a;
	int list_r;
	int list_s;
	char path[PATH_LENGTH];
};

list *l;
