#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "turingMachine.h"

/* This function creates a turing machine */
machine create_MT(){
   machine mt;
   // Memory allocation
   mt = (machine) malloc(sizeof(struct turingMachine));
   mt -> head = (head) malloc(sizeof(struct cell));
   if (mt == NULL) {
      printf("Out of memory\n");
      exit(EXIT_FAILURE);
   }
   mt->conf = NULL;
   mt->head->right = NULL;
   mt->head->left = NULL;
   mt->head->element = 'B';
   return mt;
}

/* move_head_to_left places the head in the leftmost cell of the tape */
void move_head_to_left(head *h){
   if (*h!=NULL)
      *h = (*h)->left;
}

/* write_cell writes the character 'e' in the head and moves in the right direction */
void write_cell(head *h, char direction, tElement e){

   // The item is written into the current cell 
   (*h) -> element = e;

   // Left direction
   if (direction == 'L'){ 
      // If there isn't left cell, the cell is created and the head moves to the left
      if ((*h) -> left == NULL){
         pcell tmp = (pcell) malloc(sizeof(struct cell));
         if (tmp == NULL) {
            printf("Out of memory\n");
            exit(EXIT_FAILURE);
         }
         tmp -> right = *h;
         tmp -> left = NULL;
         tmp -> element = 'B';
         (*h) -> left = tmp;
         *h = tmp;
      // If the left cell exists, simply head moves to that position
      } else {
         *h = (*h) -> left;
      }

   // Right direction, idem left
   } else if (direction == 'R'){ 
      if ((*h) -> right == NULL){
         pcell tmp = (pcell) malloc(sizeof(struct cell));
         if (tmp == NULL) {
            printf("Out of memory\n");
            exit(EXIT_FAILURE);
         }

         tmp -> right = NULL;
         tmp -> left = *h;
         tmp -> element = 'B';
         (*h) -> right = tmp;
         *h = tmp;
      } else {
         *h = (*h) -> right;

      }   

   }
}

/* This function initializes the turing machine */
void initialize_MT(machine *mt, char **array, char *sequence){
   int i;
   // It stores the array configuration
   (*mt)->conf = array;
   // It writes the input sequence by the user on the tape
   for (i=0;i<strlen(sequence);i++){
      write_cell(&(*mt)->head,'R',sequence[i]);
   }
   // It places the head at the beginning of the tape using the auxiliary function move_head_to_left
   while ((*mt)->head->left != NULL){
      move_head_to_left(&(*mt)->head);
   }
}

/* This function runs turing machine */
void run_MT(machine *mt, int lines){
   char state;
   int steps = 0, running = 0, accept = 0;
   while(1){
      // If it's the first step then we get initial state 
      if ( steps == 0){ 
         state = (*mt)->conf[0][0];       
      }
      
      steps++;
      /* It reads each line of the array and look if the state of the line and 
	its alphabet is equal to the current state of the MT and the character 
	that marks the head*/
      running = 0;
      int i;
      for (i=0;i<lines;i++){
         // If it matches, it updates the state machine, writes on the tape and moves with the auxiliary function write_cell
         if (((*mt)->conf[i][0] == state) && ((*mt)->head->element == (*mt)->conf[i][2])){
            running = 1;
            state = (*mt)->conf[i][1];
            write_cell(&(*mt)->head, (*mt)->conf[i][4], (*mt)->conf[i][3]);
            break;
         }
      }
      // If the machine has not found a state, the machine ends
      if (!running){
        accept = 0;
        break;
      }
      // If the final state is reached then ends 
      if (state == 'H'){
         accept = 1;
         break;
      }
   }

   if (accept){         
      printf("Accept: Yes\n");
   } else{
      printf("Accept: No\n");         
   }
   
   printf("Steps: %i\n", steps);
}

/* delete_MT deletes turing machine and frees memory*/
void delete_MT(machine *mt, int lines){
   /* While there is a cell to the right of the head, the space is freed. The 
   pointer to the cell to the right passes to point to the next cell to the 
   right. To release all the cells to the right of the head.*/
   while ((*mt)->head->right != NULL){
      pcell tmp = ((*mt)->head)->right;
      (*mt)->head->right = ((*mt)->head->right)->right;
      free(tmp);
   }
   // Idem left
   while ((*mt)->head->left != NULL){
      pcell tmp = (*mt)->head->left;
      (*mt)->head->left = ((*mt)->head->left)->left;
      free(tmp);
   }
   // It frees the space occupied by the configuration file, head, and turing machine
   int i = 0;
   for(i=0;i<lines;i++){
      free((*mt)->conf[i]);
   }
   free((*mt)->conf);
   free((*mt)->head);
   free(*mt); 
}

/* It writes the data tape in a file given a route, if the file does not exist create it */
void mt_to_file(machine mt, char *file){
   // Open file in write mode
   FILE *pfile;
   pfile = fopen(file, "w");
   pcell tmp = (mt -> head);
   // We stand at the begining of the tape 
   while (tmp -> left != NULL){
      tmp  = tmp -> left;
   }

   // It writes the values to the left until you reach the head
   while (tmp != (mt -> head)){
      fputc(tmp->element, pfile);
      tmp = tmp -> right;
   }

   fputc('\n', pfile);
   // It writes the remaining values
   while (tmp != NULL){
      fputc(tmp->element, pfile);
      tmp = tmp -> right;
   }
   fputc('\n', pfile);


   // Close file
   fclose(pfile);
}
