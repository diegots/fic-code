all: report_bib

report:
	if [ ! -d build-report ] ; then mkdir build-report ; fi
	pdflatex -output-directory build-report/ report.tex	         

report_bib:
	if [ ! -d build-report ] ; then mkdir build-report ; fi
	pdflatex -output-directory build-report/ report.tex	         
	bibtex build-report/report.aux
	pdflatex -output-directory build-report/ report.tex	         
	pdflatex -output-directory build-report/ report.tex	         

presentation:
	if [ ! -d build-presentation ] ; then mkdir build-presentation ; fi
	pdflatex -output-directory build-presentation/ presentation.tex 

clean:
	if [ -d build-report ] ; then rm -rf build-report/* ; fi
	if [ -d build-presentation ] ; then rm -rf build-presentation/* ; fi
