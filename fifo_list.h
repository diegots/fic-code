/* Autor: Diego Trabazo Sardón
 * Horario: Jueves 17:30/19:30
 * Curso: 2013/2014 */

#include <stddef.h>

struct node {
	void *element;
	struct node *next;
};

struct list_control {
	struct node *first;
	struct node *last;
};

typedef struct list_control list;

/* Functions */
list *create_list(list *l);
struct node *create_node(void *element, size_t s);
void delete_list(list *l);
void delete_node(struct node *n);
void *get_element(list *l);
struct node *get_node(list *l);
list *insert_node(list *l, struct node *n);
list *next_element(list *l);
