% Implementar el filtro de orden de medianas. La función permitirá establecer el
% tama˜no del filtro.
function outputImage = medianFilter2D (inputImage, filterSize)
	disp(sprintf('[medianFilter2D] Llamando a uConvolve con tamaño %d de filtro', filterSize))
	
	% Se genera una matriz de unos del tama˜no filterSize
	kernel = ones (filterSize);
	
	outputImage = uConvolve (inputImage, kernel, 'median');
	
end