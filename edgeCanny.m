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
			
			%if (actual == 1799)
			%	disp('Actual 1799 y estoy en V')
			%end
			if (r_+1 <= r)
				sig = sub2ind([r c], r_+1, c_);
			%	if (actual == 1799)
			%		disp(sprintf('despues de 1799 --> %d', sig))
			%	end

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
			%if (actual == 1799)
			%	disp(sprintf('No me lo cargo en isMember'));
			%end
			;
		else
			%if (actual == 1799)
			%	ismember(porVisitar,sig)
			%	disp(sprintf('ME lo cargo en isMember'));
			%end
			sig = 0;
		end
		
		restantes = porVisitar;
	end

	function bor = afinaBorde (bordes, magnitud, direccion, dk, r,c)
		%if (strcmp (direccion, 'V'))
		%	porVisitar = find (dk)
		%end
		
		numEvit = 0;
		numbor = 0;
		bor = bordes;
		porVisitar = find (dk); % indices de los puntos para visitar en dk
		
		while (porVisitar)
			actual = porVisitar(1);
			%	if (actual == 1800)
			%		disp(sprintf('Me lo cargo en el bucle principal: %d', actual))
			%	end
			porVisitar(1) = [];
			mayor = actual;
			viaja = actual;

			while (viaja)
			%	if (viaja == 1799)
			%		disp(sprintf('viaja es 1799'))
			%	end
				if (magnitud(viaja) > magnitud(mayor))
					mayor = viaja;
				end
			%	if (viaja == 1799)
			%		[tmp porVisitar] = siguiente (viaja, direccion, porVisitar, r, c);
			%		disp(sprintf('antes %s, despues de 1799 -> %d', direccion, tmp))
			%		viaja = tmp;
			%	end
				[viaja porVisitar] = siguiente (viaja, direccion, porVisitar, r, c);
			%	if (viaja == 1799)
			%		disp(sprintf('despues de 1799 -> %d', viaja))
			%	end
				if (viaja)
				%	numEvit = numEvit+1;
				%	if (numel(porVisitar)<viaja)
				%		disp('porVisitar tiene menos que el valor de viaja')
				%	end
					p = find(porVisitar==viaja);
					porVisitar (p) = [];
				end
			end
			
			bor(mayor) = magnitud(mayor);
		%	numbor = numbor + 1;
		end
		%disp(sprintf('Puntos borde: %d', numbor))
		%disp(sprintf('Evitados: %d', numEvit))
	end

	% Inicializaciones
	operator = uSpecial('Sobel');
	operation = 'shrink';
	fillValue = 0;
	[r,c] = size (inputImage);
	Eo = zeros (r,c); % Inicializa Eo
	Em = zeros (r,c); % Inicializa Em
	outputImage = zeros (r,c); % matriz de bordes resultante
	
	% 1. Mejora de la imagen: suavizado
	smoothImage = gaussianFilter2D (inputImage, sigma);
	smoothImage = uExtendShrink (smoothImage, gaussKernel2D(sigma), operation, fillValue);
	
	% Localización de bordes empleando el detector de Sobel
	[gy, gx] = derivatives (smoothImage, 'Sobel');

	% Recorta los bordes bordes que se añaden la convolución tipo full
	gy = uExtendShrink (gy, operator, operation, fillValue);
	gx = uExtendShrink (gx, operator, operation, fillValue);

	Em = sqrt (gy.^2 + gx.^2); % Magnitud
	
	% Orientación
	Eo = atan (gy ./ gx); % OjO: result in radians.
	Eo = radtodeg (Eo); % Cambia los valores de radianes a grados
	Eo = mod (Eo+360, 180); % Pasamos todos los ángulos al primer y segundo cuadrantes
	
	% 2. Supresión no máxima, objetivo bordes de 1 px de grosor
	
	% 4 grupos dk de orientaciones
	dk1 = (Eo>=0 & Eo<45);     % 0º to 45º -----> -
    dk2 = (Eo>=45 & Eo<90);   % 45º to 90º ----> /
    dk3 = (Eo>=90 & Eo<135);  % 90º to 135º  --> |
    dk4 = (Eo>=135 & Eo<180); % 135º to 180º --> \
	
	%disp(sprintf('En dk1: %d', numel(find(dk1))))
	outputImage = afinaBorde (outputImage, Em, 'V', dk1, r, c);
	outputImage = afinaBorde (outputImage, Em, 'D', dk2, r, c);
	outputImage = afinaBorde (outputImage, Em, 'H', dk3, r, c);
	outputImage = afinaBorde (outputImage, Em, 'I', dk4, r, c);
	%

	
	
	% umbralización por 
	
	
end