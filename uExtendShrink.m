% operation: 'extend' genera un marco del tama�o adecuado para realizar la 
% 	convoluci�n con un kernel pasado por par�metro. Con 'shrink' se elimina
%	dicho marco.
function outputImage = uExtendShrink (inputImage, kernel, operation, fillValue)
	
	% %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% %
	% SE CONSIDERA UN KERNEL DE LADO IMPAR PARA ESTA OPERACI�N %
	% %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% %
	
	[r, c] = size (inputImage);
	[kr kc] = size (kernel);

	% Operaci�n de extensi�n
	if (strcmp (operation, 'extend'))
		disp(sprintf('[uExtendShrink] Realizando extend'))
	
		t_marco_h = (kc-1)/2;
		t_marco_v = (kr-1)/2;
		
		if (fillValue)
			marco_h = ones(t_marco_h, c);
		else
			marco_h = zeros(t_marco_h, c);
		end
		
		[a,b] = size(marco_h);
		disp(sprintf('[uExtendShrink] Tama�o del marco horizontal: %dx%d', a, b))
		
		inputImage_marco_h = [marco_h; inputImage; marco_h];
		[r_,c_] = size(inputImage_marco_h);
		
		if (fillValue)
			marco_v = ones(r_, t_marco_v);
		else
			marco_v = zeros(r_, t_marco_v);
		end
		
		[a,b] = size(marco_v);
		disp(sprintf('[uExtendShrink] Tama�o del marco vertical: %dx%d', a, b))
		
		outputImage = [marco_v inputImage_marco_h marco_v];
		
	% Operaci�n de recorte
	elseif (strcmp (operation, 'shrink'))
		disp(sprintf('[uExtendShrink] Realizando shrink'))
		indexes = reshape([1:r*c],r,c);
		centers = indexes (ceil(kr/2):r-(floor(kr/2)), ceil(kc/2):c-(floor(kc/2)));
		outputImage = inputImage(centers);
	
	else 
		disp(sprintf('[uExtendShrink] Operaci�n no v�lida'))
	end

	[r, c] = size (outputImage);
	disp(sprintf('[uExtendShrink] Nuevo tama�o: %dx%d', r, c))
	
end