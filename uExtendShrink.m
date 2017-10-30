% operation: 'extend' genera un marco del tamaño adecuado para realizar la 
% 	convolución con un kernel pasado por parámetro. Con 'shrink' se elimina
%	dicho marco.
function outputImage = uExtendShrink (inputImage, kernel, operation)
	
	[r, c] = size (inputImage);
	[kr kc] = size (kernel);

	disp(sprintf('[uExtendShrink] tamaño del marco horizontal: %2.0f', kc/2))
	disp(sprintf('[uExtendShrink] tamaño del marco vertical: %2.0f', kr/2))

	% Operación de extensión
	if (strcmp (operation, 'extend'))
		disp(sprintf('[uExtendShrink] Realizando extend'))
	
		t_marco_h = round(kc/(2*2));
		t_marco_v = round(kr/(2*2));
		
		marco_h = zeros(t_marco_h, c);
		inputImage_marco_h = [marco_h; inputImage; marco_h];
		[r_,c_] = size(inputImage_marco_h)
		
		marco_v = zeros(r_, t_marco_v);
		outputImage = [marco_v inputImage_marco_h marco_v];
	
	% Operación de recorte
	else
		disp(sprintf('[uExtendShrink] Realizando shrink'))
	end




end