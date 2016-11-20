LIST=fifo_list
MAIN=test

all: compile 

compile:
	gcc -g -o $(MAIN) $(LIST).c $(MAIN).c

clean:
	rm $(MAIN)
