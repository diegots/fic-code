#include "list.h"

char TipoFichero (mode_t m) {
	switch (m & S_IFMT) { /*and bit a bit con los bits de formato,0170000 */
		case S_IFSOCK: return 's'; /*socket */
		case S_IFLNK: return 'l'; /*symbolic link*/
		case S_IFREG: return '-'; /* fichero normal*/
		case S_IFBLK: return 'b'; /*block device*/
		case S_IFDIR: return 'd'; /*directorio */
		case S_IFCHR: return 'c'; /*char device*/
		case S_IFIFO: return 'p'; /*pipe*/
		default: return '?'; /*desconocido, no deberia aparecer*/
	}
} /* Fin TipoFichero */


char * ConvierteModo2 (mode_t m) {
	static char permisos[12];

	strcpy (permisos,"---------- ");
	permisos[0]=TipoFichero(m);
	if (m & S_IRUSR) permisos[1]='r'; /*propietario*/
	if (m & S_IWUSR) permisos[2]='w';
	if (m & S_IXUSR) permisos[3]='x';
	if (m & S_IRGRP) permisos[4]='r'; /*grupo*/
	if (m & S_IWGRP) permisos[5]='w';
	if (m & S_IXGRP) permisos[6]='x';
	if (m & S_IROTH) permisos[7]='r'; /*resto*/
	if (m & S_IWOTH) permisos[8]='w';
	if (m & S_IXOTH) permisos[9]='x';
	if (m & S_ISUID) permisos[3]='s'; /*setuid, setgid y stickybit*/
	if (m & S_ISGID) permisos[6]='s';
	if (m & S_ISVTX) permisos[9]='t';

	return (permisos);
} /* Fin ConvierteModo2 */


/* 
 *	returns pointer to username associated with uid, uses getpw()
 */	
char *uid_to_name(uid_t uid) {
	struct passwd *pw_ptr;
	static char numstr[10];

	if ((pw_ptr = getpwuid(uid)) == NULL ){
		sprintf(numstr,"%d", uid);
		return numstr;
	}
	else
		return pw_ptr->pw_name ;
}


/*
 *	returns pointer to group number gid. used getgrgid(3)
 */
char *gid_to_name( gid_t gid ) {
	struct group *grp_ptr;
	static  char numstr[10];

	if ( ( grp_ptr = getgrgid(gid) ) == NULL ){
		sprintf(numstr,"%d", gid);
		return numstr;
	}
	else
		return grp_ptr->gr_name;
}


struct list_args *get_list_args(struct command *c) {
	int i = 1;

	struct list_args *_list_args = 
		(struct list_args *) malloc(sizeof(struct list_args));
	if(_list_args == NULL)	
		return NULL;

	list_args_set_default(_list_args);

	while(i<c->number_tokens) {
		if(!strcmp(c->tokens[i], "-a")) 
			(*_list_args).list_a = 1;
		
		if(!strcmp(c->tokens[i], "-s")) 
			(*_list_args).list_s = 1;
		
		if(!strcmp(c->tokens[i], "-r")) 
			(*_list_args).list_r = 1;
		
		i++;
	}

	//(*_list_args).path[0]='.'; // XXX 
	strcpy((*_list_args).path, CWD);
	if(c->number_tokens>1) {
		i = 1; // Must start in 1. 0 is list command
		while(i<c->number_tokens) {
			if(c->tokens[i][0] != '-') {	
				strcpy((*_list_args).path, c->tokens[i]);
				break;
			}
			i++;
		}
	}

	return _list_args;
}

void list_args_set_default(struct list_args *_list_args) {
	(*_list_args).list_a = 0;
	(*_list_args).list_r = 0;
	(*_list_args).list_s = 0;
	(*_list_args).path[0] = '\0';
}

