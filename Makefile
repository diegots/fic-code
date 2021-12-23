# 
# Rules for compiling and linking the typechecker/evaluator
#
# Type
#   make         to rebuild the executable file f
#   make windows to rebuild the executable file f.exe
#   make test    to rebuild the executable and run it on input file test.f
#   make clean   to remove all intermediate and temporary files
#   make depend  to rebuild the intermodule dependency graph that is used
#                  by make to determine which order to schedule 
#	           compilations.  You should not need to do this unless
#                  you add new modules or new dependencies between 
#                  existing modules.  (The graph is stored in the file
#                  .depend)

# These are the object files needed to rebuild the main executable file
#
OBJS = support.cmo syntax.cmo core.cmo parser.cmo lexer.cmo main.cmo
TLOBJS = support.cmo syntax.cmo core.cmo parser.cmo lexer.cmo toplevel.cmo
MLFILES = core.ml main.ml support.ml syntax.ml

# Files that need to be generated from other files
DEPEND += lexer.ml parser.ml 

DOC = doc-html

# When "make" is invoked with no arguments, we build an executable 
# typechecker, after building everything that it depends on
all: $(DEPEND) $(OBJS) f

# On a Windows machine, we do exactly the same except that the executable
# file that gets built needs to have the extension ".exe"
windows: $(DEPEND) $(OBJS) f.exe

doc: $(OBJS)
	@echo Generating HTML documentation
	if [ ! -d $(DOC) ] ; then mkdir $(DOC) ; fi
	ocamldoc -html -d $(DOC) *.ml *.mli

doc-graph: $(OBJS)
	@echo Generating dependency graph
	ocamldoc -o modules-graph.dot -dot *.ml *.mli
	dot -Grotate=0 -Ecolor="#CC4A14" -Nfillcolor="#54CC14" \
            -Tsvg -omodules-graph.svg modules-graph.dot
        


# Include an automatically generated list of dependencies between source files
include .depend

# Build toplevel typechecker
toplevel: $(OBJS) toplevel.cmo
	@echo Linking $@
	ocamlc -o $@ $(COMMONOBJS) $(TLOBJS) 

# Build an executable typechecker
f: $(OBJS) main.cmo 
	@echo Linking $@
	ocamlc -o $@ $(COMMONOBJS) $(OBJS) 

# Build an executable typechecker for Windows
f.exe: $(OBJS) main.cmo 
	@echo Linking $@
	ocamlc -o $@ $(COMMONOBJS) $(OBJS) 

# Build and test
test: all
	./f test.f

# Compile an ML module interface
%.cmi : %.mli
	ocamlc -c $<

# Compile an ML module implementation
%.cmo : %.ml
	ocamlc -c $<

# Generate ML files from a parser definition file
parser.ml parser.mli: parser.mly
	@rm -f parser.ml parser.mli
	ocamlyacc -v parser.mly
	@chmod -w parser.ml parser.mli

# Generate ML files from a lexer definition file
%.ml %.mli: %.mll
	@rm -f $@
	ocamllex $<
	@chmod -w $@

# Clean up the directory
clean::
	if [ -d $(DOC) ] ; then rm -rf $(DOC)/* ; fi
	rm -rf lexer.ml parser.ml parser.mli *.o *.cmo *.cmi parser.output \
	   f f.exe toplevel TAGS *~ *.bak *.dot *.svg

# Rebuild intermodule dependencies
depend:: $(DEPEND) 
	ocamldep $(INCLUDE) *.mli *.ml > .depend

# 