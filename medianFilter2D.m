% Implementar el filtro de orden de medianas. La función permitirá establecer el
% tamaño del filtro.
function outputImage = medianFilter2D (inputImage, filterSize)
	disp(sprintf('[medianFilter2D] Llamando a uConvolve con tamaÃ±o %d de filtro', ...
		filterSize))
	
	% Se genera una matriz de unos del tamaño filterSize
	kernel = ones (filterSize);
	
	outputImage = uConvolve (inputImage, kernel, 'full', 'median');
	
	% Implementación de MATLAB
	%outputImage = medfilt2(inputImage, [filterSize filterSize]) ;
	
end