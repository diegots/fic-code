# This file builds and packages all the code for this assignment. Ie.: C, Java
# and Ocaml versions.

OMLDIR = ocaml
OMLBINDIR = $(OMLDIR)/bindir
CDIR = c
CBINDIR = $(CDIR)/bindir

# Report ID in Google Docs.
REPORT_ID = 1AdYfQ0SjebgcSl4r19QzOdVM8jkXiqNTn_m1NK6lVOQ

all: unpackage build

build:
	ant jar -silent -f java/build.xml # Java compilation
	if [ ! -d $(OMLBINDIR) ] ; then mkdir $(OMLBINDIR) ; fi # Ocaml compilation
	ocamlc -I $(OMLDIR) -c $(OMLDIR)/io.mli $(OMLDIR)/io.ml
	ocamlc -I $(OMLDIR) -c $(OMLDIR)/engine.mli $(OMLDIR)/engine.ml
	ocamlc -I $(OMLDIR) -c $(OMLDIR)/main.ml
	ocamlc -I $(OMLDIR) -o $(OMLBINDIR)/ocaml io.cmo engine.cmo main.cmo
	mv $(OMLDIR)/*.c* $(OMLBINDIR) # Move out all binary files from source dir
	if [ ! -d $(CBINDIR) ] ; then mkdir $(CBINDIR) ; fi # C compilation
	gcc -o $(CBINDIR)/main $(CDIR)/*.c
	
clean:
	@echo "Cleaning projects:"
	ant clean -silent -f java/build.xml # Java
	rm -rf $(OMLBINDIR) # Ocaml
	rm -rf $(CBINDIR) # C

unpackage:
	@echo "Extracting project directories:"
	tar xzf java.tgz
	tar xzf ocaml.tgz
	tar xzf c.tgz
	@echo "Deleting compressed files:"
	rm -f java.tgz
	rm -f ocaml.tgz
	rm -f c.tgz
	@echo "Creating ocaml build dir:"
	
package: clean
	@echo "Downloading report in PDF format from Google Docs:"
	wget https://docs.google.com/document/d/$(REPORT_ID)/export?format=pdf -O Report.pdf --quiet
	@echo "Creating compressed tar packages:"
	tar czf java.tgz java
	tar czf ocaml.tgz ocaml
	tar czf c.tgz c
	@echo "Deleting project folders:"
	rm -rf java
	rm -rf ocaml
	rm -rf c
