#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "turingMachine.h"

machine create_MT(){
   machine mt;
   mt = (machine) malloc(sizeof(struct turingMachine));
   mt -> head = (head) malloc(sizeof(struct cell));
   if (mt == NULL) {
      printf("memoria agotada\n");
      exit(EXIT_FAILURE);
   }
   mt->conf = NULL;
   mt->head->right = NULL;
   mt->head->left = NULL;
   mt->head->element = 'B';
   return mt;
}


void delete_MT(machine *mt, int lines){
   while ((*mt)->head->right != NULL){
      pcell tmp = ((*mt)->head)->right;
      (*mt)->head->right = ((*mt)->head->right)->right;
      free(tmp);
   }
   while ((*mt)->head->left != NULL){
      pcell tmp = (*mt)->head->left;
      (*mt)->head->left = ((*mt)->head->left)->left;
      free(tmp);
   }
   
   int i = 0;
   for(i=0;i<lines;i++){
      free((*mt)->conf[i]);
   }
   free((*mt)->conf);
   free((*mt)->head);
   free(*mt); 
}

void write_cell(head *c, char direction, tElement e){

   // The item is written into the current cell and the head moves in the direction given / Escribimos el elemento en la celda actual y nos movemos en funcion de la direccion dada
   (*c) -> element = e;

   // Left direction
   if (direction == 'L'){ 

      if ((*c) -> left == NULL){
         pcell tmp = (pcell) malloc(sizeof(struct cell));
         if (tmp == NULL) {
            printf("memoria agotada\n");
            exit(EXIT_FAILURE);
         }
         tmp -> right = *c;
         tmp -> left = NULL;
         tmp -> element = 'B';
         (*c) -> left = tmp;
         *c = tmp;
      } else {
         *c = (*c) -> left;
      }

   } else if (direction == 'R'){ // Right direction
      if ((*c) -> right == NULL){
         head tmp = (head) malloc(sizeof(struct cell));
         if (tmp == NULL) {
            printf("Out of memory\n");
            exit(EXIT_FAILURE);
         }

         tmp -> right = NULL;
         tmp -> left = *c;
         tmp -> element = 'B';
         (*c) -> right = tmp;
         *c = tmp;
      } else {
         *c = (*c) -> right;

      }   

   }
}

void move_head_to_left(head *c){
   if (*c!=NULL)
      *c = (*c)->left;
}

void initialize_MT(machine *mt, char **array, char *sequence){
   int i;
   (*mt)->conf = array;
   for (i=0;i<strlen(sequence);i++){
      write_cell(&(*mt)->head,'R',sequence[i]);
   }
   while ((*mt)->head->left != NULL){
      move_head_to_left(&(*mt)->head);
   }
}

void run_MT(machine *mt, int lines){
   char state;
   int steps = 0, exit = 0, accept = 0;
   while(1){
      if ( steps == 0){ // If it's the first step get initial state / Si es el primer paso obtenemos el estado inicial
         state = (*mt)->conf[0][0];       
      }
      
      steps++;
      /* Leemos cada linea del array y buscamos si el estado de la linea y su alfabeto es igual al estado actual de la MT 
      y al caracter que señala el cabezal*/
      exit = 0;
      int i;
      for (i=0;i<lines;i++){
         if (((*mt)->conf[i][0] == state) && ((*mt)->head->element == (*mt)->conf[i][2])){
            exit = 1;
            state = (*mt)->conf[i][1];
            write_cell(&(*mt)->head, (*mt)->conf[i][4], (*mt)->conf[i][3]);
            break;
         }
      }
      // Si no se ha encontrado un estado siguiente se finaliza la ejecucion sin exito
      if (!exit){
        accept = 0;
        break;
      }
      // If the final state is reached then ends / Si se alcanza el estado final se sale
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

void mt_to_file(machine mt, char *file){
   // Open file in write mode
   FILE *pfile;
   pfile = fopen(file, "w");
   pcell tmp = (mt -> head);
   // We stand at the begining of the tape / Nos colocamos al principio de la cinta
   while (tmp -> left != NULL){
      tmp  = tmp -> left;
   }

   // Vamos escribiendo los valores a la izquierda hasta llegar a la cabeza
   while (tmp != (mt -> head)){
      fputc(tmp->element, pfile);
      tmp = tmp -> right;
   }

   fputc('\n', pfile);
   // Escribimos los valores restantes
   while (tmp != NULL){
      fputc(tmp->element, pfile);
      tmp = tmp -> right;
   }

   // Close file
   fclose(pfile);
}