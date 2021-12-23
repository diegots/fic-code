#!/usr/bin/env Rscript

# Libraries install commands (from inside R environment):
# install.packages("data.table")
# install.packages("RUnit")

# load libraries
library(data.table)
library(RUnit)

# cli arguments
args = commandArgs(trailingOnly=TRUE)
if (length(args) != 1)
	stop("Missing command arguments!")

# setup data
a. <- fread(args[1], sep="\t")    

# tests
userIds <- c(3, 5, 2, 4, 1)
recomms <- c("[5:8.298, 3:5.197, 6:4.432]",
             "[7:7.006, 1:5.869, 6:3.572]",
             "[1:6.34, 2:5.96, 6:3.896]",
             "[8:6.6, 2:5.254, 3:4.531]",
             "[7:7.129, 8:5.854, 3:3.911]")


for (i in c(1:5)) {
    checkEquals(a.$V1[i], userIds[i])
    checkEquals(a.$V2[i], recomms[i])
}