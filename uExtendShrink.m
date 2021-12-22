% operation: 'extend' genera un marco del tama�o adecuado para realizar la 
% 	convoluci�n con un kernel pasado por par�metro. Con 'shrink' se elimina
%	dicho marco.
function outputImage = uExtendShrink (inputImage, kernel, operation, fillValue)
	
	[r, c] = size (inputImage);
	[kr kc] = size (kernel);

	% Operaci�n de extensi�n
	if (strcmp (operation, 'extend'))
		disp(sprintf('[uExtendShrink] Realizando extend'))
	
		if (mod(kc,2)) % cols impares
			t_marco_h = (kc-1)/2;
			t_marco_v = (kr-1)/2;
			
			marco_h = fillValue * ones(t_marco_h, c);
			
			[a,b] = size(marco_h);
			disp(sprintf('[uExtendShrink] Tama�o del marco horizontal: %dx%d', a, b))
			
			inputImage_marco_h = [marco_h; inputImage; marco_h];
			[r_,c_] = size(inputImage_marco_h);
			
			marco_v = fillValue * ones(r_, t_marco_v);
			
			[a,b] = size(marco_v);
			disp(sprintf('[uExtendShrink] Tama�o del marco vertical: %dx%d', a, b))
			
			outputImage = [marco_v inputImage_marco_h marco_v];

		else % cols pares
			t_marco_h_left = (kc/2)-1;
			t_marco_h_right = (kc/2);

			marco_h_left = fillValue * ones (t_marco_h_left, c);
			marco_h_right = fillValue * ones (t_marco_h_right, c);

			[a,b] = size(marco_h_right);
			disp(sprintf('[uExtendShrink] Tama�o del marco horizontal derecho: %dx%d', a, b))			
			[a,b] = size(marco_h_left);
			disp(sprintf('[uExtendShrink] Tama�o del marco horizontal izquierdo: %dx%d', a, b))
			
			inputImage_marco_h = [marco_h_left; inputImage; marco_h_right];
			[r_,c_] = size(inputImage_marco_h);
			
			t_marco_v_top = kr/2;
			t_marco_v_botton = (kr/2)-1;
			
			marco_v_top = fillValue * ones(r_, t_marco_v_top);
			marco_v_botton = fillValue * ones(r_, t_marco_v_botton);

			[a,b] = size(marco_v_top);
			disp(sprintf('[uExtendShrink] Tama�o del marco vertical superior: %dx%d', a, b))
			[a,b] = size(marco_v_botton);
			disp(sprintf('[uExtendShrink] Tama�o del marco vertical inferior: %dx%d', a, b))
			
			outputImage = [marco_v_top inputImage_marco_h marco_v_botton];
			
		end
	

		
	% Operaci�n de recorte
	elseif (strcmp (operation, 'shrink'))
		disp(sprintf('[uExtendShrink] Realizando shrink'))

		indexes = reshape([1:r*c],r,c);
		if (mod(kc,2)) % cols impares
			centers = indexes (ceil(kr/2):r-(floor(kr/2)), ceil(kc/2):c-(floor(kc/2)));
			outputImage = inputImage(centers);

		else % cols pares
			centers = indexes ((kr/2)+1 : r-(kr/2)+1, kc/2:c-(kc/2));
			outputImage = inputImage(centers);
		end
	else 
		disp(sprintf('[uExtendShrink] Operaci�n no v�lida'))
	end

	[r, c] = size (outputImage);
	disp(sprintf('[uExtendShrink] Nuevo tama�o: %dx%d', r, c))
	
end