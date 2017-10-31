% operation: 'extend' genera un marco del tamaño adecuado para realizar la 
% 	convolución con un kernel pasado por parámetro. Con 'shrink' se elimina
%	dicho marco.
function outputImage = uExtendShrink (inputImage, kernel, operation, fillValue)
	
	% %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% %
	% SE CONSIDERA UN KERNEL DE LADO IMPAR PARA ESTA OPERACIÓN %
	% %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% %
	
	[r, c] = size (inputImage);
	[kr kc] = size (kernel);

	% Operación de extensión
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
		disp(sprintf('[uExtendShrink] Tamaño del marco horizontal: %dx%d', a, b))
		
		inputImage_marco_h = [marco_h; inputImage; marco_h];
		[r_,c_] = size(inputImage_marco_h);
		
		if (fillValue)
			marco_v = ones(r_, t_marco_v);
		else
			marco_v = zeros(r_, t_marco_v);
		end
		
		[a,b] = size(marco_v);
		disp(sprintf('[uExtendShrink] Tamaño del marco vertical: %dx%d', a, b))
		
		outputImage = [marco_v inputImage_marco_h marco_v];
		
	% Operación de recorte
	elseif (strcmp (operation, 'shrink'))
		disp(sprintf('[uExtendShrink] Realizando shrink'))
		indexes = reshape([1:r*c],r,c);
		centers = indexes (ceil(kr/2):r-(floor(kr/2)), ceil(kc/2):c-(floor(kc/2)));
		outputImage = inputImage(centers);
	
	else 
		disp(sprintf('[uExtendShrink] Operación no válida'))
	end

	[r, c] = size (outputImage);
	disp(sprintf('[uExtendShrink] Nuevo tamaño: %dx%d', r, c))
	
end