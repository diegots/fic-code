function outputImage = convolve (inputImage , kernel)
	outputImage = uConvolve (inputImage, kernel, '2dconvo');
end