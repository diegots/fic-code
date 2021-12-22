#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include "fifo_list.h"

int main (int argc, char *argv[]) {

    test_create_list ();
    test_insert_element_and_delete_list ();
    test_insert_3_element_and_delete_list ();
    test_get_element ();
    test_get_elements ();

    return 0;
}
