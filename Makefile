all: presentation report 

report:
	pdflatex -output-directory build-report/ report.tex	         

report_b:
	bibtex build-report/report.aux

report_bib: report report_b report report

presentation:
	pdflatex -output-directory build-presentation/ presentation.tex 

clean:
	rm -rf build-report/* build-presentation/*
