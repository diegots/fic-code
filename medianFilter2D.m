% Implementar el filtro de orden de medianas. La funci�n permitir� establecer el
% tama�o del filtro.
function outputImage = medianFilter2D (inputImage, filterSize)
	disp(sprintf('[medianFilter2D] Llamando a uConvolve con tamaño %d de filtro', ...
		filterSize))
	
	% Se genera una matriz de unos del tama�o filterSize
	kernel = ones (filterSize);
	
	outputImage = uConvolve (inputImage, kernel, 'full', 'median');
	
	% Implementaci�n de MATLAB
	%outputImage = medfilt2(inputImage, [filterSize filterSize]) ;
	
end