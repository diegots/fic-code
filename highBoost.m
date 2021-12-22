function outputImage = highBoost (inputImage, A, method, parameter)
	% https://www.youtube.com/watch?v=Qcl5g1UtEv4
	
	[r,c] = size (inputImage);
	outputImage = zeros(r,c);
	
	disp(sprintf('[highBoost] Calculando High Boost con par�metro A =%5.2f', A))
	disp('                    High Boost es una generalizaci�n de Unsharp Mask.')
	disp('                    Unsharp Mask calcula g(x,y) = f(x,y)-ver_smooth(x,y)')
	disp('                    High Boost calcula g(x,y) = A * f(x,y) - ver_smooth(x,y) con A>=0')

	imagen_suavizada = zeros(r,c);	
	if (strcmp (method, 'gaussian'))
		disp(sprintf('[highBoost] Calculando la imagen suavizada con un filtro Gaussiano de sigma=%d', parameter))
		imagen_suavizada = gaussianFilter2D (inputImage, parameter);
		
		kernel = gaussKernel2D(parameter); % kernel usando para el shrink
	
	elseif (strcmp (method, 'median'))
		disp(sprintf('[highBoost] Calculando la imagen suavizada con un filtro de medias de tama�o %d', parameter))
		imagen_suavizada = medianFilter2D (inputImage, parameter);
		
		kernel = ones (parameter); % kernel usando para el shrink
	else
		disp(sprintf('[highBoost] par�metro "method" no v�lido: %s', method))
	end
	
	% La imagen suavizada se ha calculado convolucionado como full -> hay que encogerla
	
	[a b] = size(imagen_suavizada);
	disp(sprintf('[highBoost] Tama�o imagen suavizada %dx%d', a, b))
	
	% Encogemos la imagen para devolverla con el mismo tama�o que la original
	imagen_suavizada = uExtendShrink(imagen_suavizada, kernel, 'shrink');
	
	[a b] = size(imagen_suavizada);
	disp(sprintf('[highBoost] Tama�o imagen suavizada con shrink: %dx%d', a, b))
	
	o = (A .* inputImage) - imagen_suavizada;
	
	% Despu�s de hacer el producto con A, los valores resultates se salen del 
	% rengo de trabajo 0-1, as� que normalizamos.
	outputImage = histAdapt(o, 0, 1);
	%outputImage = imadjust(o, stretchlim(o),[0 1]);
	 
end