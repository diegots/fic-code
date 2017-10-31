
function outputImage = convolve (inputImage , kernel, shape)
	disp('[convolve] Llamando a uConvolve')
	outputImage = uConvolve (inputImage, kernel, shape, '2dconvo');
		
	% Implementación de convolución de MATLAB
	%outputImage = conv2(inputImage, kernel, shape);
	
end
