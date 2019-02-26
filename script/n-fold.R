#!/usr/bin/env Rscript
#
# Este script está pensado para el caso de querer seleccionar un número 
# de usuarios de los cuales se evalurán todos sus items.
#
# Obtención de un reparto n-fold de IDs
# Ejemplo de ejecución: ./percentaje.R infile n outfile
# Ejemplo de ejecución: ./percentaje.R HadoopCached/u.data 4 users.txt

library(data.table) 

args = commandArgs(trailingOnly=TRUE)
if (length(args) != 3)
        stop("Missing command arguments!")

n <- as.numeric(as.character(args[2]))

v <-fread(args[1]) # lectura del dataset
w <-v[,list(V1),]
x <- w [,.N, by = V1]
users_groups <- x [,.N,by=V1] 
number_users <- length (users_groups[,V1]) # nº de usuarios del dataset
users <- users_groups [,V1,] # conjunto de usuarios del dataset

exactos <- floor(number_users / n)
resto <- floor(number_users %% n)
base <- rep (exactos, n) # nº de elementos base
extra <- c ( rep (1, resto), rep (0, n-resto))
reparto <- base + extra

group <- sample (users, reparto[1])
groups <- data.table('1' = group)

for (i in 2:n) {
    group <- sample (users, reparto[i])

    if (reparto[i] < max(reparto)) {
	group <- c(group, rep(NA, max(reparto)-reparto[i]))
    }

    col_name <- toString (i) 
    groups [,(col_name) := group]
}

write.table(groups, file=args[3], sep="\t",  row.names=FALSE, col.names=FALSE)

