FUENTE1 = lexicoH
FUENTE2 = sintacticoH
FUENTE3 = heroquest
PRUEBA = mapaHeroquest.txt

all: compile run

compile:
	flex $(FUENTE1).l
	bison -o $(FUENTE2).tab.c $(FUENTE2).y -yd
	gcc -g -o $(FUENTE3) lex.yy.c $(FUENTE2).tab.c -lfl -ly

run:
	./$(FUENTE3)

clean:
	rm $(FUENTE3) lex.yy.c $(FUENTE2).tab.c $(FUENTE2).tab.h
