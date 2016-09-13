# This file builds and packages all the code for this assignment. Ie.: C and 
# Java versions.

unpackage:
	tar xzf java.tgz
	rm -f java.tgz

build:
	ant jar -silent -f java/build.xml
	
clean:
	ant clean -silent -f java/build.xml

package:
	tar czf java.tgz java
	rm -rf java
	