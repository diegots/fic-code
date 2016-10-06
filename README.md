# Turing Machine simulation.

This project contains three implementations of a Turing Machine model. User can
run the Machine with the desired input. Tape final state is written to a file 
if provided some name as a parameter. The number of steps done is printed on the
standard output, and also the accept status.

Each implementation is done in a different language. There are Java, C and OcaML
versions.

### How to use it:

First unpack the project and build it:
`user@host:fic-dlp-assignment-1$ make`

Then to execute desired implemetation:
`user@host:fic-dlp-assignment-1$ c/bindir/main machineDesc/bb2 dest-file`

Result will be printed in the standard output. dest-file is optional.

To build the project for the second time, just run
`make build`

C and OcaML compilation moves the resulting binary to `c/bindir/main` and 
`ocaml/bindir/ocaml` respectively. Java can be found in `java/dist/java.jar`

### Execution example:
user@host:fic-dlp-assignment-1$ make build
ant jar -silent -f java/build.xml # Java compilation
Buildfile: /home/diego/workspace/fic-dlp-assignment-1/java/build.xml
if [ ! -d ocaml/bindir ] ; then mkdir ocaml/bindir ; fi # Ocaml compilation
    ocamlc -I ocaml -c ocaml/io.mli ocaml/io.ml
    ocamlc -I ocaml -c ocaml/engine.mli ocaml/engine.ml
    ocamlc -I ocaml -c ocaml/main.ml
    ocamlc -I ocaml -o ocaml/bindir/ocaml io.cmo engine.cmo main.cmo
    mv ocaml/*.c* ocaml/bindir # Move out all binary files from source dir
    if [ ! -d c/bindir ] ; then mkdir c/bindir ; fi # C compilation
        gcc -o c/bindir/main c/*.c
user@host:fic-dlp-assignment-1$
user@host:fic-dlp-assignment-1$
user@host:fic-dlp-assignment-1$ time java -jar java/dist/java.jar machineDesc/posbb5 outFile-java
Input:
Accept: yes
Steps: 47176870

real    59m27.393s
user    58m43.126s
sys     0m4.023s

Yes, Java version with posbb5 machine example is **really** slow. This is the 
worst case, fortunately.
