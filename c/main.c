#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "turingMachine.h"

#define TAMANHO 1024

int contar_lineas(char *ruta){
   int lines = 0;
   char ch;
   FILE *archivo;
   archivo = fopen(ruta, "r");
   while(!feof(archivo))
   {
     ch = fgetc(archivo);
     if(ch == '\n')
     {
       lines++;
     }
   }
   fclose(archivo);
   return (lines + 1);

}

char** crear_array(char *ruta, int lineas) {
   // Crea el array con el fichero de configuracion
   int i = 0;
   char **array_fichero;
   FILE *archivo;
   archivo = fopen(ruta, "r");

   if (archivo == NULL)
      exit(1);


   array_fichero = (char**) calloc(lineas, sizeof(char*));
   while (feof(archivo) == 0){
      array_fichero[i] = (char*) calloc(10, sizeof(char));
      fgets(array_fichero[i],10,archivo);
      i++;
   }   
   fclose(archivo);

   return array_fichero;
}

/* Función principal */
int main (int argc,char **argv){
	char input[TAMANHO];
   char **array_fichero;
   char ruta[100] = "../";
   int lineasFichero = 0, fichero = 0;

   // Comprobacion de parametros
   if (argc == 3) {
      fichero = 1;
	} else if (argc != 2){
		printf("Argumentos incorrectos.\n");
		return 0;
	}

	// Se solicita al usuario la secuencia de entrada de la MT
	printf("Input: ");
	fgets(input, TAMANHO, stdin);

   if (input[0]=='\n'){
      // Cadena de entrada en blanco, se ejecuta la MT como si entrase un Blanco
      input[0] = 'B';
   } else{ // Se elimina el caracter \n que se almacena en la cadena por usar la funcion fgets()
      input[strlen(input)-1] = '\0';
   }
	
   // Se crea la maquina de turing
   machine mt;
   mt = crear_MT();
   
   // Crea la ruta correta para acceder al archivo de configuracion de la MT
   strcat(ruta,argv[1]);

   // Crea el array con el fichero de configuracion
   lineasFichero = contar_lineas(ruta);
   array_fichero = crear_array(ruta, lineasFichero);

   // Se inicializa la maquina con el fichero de configuracion y la entrada introducida por el usuario
   inicializar_MT(&mt, array_fichero, input);
   ejecutar(&mt, lineasFichero);

   if (fichero){
      escribir_MT(mt, argv[2]);
   }

   eliminar_MT(&mt);

   /*
   int i = 0;
   for(i=0;i<lineasFichero;i++){
      free(array_fichero[lineasFichero]);
   }
   free(array_fichero);*/
   

	return 0;
}