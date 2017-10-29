function outputImage = highBoost (inputImage, A, method, parameter)

	[r,c] = size (inputImage);
	outputImage = zeros(r,c);
	
	disp(sprintf('[highBoost] Calculando High Boost con parámetro A=%d', A))

	% Opciónn 1 aplicando la fórmula
	imagen_suavizada = zeros(r,c);	
	if (strcmp (method, 'gaussian'))
		disp(sprintf('[highBoost] Calculando la imagen suavizada con un filtro Gaussiano de sigma=%d', parameter))
		imagen_suavizada = gaussianFilter2D (inputImage, parameter);
	elseif (strcmp (method, 'median'))
		disp(sprintf('[highBoost] Calculando la imagen suavizada con un filtro de medias de tamaño %d', parameter))
		imagen_suavizada = medianFilter2D (inputImage, parameter);
	else
		disp(sprintf('[highBoost] parámetro "method" no válido: %s', method))
	end
	
	outputImage = (A .* inputImage) - imagen_suavizada;
	
%	% Opción 2 mediante máscaras de convoluciónn. El valor de A controla la 
%	% salida de tal forma que si 
%	% A = 0 --> Laplaciano
%	% A < 0 --> Bordes
%	% A > 0 --> Realce
%	kernel = [-1  -1 -1;
%	          -1 A+8 -1;
%	          -1  -1 -1];
%			  
%	kernel = [ 0  -1  0;
%	          -1 A+4 -1;
%	           0  -1  0];			  
%	outputImage = convolve (inputImage, kernel);
%	%outputImage = conv2(inputImage, kernel, 'same');
	 
end