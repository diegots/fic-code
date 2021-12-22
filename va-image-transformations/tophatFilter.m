% Operaci�n de TopHat blanco y negro. Las pruebas con las implementaciones de 
% MATLAB no son equivalentes ya que la operaci�n de TopHat se realiza sobre 
% escala de grises. Aqu� el opening y closing son sobre im�genes binarias
function outputImage = tophatFilter (inputImage, strElType, strElSize, mode)
	
	% El TopHat blanco consiste en restar a la imagen original, el resultado
	% de una apertura
	if (strcmp (mode, 'white'))
        apertura = opening (inputImage, strElType, strElSize);
        outputImage = inputImage - apertura;
	
		%% Probar con la implementaci�n TopHat de MATLAB
        %kernel = uKernelMorfologicos (strElType, strElSize);
        %outputImage = imtophat (inputImage, kernel);
	
	% El TopHat negro consiste en restar a un cierre sobre la imagen, los valores
	% de la imagen original
	elseif (strcmp (mode, 'black'))
		cierre = closing (inputImage, strElType, strElSize);
		outputImage = cierre - inputImage;
	
		% Probar con la implementaci�n TopHat negro de MATLAB
		%kernel = uKernelMorfologicos (strElType, strElSize);
        %outputImage = imbothat (inputImage, kernel);
		
	else
		disp(sprintf('[tophatFilter] Modo "%s" no soportado. Prueba "white" o "black"', mode))
	end

end