# This file builds and packages all the code for this assignment. Ie.: C and 
# Java versions.

build:
	tar xzf java.tgz
	ant jar -silent -f java/build.xml
	
clean:
	ant clean -silent -f java/build.xml

package:
	tar czf java.tgz java
	rm -rf java
	