% Implementar el detector de bordes de Canny mediante una función que permita 
% especificar el valor de σ en el suavizado Gaussiano y los umbrales del proceso
% de histéresis
function outputImage = edgeCanny (inputImage, sigma, tlow, thigh)

	function [sig restantes] = siguiente(actual, direccion, porVisitar, r, c)

		[r_ c_] = ind2sub ([r c], actual);
		sig = 0;
	
		if (strcmp (direccion, 'H'))
			if (c_+1 <= c)
				sig = sub2ind([r c], r_, c_+1);
			end
		
		elseif (strcmp (direccion, 'V'))
			if (r_+1 <= r)
				sig = sub2ind([r c], r_+1, c_);
			end

		elseif (strcmp (direccion, 'I'))
			if (r_-1>0 & c_+1<=c)
				sig = sub2ind([r c], r_-1, c_+1);
			end

		elseif (strcmp (direccion, 'D'))
			if (r_+1<=r & c_+1<=c)
				sig = sub2ind([r c], r_+1, c_+1);
			end
		end
		
		% Última comprobación: el nuevo punto debe estar entre los porVisitar
		if (find(ismember(porVisitar,sig)))
			;
		else
			sig = 0;
		end
		
		restantes = porVisitar;
	end

	function bor = afinaBorde (bordes, magnitud, direccion, dk, r,c)
		
		numEvit = 0;
		numbor = 0;
		bor = bordes;
		porVisitar = find (dk); % indices de los puntos para visitar en dk
		
		while (porVisitar)
			actual = porVisitar(1);
			porVisitar(1) = [];
			mayor = actual;
			viaja = actual;

			while (viaja)
				if (magnitud(viaja) > magnitud(mayor))
					mayor = viaja;
				end
				[viaja porVisitar] = siguiente (viaja, direccion, porVisitar, r, c);
				if (viaja)
					p = find(porVisitar==viaja);
					porVisitar (p) = [];
				end
			end
			
			bor(mayor) = magnitud(mayor);
		end
	end

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

	function bor = supresion (bordes, magnitud, direccion, dk, r, c)	
		bor = bordes;
		count = 0;
		for elem = (find (dk))'
			[v1 v2] = vecinos (elem, direccion, r, c);
			if (v1 & v2)
				% Montamos un vector con las tres magnitudes
				v =  [magnitud(elem) magnitud(v1) magnitud(v2)]; % las 3 magnitudes
				
				% indice de la mayor de las magnitudes dentro del vector v: 1, 2 o 3
				max_index = find (v == max(v)); % encuentra el índice del mayor en el vector v
				
				% Si el índice obtenido se corresponde con 1, que es el del punto actual,
				% enconces la comparación es true y vale 1, en caso contrario 0.
				% Por último multiplica ese 0 o 1 por el valor de la magnitud del
				% elemento actual, que es lo que se almacena
				bor(elem) = (1 == max_index) * magnitud(elem); 
			end
		end
	end
	
	% Inicializaciones
	operator = uSpecial('Sobel');
	operation = 'shrink';
	fillValue = 0;
	[r,c] = size (inputImage);
	Eo = zeros (r,c); % Inicializa Eo
	Em = zeros (r,c); % Inicializa Em
	outputImage = zeros (r,c); % matriz de bordes resultante
	
	% Mejora de la imagen: suavizado
	smoothImage = gaussianFilter2D (inputImage, sigma);
	smoothImage = uExtendShrink (smoothImage, gaussKernel2D(sigma), operation, fillValue);
	
	% Localización de bordes empleando el detector de Sobel
	[gy, gx] = derivatives (smoothImage, 'Sobel');

	% Recorta los bordes bordes que se añaden la convolución tipo full
	gy = uExtendShrink (gy, operator, operation, fillValue);
	gx = uExtendShrink (gx, operator, operation, fillValue);

	Em = sqrt (gy.^2 + gx.^2); % Matriz de magnitudes
	
	% Matriz de orientaciones
	Eo = atan (gy ./ gx); % OjO: result in radians.
	Eo = radtodeg (Eo); % Cambia los valores de radianes a grados
	Eo = mod (Eo+360, 180); % Pasamos todos los ángulos al primer y segundo cuadrantes
	
	% 4 grupos dk de orientaciones
	dk1 = (Eo>=0 & Eo<45);     % 0º to 45º -----> -
    dk2 = (Eo>=45 & Eo<90);   % 45º to 90º ----> /
    dk3 = (Eo>=90 & Eo<135);  % 90º to 135º  --> |
    dk4 = (Eo>=135 & Eo<180); % 135º to 180º --> \
	
	% supresión no máxima - mirando sólo los vecinos de cada punto
	outputImage = supresion (outputImage, Em, 'V', dk1, r, c);
	outputImage = supresion (outputImage, Em, 'I', dk2, r, c);
	outputImage = supresion (outputImage, Em, 'H', dk3, r, c);
	outputImage = supresion (outputImage, Em, 'D', dk4, r, c);
	
	% umbralización por histéresis
	
	
	
end