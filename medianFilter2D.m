% Implementar el filtro de orden de medianas. La función permitirá establecer el
% tamaño del filtro.
function outputImage = medianFilter2D (inputImage, filterSize)
	disp(sprintf('[medianFilter2D] Llamando a uConvolve con tamaño de filtro %d', ...
		filterSize))
	
	% Se genera una matriz de unos del tamaño filterSize
	kernel = ones (filterSize);
	
	shape = 'full';
	%shape = 'same';
	
	outputImage = uConvolve (inputImage, kernel, shape, 'median');
	
	% Implementación de MATLAB
	% No funciona correctamente porque el resultado no es la imagen apliada
	% como en el caso de una convolución full
	%outputImage = medfilt2(inputImage, [filterSize filterSize],'zeros');
	
end