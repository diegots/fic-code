/* Autor: Diego Trabazo Sardón
 * Horario: Jueves 17:30/19:30
 * Curso: 2013/2014 */

#include <stddef.h>
#include <stdlib.h>
#include <string.h>
#include "fifo_list.h"

/* Creates an empty list and returns a pointer to it. */
list *create_list(list *l) {
	list *aux = NULL;

	aux = (list *) malloc(sizeof(list));;
	if(aux == NULL) return NULL;

	aux->first = NULL;
	aux->last = NULL;

	return (l = aux);
}

/* Creates one new list node.  */
struct node *create_node(void *element, size_t s) {
	struct node *n = NULL;

	n = (struct node *) malloc (sizeof(struct node));
	if(n == NULL) return NULL;

	n->element = malloc(s);
	if(n->element == NULL) return NULL;

	memcpy(n->element, element, s);
	n->next = NULL;
	return n;
}

/* Delete given l list */
void delete_list(list *l) {
	while((l->first) != NULL)
		l = next_element(l);
	free(l);
}

/* Delete given node */
void delete_node(struct node *n) {
	free(n->element);
	free(n);
}

/* Get current stored element at the tail of the list */
void *get_element(list *l) {
	struct node *n = get_node(l);

	if(n != NULL)
		return (void *) n->element;
	return NULL;
}

/* Get current node at the tail of the list */
struct node *get_node(list *l) {
	if((l->first) != NULL)
		return l->first;
	return NULL;
}
 
/* Inserts a new node at the head of the list */
list *insert_node(list *l, struct node *n) {
	
	if(l->first == NULL && l->last == NULL) {
		l->first = n;
		l->last = n;
	} else {
		l->last->next = n;
		l->last = l->last->next ;
	}

	return l;
}

/* Deletes current node and points to the next. Returns NULL on an empty list */
list *next_element(list *l) {
	struct node *n = NULL;

	if(l->first != NULL) {
		n = l->first;
		l->first = l->first->next;
		delete_node(n);
	}
	return l;
}
