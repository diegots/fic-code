#!/usr/bin/env Rscript
#
# Este script está pensado para el caso de querer seleccionar un número 
# de items conocidos que serán considerados para todos los usuarios (given-n). 
# También se puede utilizar para el caso de que los items seleccionados vayan
# a estar ocultos y se realiza la evaluación con todos los demás (all-but-n).
#
# Ejemplo de ejecución: ./given-n.R infile num outfile
# Ejemplo de ejecución: ./given.R HadoopCached/u.data 5 usuarios.txt

library(data.table) 

args = commandArgs(trailingOnly=TRUE)
if (length(args) != 3)
	stop("Missing command arguments!")

n <- as.numeric(as.character(args[2]))

v <-fread(args[1]) 
w <-v[,list(V2),] # extrae la columna de items
x <- w [,.N, by = V2]
items <- x [,.N,by=V2] # agrupa los items iguales
number_items <- length (items[,V2]) # cuenta los items distintos

if (n > number_items)
	stop("There aren't enough items in the dataset!")

items_list <- (sample (items[,V2], n))
write.table(items_list, file=args[3], sep=",",  row.names=FALSE, col.names=FALSE)

