#include <stddef.h>

typedef struct _list list;
struct _list {
	struct node *first;
	struct node *last;
};

/* Functions */
list *create_list();
list *insert_element(list *l, void *element, size_t s);
void *get_element(list *l);
void delete_list(list *l);

/* ****** Tests ******  */
void test_create_list ();
void test_insert_element_and_delete_list ();
void test_insert_3_element_and_delete_list ();
void test_get_element ();
void test_get_elements ();
