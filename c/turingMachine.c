#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "turingMachine.h"

/* Crea una maquina de turing */
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

/* Coloca la cabeza en la celda mas a la izquierda de la cinta */
void move_head_to_left(head *h){
   if (*h!=NULL)
      *h = (*h)->left;
}

/* Escribe el caracter e en la cabeza y se mueve en la direccion direction */
void write_cell(head *h, char direction, tElement e){

   // The item is written into the current cell / Escribimos el elemento en la celda actual
   (*h) -> element = e;

   // Left direction
   if (direction == 'L'){ 
      // Si no hay celda izquierda se crea y se mueve el cabezal a la izquierda
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
      // Si la celda izquierda existe, simplemente se mueve la cabeza a esa posicion
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

/* Inicializa la maquina de turing */
void initialize_MT(machine *mt, char **array, char *sequence){
   int i;
   // Almacena el array de configuracion
   (*mt)->conf = array;
   // Escribe la secuencia introducida por el usuario en la cinta
   for (i=0;i<strlen(sequence);i++){
      write_cell(&(*mt)->head,'R',sequence[i]);
   }
   // Coloca la cabeza al principio de la cinta usando la funcion auxiliar move_head_to_left
   while ((*mt)->head->left != NULL){
      move_head_to_left(&(*mt)->head);
   }
}

/* Ejecuta la maquina de turing */
void run_MT(machine *mt, int lines){
   char state;
   int steps = 0, running = 0, accept = 0;
   while(1){
      // If it's the first step get initial state / Si es el primer paso obtenemos el estado inicial
      if ( steps == 0){ 
         state = (*mt)->conf[0][0];       
      }
      
      steps++;
      /* Leemos cada linea del array y buscamos si el estado de la linea y su alfabeto es igual al estado actual de la MT 
      y al caracter que señala el cabezal*/
      running = 0;
      int i;
      for (i=0;i<lines;i++){
         // Si coincide, actualizamos el estado, escribimos en la cinta y nos movemos con la funcion auxiliar write_cell
         if (((*mt)->conf[i][0] == state) && ((*mt)->head->element == (*mt)->conf[i][2])){
            running = 1;
            state = (*mt)->conf[i][1];
            write_cell(&(*mt)->head, (*mt)->conf[i][4], (*mt)->conf[i][3]);
            break;
         }
      }
      // Si no se ha encontrado un estado siguiente se finaliza la ejecucion sin exito
      if (!running){
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

/* Elimina la maquina de turing y libera la memoria*/
void delete_MT(machine *mt, int lines){
   /* Mientras exista una celda a la derecha de la cabeza, se libera su 
   espacio. El puntero que apunta a la celda de la derecha pasa a apuntar
   a la siguiente celda de su derecha. Así hasta liberar todas las celdas a la
   derecha de la caebza.*/
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
   // Se libera el espacio ocupado por el fichero de configuracion, la cabeza, y la maquina de turing
   int i = 0;
   for(i=0;i<lines;i++){
      free((*mt)->conf[i]);
   }
   free((*mt)->conf);
   free((*mt)->head);
   free(*mt); 
}

/* Escribe los datos de la cinta en un fichero dada una ruta, si el fichero no existe lo crea */
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
   fputc('\n', pfile);


   // Close file
   fclose(pfile);
}