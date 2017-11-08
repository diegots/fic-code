% Implementar el detector de bordes de Canny mediante una funci�n que permita 
% especificar el valor de sigma en el suavizado Gaussiano y los umbrales del proceso
% de hist�resis
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
		
		% �ltima comprobaci�n: el nuevo punto debe estar entre los porVisitar
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
	
		% obtiene los subindices para un punto pasado como �ndice linea en una
		% matriz de tama�o r,c
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
				max_index = find (v == max(v)); % encuentra el �ndice del mayor en el vector v
				
				% Si el índice obtenido se corresponde con 1, que es el del punto actual,
				% enconces la comparaci�n es true y vale 1, en caso contrario 0.
				% Por �ltimo multiplica ese 0 o 1 por el valor de la magnitud del
				% elemento actual, que es lo que se almacena
				if (numel (max_index) == 1)
					bor(elem) = (1 == max_index) * magnitud(elem); 
				else
					% Ojo puede haber 2 o tres elementos magnitudes iguales
					% detectamos si elem est�n entre ellas (caso de 2)
					if (ismember(max_index, 1))
						bor(elem) = magnitud(elem); 
					end
				end
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
	output = zeros (r,c);
	outputImage = zeros (r,c); % matriz de bordes resultante
	
	% Mejora de la imagen: suavizado
	smoothImage = gaussianFilter2D (inputImage, sigma);
	smoothImage = uExtendShrink (smoothImage, gaussKernel2D(sigma), operation, fillValue);
	
	% Localizaci�n de bordes empleando el detector de Sobel
	[gy, gx] = derivatives (smoothImage, 'Sobel');

	% Recorta los bordes bordes que se a�aden la convoluci�n tipo full
	gy = uExtendShrink (gy, operator, operation, fillValue);
	gx = uExtendShrink (gx, operator, operation, fillValue);

	Em = sqrt (gy.^2 + gx.^2); % Matriz de magnitudes
	
	% Matriz de orientaciones
	Eo = atan (gy ./ gx); % OjO: result in radians.
	Eo = radtodeg (Eo); % Cambia los valores de radianes a grados
	Eo = mod (Eo+360, 180); % Pasamos todos los �ngulos al primer y segundo cuadrantes
	
	% 4 grupos dk de orientaciones
	dk1 = (Eo>=0 & Eo<45);     % 0� to 45� -----> -
    dk2 = (Eo>=45 & Eo<90);   % 45� to 90� ----> /
    dk3 = (Eo>=90 & Eo<135);  % 90� to 135�  --> |
    dk4 = (Eo>=135 & Eo<180); % 135� to 180� --> \
	
	% supresi�n no m�xima - mirando s�lo los vecinos adyacentes de cada punto
	output = supresion (output, Em, 'V', dk1, r, c);
	output = supresion (output, Em, 'I', dk2, r, c);
	output = supresion (output, Em, 'H', dk3, r, c);
	output = supresion (output, Em, 'D', dk4, r, c);
	outputImage = output;
	% desactiva todos los puntos con valor inferior a tlow
	output(output<=tlow) = 0;
	
	% matriz de unos para los objetos que est�n entre tlow y thigh
	midHigh = (output > tlow);
	
	% num componentes conexas en la matriz label
	[labels, num] = bwlabel (midHigh, 8);
	disp(sprintf('[edgeCanny] n� de componentes conexas: %d', num))
	
	% matriz de tama�o r,c con unos donde hay thigh
	rangoAlto = output >= thigh;
	
	% para cada coordenada de thigh
	for phigh = find(output>thigh)
		for label = labels(phigh)' % etiquetas donde hay y thigh
			outputImage (find(labels == label)) = 1;
		end
	end
end