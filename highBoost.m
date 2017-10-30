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
	
	o = (A .* inputImage) - imagen_suavizada;
	outputImage = imadjust(o, stretchlim(o),[0 1]);
	 
end