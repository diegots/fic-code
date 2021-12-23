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


/* Create array with the configuration file */
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

   // Checking parameters 
   if (argc == 3) {
      out_file = 1;
	} else if (argc != 2){
		printf("Wrong arguments.\n");
		return 0;
	}

	// Get input 
	printf("Input: ");
	fgets(input, SIZE, stdin);

   if (input[0]=='\n'){
      // Input empty, run MT like input was 'B' 
      input[0] = 'B';
   } else{ // Character '\n' is removed 
      input[strlen(input)-1] = '\0';
   }
	
   // Create turing machine 
   machine mt;
   mt = create_MT();
   
   // Create correct path to access at the configuration file in the turing machine 
   strcat(path,argv[1]);

   // Create array with configuration file 
   file_lines = get_lines(path);
   file_array = crear_array(path, file_lines);

   // Initialize machine with configuration file and the input. 
   initialize_MT(&mt, file_array, input);
   run_MT(&mt, file_lines);

   if (out_file){
      mt_to_file(mt, argv[2]);
   }

   // Delete turing machine
   delete_MT(&mt, file_lines);

	return 0;
}
