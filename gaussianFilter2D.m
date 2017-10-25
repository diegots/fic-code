% Implementar una funci�n que permita realizar un suavizado Gaussiano 
% bidimensional usando un filtro NxN de par�metro sigma, donde N se calcula 
% igual que en la funci�n gaussKernel1D
%
function outputImage = gaussianFilter2D (inputImage, sigma)

	kernel = gaussKernel2D(sigma);
	outputImage = convolve (inputImage, kernel);

end