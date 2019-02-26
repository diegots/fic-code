#!/usr/bin/env Rscript
# 
# Este script está pensado para el caso de querer seleccionar un porcentaje
# de usuarios de los cuales se evalurán todos sus items.
# Genera una lista de IDs a partir del dataset de entrada para . El script
# cuenta el número de IDs total del dataset para que las listas sean el 
# porcentaje correcto.
#
# Ejemplo de ejecución: ./percentaje.R infile percentage outfile
# Ejemplo de ejecución: ./percentaje.R HadoopCached/u.data 3 users.txt
#

library(data.table) 

args = commandArgs(trailingOnly=TRUE)
if (length(args) != 3)
        stop("Missing command arguments!")

v <-fread(args[1]) 
w <-v[,list(V1),]
x <- w [,.N, by = V1]
users <- x [,.N,by=V1]
number_users <- length (users[,V1])
percent = number_users * as.numeric(as.character(args[2])) * 0.01
users_list <- (sample (users[,V1], percent))
write.table(users_list, file=args[3], sep=",",  row.names=FALSE, col.names=FALSE)

