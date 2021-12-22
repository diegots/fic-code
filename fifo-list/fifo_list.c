#include <stddef.h>
#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <assert.h>
#include "fifo_list.h"

struct node {
	void *element;
	struct node *next;
};

void delete_node(struct node *n);
struct node *get_node(list *l);
void next_element(list *l);
list *insert_node(list *l, struct node *n);
struct node *create_node(void *element, size_t s);

/* Creates an empty list and returns a pointer to it. */
list *create_list() {

	list *l = (list *) malloc(sizeof(list));;
	if(l == NULL) {
            fprintf (stderr, "Couldn't allocate memory for the list\n"); 
            return;
        }

	l->first = NULL;
	l->last = NULL;
        return l;
}

/* Creates one new list node.  */
struct node *create_node(void *element, size_t s) {

	struct node *n = malloc (sizeof *n);
	if(n == NULL) return NULL;

	n->next = NULL;
	n->element = malloc(s);
	if(n->element == NULL) return NULL;

        memcpy(n->element, element, s);

	return n;
}

/* Delete given l list */
void delete_list(list *l) {

        if (l->first == NULL && l->last != NULL) {
                fprintf (stderr, "Bad pointers. first NULL but last not\n"); 
                return; 
        }

        // Empty list
        if (l->first == NULL && l->last == NULL) {
                free(l); 
                return; 
        }

	while(l->first->next != NULL)
		next_element(l);

        delete_node(l->first);
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

	if (n != NULL) {
                void *element = n->element;
                free(n);
		return element;

        } else return NULL;
}

/* Get current node at the tail of the list */
struct node *get_node(list *l) {
        
        struct node *n = NULL;

        if (l->first == l->last && l->first == NULL) { // Empty list
                //fprintf (stdout, "Empty list\n"); 
                return NULL;
        } else if (l->first == l->last) { // List has one element
                //fprintf (stdout, "List has one element\n"); 
                n = l->first;
                l->first = NULL;
                l->last = l->first;
                return n;
        } else if ((l->first) != NULL) { // More than one elements
                //fprintf (stdout, "List has more than one element\n"); 
                n = l->first;
                l->first = l->first->next;
		return n;

        } else return NULL;
}
 
/* Inserts a new node at the head of the list */
list *insert_node(list *l, struct node *n) {
	
        // Empty list
        if (l->first == NULL && l->last == NULL) {
                l->first = n;
                l->last = n;
                return l; 

        // Insert at the end
        } else if ((l->first != NULL && l->last != NULL && l->first == l->last ) // List has one element
               ||  (l->first != l->last) ) { // List has two or more
                l->last->next = n;
		l->last = l->last->next;
                
        } else { fprintf (stderr, "Insertion operation error\n"); }
}

list *insert_element(list *l, void *element, size_t s) {
    struct node *n = create_node (element, s);
    return insert_node(l, n);
}

/* Deletes current node and points to the next */
void next_element(list *l) {

	if(l->first != NULL && l->first->next != NULL) {
		struct node *n = l->first;
		l->first = l->first->next;
		delete_node(n);
	}
}

/* ****** Tests ******  */
void test_create_list () {

    list *l = create_list ();
    assert (l->first == NULL && l->last == NULL);
    assert (l->first == l->last);
    delete_list (l);
}

void test_insert_element_and_delete_list () {

    list *l = create_list ();
    int a = 54;
    l = insert_element (l, &a, sizeof a);
    assert (l->first == l->last);
    assert (l->first != NULL);

    delete_list (l);
}

void test_insert_3_element_and_delete_list () {
    list *l = create_list ();
    int a = 54;
    l = insert_element (l, &a, sizeof a);
    assert (l->first == l->last);
    assert (l->first != NULL);

    int b = 55;
    l = insert_element (l, &b, sizeof b);
    assert (l->first != l->last);

    int c = 56;
    l = insert_element (l, &c, sizeof c);

    delete_list (l);
}

void test_get_element () {

    /* Empty list */
    list *llll = create_list ();
    int *qq = get_element (llll);
    assert (qq == NULL);
    delete_list (llll);

    /* List with one element */
    list *lll = create_list ();

    int p = 54;                         
    printf ("Put element: %d\n", p);
    lll = insert_element (lll, &p, sizeof p);

    int *pp = get_element (lll);
    if (pp == NULL) fprintf (stderr, "get_element returned NULL\n"); 
    assert (p == *pp);
    printf ("Got element: %d\n", *pp);
    free (pp);

    delete_list (lll);

    /* List with three element */
    list *ll = create_list ();

    int x = 54;                         
    printf ("Put element: %d\n", x);
    ll = insert_element (ll, &x, sizeof x);

    int y = 55;
    printf ("Put element: %d\n", y);
    ll = insert_element (ll, &y, sizeof y);

    int z = 56;
    printf ("Put element: %d\n", z);
    ll = insert_element (ll, &z, sizeof z);

    int *xx = get_element (ll);
    if (xx == NULL) fprintf (stderr, "get_element returned NULL\n"); 
    assert (x == *xx);
    printf ("Got element: %d\n", *xx);
    free (xx);

    delete_list (ll);
}
void test_get_elements () {

    list *ll = create_list ();

    int x = 54;                         
    printf ("Put element: %d\n", x);
    ll = insert_element (ll, &x, sizeof x);

    int y = 55;
    printf ("Put element: %d\n", y);
    ll = insert_element (ll, &y, sizeof y);

    int z = 56;
    printf ("Put element: %d\n", z);
    ll = insert_element (ll, &z, sizeof z);

    int *xx = get_element (ll);
    if (xx == NULL) fprintf (stderr, "get_element returned NULL\n"); 
    assert (x == *xx);
    printf ("Got element: %d\n", *xx);
    free (xx);

    xx = get_element (ll);
    if (xx == NULL) fprintf (stderr, "get_element returned NULL\n"); 
    assert (y == *xx);
    printf ("Got element: %d\n", *xx);
    free (xx);

    xx = get_element (ll);
    if (xx == NULL) fprintf (stderr, "get_element returned NULL\n"); 
    assert (z == *xx);
    printf ("Got element: %d\n", *xx);
    free (xx);

    delete_list (ll);
}
