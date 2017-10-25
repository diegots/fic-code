% Implementar una función que permita realizar un suavizado Gaussiano 
% bidimensional usando un filtro NxN de parámetro sigma, donde N se calcula 
% igual que en la función gaussKernel1D
%
function outputImage = gaussianFilter2D (inputImage, sigma)

	kernel = gaussKernel2D(sigma);
	outputImage = convolve (inputImage, kernel);

end