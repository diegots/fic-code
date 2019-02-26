#!/usr/bin/env Rscript
#
# Este script está pensado para el caso de querer seleccionar un número 
# de usuarios de los cuales se evalurán todos sus items.
#
# Genera una lista de num IDs de usuario a partir del dataset de entrada.
# Ejemplo de ejecución: ./number.R infile num outfile
# Ejemplo de ejecución: ./number.R u.data 5 usuarios.txt

library(data.table)

args = commandArgs(trailingOnly=TRUE)
if (length(args) != 3)
	stop("Missing command arguments!")

a. <- fread(args[1], header = FALSE) # lee fichero entrada
b. <- unique(a.[, c("V1")]) # extrae todas las filas de la columna V1
c. <- sample(b.$V1, as.integer(args[2]), replace=FALSE) # genera una muestra aleatoria
fwrite(as.data.table(c.), file=args[3], col.names=FALSE) # escribe la salida
