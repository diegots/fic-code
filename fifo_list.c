#include <stddef.h>
#include <stdlib.h>
#include <string.h>

#include "list.h"

/* Creates an empty list */
list *create_list(list *l) {
	list *aux = NULL;

	aux = (list *) malloc(sizeof(list));;
	if(aux == NULL) return NULL;

	aux->first = NULL;
	aux->last = NULL;

	return (l = aux);
}

/* Creates one new node */
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

/* Delete complete list */
void delete_list(list *l) {
	struct node *n = NULL;

	while((n = get_node(l)) != NULL) {
		l = next_element(l);
	}

	free(l);
}

/* Delete given node */
void delete_node(struct node *n) {
	free(n->element);
	free(n);
}

/* Get current stored element */
void *get_element(list *l) {
	return l->first->element;
}

/* Get current node */
struct node *get_node(list *l) {
	return l->first;	
}
 
/* Inserts a new node in the list */
list *insert_node(list *l, struct node *n) {
	
	if(l->first == NULL && l->last == NULL) {
		l->first = n;
		l->last = n;
	} else {
		l->last->next = n;
		l->last = n;
	}

	return l;
}

/* Passes to the next node in the list */
list *next_element(list *l) {
	struct node *n = NULL;

	n = l->first;
	l->first = l->first->next;
	delete_node(n);
	
	return l;
}
