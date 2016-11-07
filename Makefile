all: report 

report:
	pdflatex -output-directory build-report/ report.tex	         

report_bib:
	pdflatex -output-directory build-report/ report.tex	         
	bibtex build-report/report.aux
	pdflatex -output-directory build-report/ report.tex	         
	pdflatex -output-directory build-report/ report.tex	         

presentation:
	pdflatex -output-directory build-presentation/ presentation.tex 

clean:
	rm -rf build-report/* build-presentation/*
