function outputImage = highBoost (inputImage, A, method, parameter)

	[r,c] = size (inputImage);
	outputImage = zeros(r,c);
	
	disp(sprintf('[highBoost] Calculando High Boost con par�metro A=%d', A))

	% Opci�nn 1 aplicando la f�rmula
	imagen_suavizada = zeros(r,c);	
	if (strcmp (method, 'gaussian'))
		disp(sprintf('[highBoost] Calculando la imagen suavizada con un filtro Gaussiano de sigma=%d', parameter))
		imagen_suavizada = gaussianFilter2D (inputImage, parameter);
	elseif (strcmp (method, 'median'))
		disp(sprintf('[highBoost] Calculando la imagen suavizada con un filtro de medias de tama�o %d', parameter))
		imagen_suavizada = medianFilter2D (inputImage, parameter);
	else
		disp(sprintf('[highBoost] par�metro "method" no v�lido: %s', method))
	end
	
	outputImage = (A .* inputImage) - imagen_suavizada;
	
%	% Opci�n 2 mediante m�scaras de convoluci�nn. El valor de A controla la 
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