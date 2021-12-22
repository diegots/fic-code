% Implementar el filtro de orden de medianas. La funci�n permitir� establecer el
% tama�o del filtro.
function outputImage = medianFilter2D (inputImage, filterSize)
	disp(sprintf('[medianFilter2D] Llamando a uConvolve con tama�o de filtro %d', ...
		filterSize))
	
	% Se genera una matriz de unos del tama�o filterSize
	kernel = ones (filterSize);
	
	shape = 'full';
	%shape = 'same';
	
	outputImage = uConvolve (inputImage, kernel, shape, 'median');
	
	% Implementaci�n de MATLAB
	% No funciona correctamente porque el resultado no es la imagen apliada
	% como en el caso de una convoluci�n full
	%outputImage = medfilt2(inputImage, [filterSize filterSize],'zeros');
	
end