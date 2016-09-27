# This file builds and packages all the code for this assignment. Ie.: C, Java
# and Ocaml versions.

OMLDIR = ocaml
OMLBINDIR = $(OMLDIR)/bindir

all: unpackage build

build:
	ant jar -silent -f java/build.xml
	ocamlc $(OMLDIR)/ocaml.ml -o $(OMLBINDIR)/ocaml
	
clean:
	@echo "Cleaning projects:"
	ant clean -silent -f java/build.xml
	rm -rf $(OMLBINDIR)

unpackage:
	@echo "Extracting project directories:"
	tar xzf java.tgz
	tar xzf ocaml.tgz
	@echo "Deleting compressed files:"
	rm -f java.tgz
	rm -f ocaml.tgz
	@echo "Creating ocaml build dir:"
	mkdir $(OMLBINDIR)
	
package: clean
	@echo "Creating compressed tar packages:"
	tar czf java.tgz java
	tar czf ocaml.tgz ocaml
	@echo "Deleting project folders:"
	rm -rf java
	rm -rf ocaml