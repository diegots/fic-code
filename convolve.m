
function outputImage = convolve (inputImage , kernel, shape)
	disp('[convolve] Llamando a uConvolve')
	outputImage = uConvolve (inputImage, kernel, shape, '2dconvo');
		
	% Implementaci�n de convoluci�n de MATLAB
	%outputImage = conv2(inputImage, kernel, shape);
	
end
