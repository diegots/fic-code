OBJECTS=author.o cdir.o fifo_list.o get_pid.o list.o load.o parse_cli.o shell.o
SOURCE=author.c cdir.c fifo_list.c get_pid.c list.c load.c parse_cli.c shell.c
CC=gcc
PROG=shell

shell: $(OBJECTS)
	$(CC) $(OBJECTS) -o $(PROG) 

all: $(PROG)

clean:
	rm -fv $(PROG) *.o *~
