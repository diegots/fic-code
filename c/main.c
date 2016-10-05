#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "turingMachine.h"

#define SIZE 1024

/* Get the lines file located in the parameter path */
int get_lines(char *path){
   int lines = 0;
   char ch;
   FILE *file;
   file = fopen(path, "r");

   while(!feof(file))
   {
     ch = fgetc(file);
     if(ch == '\n')
     {
       lines++;
     }
   }

   fclose(file);
   return (lines + 1);

}


/* Create array with the configuration file / Crea el array con el fichero de configuracion */
char** crear_array(char *path, int lines) {
   int i = 0;
   char **file_array;
   FILE *file;
   file = fopen(path, "r");

   if (file == NULL)
      exit(1);


   file_array = (char**) calloc(lines, sizeof(char*));
   while (feof(file) == 0){
      file_array[i] = (char*) calloc(10, sizeof(char));
      fgets(file_array[i],10,file);
      i++;
   }   
   fclose(file);

   return file_array;
}

/* Main function */
int main (int argc,char **argv){
	char input[SIZE];
   char **file_array;
   char path[100] = "";
   int file_lines = 0, out_file = 0;

   // Check parameters / Comprobacion de parametros
   if (argc == 3) {
      out_file = 1;
	} else if (argc != 2){
		printf("Wrong arguments.\n");
		return 0;
	}

	// Get input / Se solicita al usuario la secuencia de entrada de la MT
	printf("Input: ");
	fgets(input, SIZE, stdin);

   if (input[0]=='\n'){
      // Input empty, run MT like input was 'B' / Cadena de entrada en blanco, se ejecuta la MT como si entrase un Blanco
      input[0] = 'B';
   } else{ // Delete character '\n' stored in the string to use fgets() / Se elimina el caracter \n que se almacena en la cadena por usar la funcion fgets()
      input[strlen(input)-1] = '\0';
   }
	
   // Create turing machine / Se crea la maquina de turing
   machine mt;
   mt = create_MT();
   
   // Create correct path to acces at the configuration file in the turing machine / Crea la path correta para acceder al archivo de configuracion de la MT
   strcat(path,argv[1]);

   // Create array with configuration file / Crea el array con el fichero de configuracion
   file_lines = get_lines(path);
   file_array = crear_array(path, file_lines);

   // Initialize machine with configuration file and the input. / Se inicializa la maquina con el fichero de configuracion y la entrada introducida por el usuario
   initialize_MT(&mt, file_array, input);
   run_MT(&mt, file_lines);

   if (out_file){
      mt_to_file(mt, argv[2]);
   }

   // Delete turing machine
   delete_MT(&mt, file_lines);

	return 0;
}