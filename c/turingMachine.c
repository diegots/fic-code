#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "turingMachine.h"

machine crear_MT(){
   machine mt;
   mt = (machine) malloc(sizeof(struct turingMachine));
   mt -> cabeza = (cabeza) malloc(sizeof(struct celda));
   if (mt == NULL) {
      printf("memoria agotada\n");
      exit(EXIT_FAILURE);
   }
   mt->conf = NULL;
   mt->cabeza->derecha = NULL;
   mt->cabeza->izquierda = NULL;
   mt->cabeza->elemento = 'B';
   return mt;
}


void eliminar_MT(machine *mt, int lineas){
   while ((*mt)->cabeza->derecha != NULL){
      pcelda tmp = ((*mt)->cabeza)->derecha;
      (*mt)->cabeza->derecha = ((*mt)->cabeza->derecha)->derecha;
      free(tmp);
   }
   while ((*mt)->cabeza->izquierda != NULL){
      pcelda tmp = (*mt)->cabeza->izquierda;
      (*mt)->cabeza->izquierda = ((*mt)->cabeza->izquierda)->izquierda;
      free(tmp);
   }
   
   int i = 0;
   for(i=0;i<lineas;i++){
      free((*mt)->conf[i]);
   }
   free((*mt)->conf);
   free((*mt)->cabeza);
   free(*mt); 
}

void escribir(cabeza *c, char direccion, tElemento e){

   // Escribimos el elemento en la celda actual y nos movemos en funcion de la direccion dada
   (*c) -> elemento = e;

   // Izquierda
   if (direccion == 'L'){ 

      if ((*c) -> izquierda == NULL){
         pcelda tmp = (pcelda) malloc(sizeof(struct celda));
         if (tmp == NULL) {
            printf("memoria agotada\n");
            exit(EXIT_FAILURE);
         }
         tmp -> derecha = *c;
         tmp -> izquierda = NULL;
         tmp -> elemento = 'B';
         (*c) -> izquierda = tmp;
         *c = tmp;
      } else {
         *c = (*c) -> izquierda;
      }

   } else if (direccion == 'R'){ // Derecha
      if ((*c) -> derecha == NULL){
         cabeza tmp = (cabeza) malloc(sizeof(struct celda));
         if (tmp == NULL) {
            printf("memoria agotada\n");
            exit(EXIT_FAILURE);
         }

         tmp -> derecha = NULL;
         tmp -> izquierda = *c;
         tmp -> elemento = 'B';
         (*c) -> derecha = tmp;
         *c = tmp;
      } else {
         *c = (*c) -> derecha;

      }   

   }
}

void mover_cabeza_izquierda(cabeza *c){
   if (*c!=NULL)
      *c = (*c)->izquierda;
}

void inicializar_MT(machine *mt, char **array, char *secuencia){
   int i;
   (*mt)->conf = array;
   for (i=0;i<strlen(secuencia);i++){
      escribir(&(*mt)->cabeza,'R',secuencia[i]);
   }
   while ((*mt)->cabeza->izquierda != NULL){
      mover_cabeza_izquierda(&(*mt)->cabeza);
   }
}

void ejecutar(machine *mt, int lineas){
   char estado;
   int steps = 0, fin = 0, accept = 0;
   while(1){
      if ( steps == 0){ // Si es el primer paso obtenemos el estado inicial
         estado = (*mt)->conf[0][0];       
      }
      
      steps++;
      /* Leemos cada linea del array y buscamos si el estado de la linea y su alfabeto es igual al estado actual de la MT 
      y al caracter que señala el cabezal*/
      fin = 0;
      int i;
      for (i=0;i<lineas;i++){
         if (((*mt)->conf[i][0] == estado) && ((*mt)->cabeza->elemento == (*mt)->conf[i][2])){
            fin = 1;
            estado = (*mt)->conf[i][1];
            escribir(&(*mt)->cabeza, (*mt)->conf[i][4], (*mt)->conf[i][3]);
            break;
         }
      }
      // Si no se ha encontrado un estado siguiente se finaliza la ejecucion sin exito
      if (!fin){
        accept = 0;
        break;
      }
      // Si se alcanza el estado final se sale
      if (estado == 'H'){
         accept = 1;
         break;
      }
   }

   if (accept){         
      printf("Accept: Yes\n");
   } else{
      printf("Accept: No\n");         
   }
   /*if (fichero){
      //escribir_MT(c, argv[2]);
   }*/
   printf("Steps: %i\n", steps);
}

void escribir_MT(machine mt, char *fichero){
   // Open file in write mode
   FILE *pfichero;
   pfichero = fopen(fichero, "w");
   pcelda tmp = (mt -> cabeza);
   // Nos colocamos al principio de la cinta
   while (tmp -> izquierda != NULL){
      tmp  = tmp -> izquierda;
   }

   // Vamos escribiendo los valores a la izquierda hasta llegar a la cabeza
   while (tmp != (mt -> cabeza)){
      fputc(tmp->elemento, pfichero);
      tmp = tmp -> derecha;
   }

   fputc('\n', pfichero);
   // Escribimos los valores restantes
   while (tmp != NULL){
      fputc(tmp->elemento, pfichero);
      tmp = tmp -> derecha;
   }

   // Close file
   fclose(pfichero);
}