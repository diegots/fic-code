function outputImage = convolve (inputImage , kernel)
	disp('[convolve] Llamando a uConvolve')
	outputImage = uConvolve (inputImage, kernel, '2dconvo');
	
	% Implementación de convolución de MATLAB
	%outputImage = conv2(inputImage, kernel, 'same');
end
