all: presentation report 

report:
	pdflatex -output-directory build-report/ report.tex	         

presentation:
	pdflatex -output-directory build-presentation/ presentation.tex 

clean:
	rm -rf build-report/* build-presentation/*
