function outputImage = convolve (inputImage , kernel)
	disp('[convolve] Llamando a uConvolve')
	outputImage = uConvolve (inputImage, kernel, '2dconvo');
end