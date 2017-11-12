function bor = uSupresion (bordes, magnitud, direccion, dk, r, c)	

	% Vecinos de un punto en la dirección del gradiente
	function [v1 v2] = vecinos (coord, direccion, r, c)
		v1 = 0;
		v2 = 0;
	
		% obtiene los subindices para un punto pasado como índice linea en una
		% matriz de tamaño r,c
		[r_ c_] = ind2sub ([r c], coord);
		
		if (strcmp (direccion, 'H'))
			if (c_-1 > 0 & c_+1 <= c) 
				v1 = sub2ind([r c], r_, c_-1);
				v2 = sub2ind([r c], r_, c_+1);
			end
			
		elseif (strcmp (direccion, 'V'))
			if (r_-1 > 0 & r_+1 <= r)
				v1 = sub2ind([r c], r_-1, c_);
				v2 = sub2ind([r c], r_+1, c_);
			end
		
		elseif (strcmp (direccion, 'I'))
			if (r_+1 <= r & c_-1 > 0 & r_-1 > 0 & c_+1 <= c)
				v1 = sub2ind([r c], r_+1, c_-1);
				v2 = sub2ind([r c], r_-1, c_+1);
			end
		
		elseif (strcmp (direccion, 'D'))
			if (r_-1 > 0 & c_-1 > 0 & r_+1 <= r & c_+1 <= c)
				v1 = sub2ind([r c], r_-1, c_-1);
				v2 = sub2ind([r c], r_+1, c_+1);
			end
		end
	end		

	bor = bordes;
	count = 0;
	for elem = (find (dk))'
		[v1 v2] = vecinos (elem, direccion, r, c);
		if (v1 & v2)
			% Montamos un vector con las tres magnitudes
			v =  [magnitud(elem) magnitud(v1) magnitud(v2)]; % las 3 magnitudes
			
			% indice de la mayor de las magnitudes dentro del vector v: 1, 2 o 3
			max_index = find (v == max(v)); % encuentra el índice del mayor en el vector v
			
			% Si el Índice obtenido se corresponde con 1, que es el del punto actual,
			% enconces la comparación es true y vale 1, en caso contrario 0.
			% Por último multiplica ese 0 o 1 por el valor de la magnitud del
			% elemento actual, que es lo que se almacena
			if (numel (max_index) == 1)
				bor(elem) = (1 == max_index) * magnitud(elem); 
			else
				% Ojo puede haber 2 o tres elementos magnitudes iguales
				% detectamos si elem están entre ellas (caso de 2)
				if (ismember(max_index, 1))
					bor(elem) = magnitud(elem); 
				end
			end
		end
	end
end