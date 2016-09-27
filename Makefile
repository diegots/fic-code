# This file builds and packages all the code for this assignment. Ie.: C and 
# Java versions.

all: unpackage build

build:
	ant jar -silent -f java/build.xml
	
clean:
	@echo "Cleaning projects:"
	ant clean -silent -f java/build.xml

unpackage:
	@echo "Extracting project directories:"
	tar xzf java.tgz
	tar xzf ocaml.tgz
	@echo "Deleting compressed files:"
	rm -f java.tgz
	rm -f ocaml.tgz
	
package: clean
	@echo "Creating compressed tar packages:"
	tar czf java.tgz java
	tar czf ocaml.tgz ocaml
	@echo "Deleting project folders:"
	rm -rf java
	rm -rf ocaml