void list_(struct command *c) {
	struct list_dir_node *aux = NULL;
	struct list_args *list_args_ptr = NULL;

	l = create_list(l);
	if(l == NULL) {
		fprintf(stderr, "create_list error\n");
		exit(-1);
	}

	list_args_ptr = get_list_args(c);
	if(list_args_ptr == NULL) {
		fprintf(stderr, "get_list_args error\n");
		exit(-1);
	}

	list_do_ls(list_args_ptr);
	
	while(l->first != NULL) {
		list_args_ptr = (struct list_args *) get_element(l);
		list_do_ls(list_args_ptr);
		l = next_element(l);
	}

	delete_list(l);
	chdir(CWD);
}


void list_do_ls(struct list_args *args) {
	DIR *dir = NULL;
	struct dirent *dirent_ptr= NULL;

	if((dir = opendir((*args).path))==NULL)
		perror("opendir");
	else {
		while ((dirent_ptr = readdir(dir)) != NULL) {
			list_do_stat((*dirent_ptr).d_name, args);
		}
		closedir(dir);
	}
}


void list_do_stat(char *filename, struct list_args *args) {
	struct stat *info_p = NULL;
	info_p = (struct stat*) malloc(sizeof(struct stat));
	if(info_p == NULL)
		exit(-1);

	chdir((*args).path); // XXX
	if (stat(filename, info_p) == -1)		/* cannot stat */
		perror(filename);			/* say why */
	else						/* else show info  */
		show_file_info(args, filename, info_p);

	free(info_p);
}

int its_parent(char *filename) {
	return !strncmp(filename, "..", 2);
}

int its_me(char *filename) {
	return !strncmp(filename, ".", 1);
}

int its_hidden(char *filename) {
	return (!its_me(filename) && !its_parent(filename) && 
		(strlen(filename)>1));
}

/*
 * display the info about 'filename'.  The info is stored in struct at *info_p
 */
void show_file_info(struct list_args *args, char *filename, struct stat *info_p) {
	struct node *n = NULL;
	struct list_args *la = 
		(struct list_args *) malloc (sizeof(struct list_args));

	if(la == NULL) {
		fprintf(stderr, "malloc error\n");
		exit(-1);
	}
	
	char *file_modes_str = ConvierteModo2((*info_p).st_mode);

	if(((*args).list_r) && (file_modes_str[0] == 'd')) {
		if(!its_parent(filename) && !its_me(filename)) {
			memcpy(la, args, sizeof(struct list_args));
			strcat(la->path, "/");
			strcat(la->path, filename);
			n = create_node(la, sizeof(struct list_args));
			if(n == NULL) {
				fprintf(stderr, "create_node");
				exit(-1);
			}
			l = insert_node(l, n);
		}
	}

	if((!(*args).list_s) && (!(*args).list_a) && filename[0] != '.') { // ls -li
		printf("%d  "  , (int) (*info_p).st_ino); // inode
		printf("%s"    , file_modes_str); // modes
		printf("%4d "  , (int) (*info_p).st_nlink); // hardlinks
		printf("%-8s " , uid_to_name((*info_p).st_uid) ); // owner
		printf("%-8s " , gid_to_name((*info_p).st_gid) ); // group
		printf("%8ld " , (long) (*info_p).st_size); // size
		printf("%.12s ", 4+ctime(&info_p->st_mtime)); // ctime
		printf("%s\n"  , filename ); // filename
	}

	if((!(*args).list_s) && ((*args).list_a)) { // ls -lia
		printf("%d  "  , (int) (*info_p).st_ino); // inode
		printf("%s"    , file_modes_str); // modes
		printf("%4d "  , (int) (*info_p).st_nlink); // hardlinks
		printf("%-8s " , uid_to_name((*info_p).st_uid) ); // owner
		printf("%-8s " , gid_to_name((*info_p).st_gid) ); // group
		printf("%8ld " , (long) (*info_p).st_size); // size
		printf("%.12s ", 4+ctime(&info_p->st_mtime)); // ctime
		printf("%s\n"  , filename ); // filename
	}

	if((*args).list_s && (!(*args).list_a) && filename[0] != '.') { // ls -s
		printf("%s "  , filename); // filename
	}

	if((*args).list_s && (*args).list_a) {// ls -sa
		printf("%s "  , filename); // filename
	}
}
