function operator = uSpecial (operator)

	if (strcmp (operator, 'Roberts'))
		operator = [0 1; -1 0];
	
	elseif (strcmp (operator, 'CentralDiff'))
		operator = [-1 0 1];
	
	elseif (strcmp (operator, 'Prewitt'))
		operator = [1 1 1; 0 0 0; -1 -1 -1];
		
	elseif (strcmp (operator, 'Sobel'))
		operator = [1 2 1; 0 0 0; -1 -2 -1];
		
	else
		disp(sprintf('Operador "%s" no implementado.', operator)) 
		disp('Prueba con alguno de los disponibles:')
		disp('    Roberts, CentralDiff, Prewitt o Sobel')
	end

end