% Implementar el detector de bordes de Canny mediante una funci�n que permita 
% especificar el valor de sigma en el suavizado Gaussiano y los umbrales del proceso
% de hist�resis
function outputImage = edgeCanny (inputImage, sigma, tlow, thigh)
	
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
	% gy son bordes verticales, gradiente horizontal
	% gx son bordes horizontales, gradiente vertical
	[gy, gx] = derivatives (smoothImage, 'Sobel');

	% Recorta los bordes bordes que se a�aden la convoluci�n tipo full
	gy = uExtendShrink (gy, operator, operation, fillValue);
	gx = uExtendShrink (gx, operator, operation, fillValue);

	Em = sqrt (gy.^2 + gx.^2); % Matriz de magnitudes
	
	% Matriz de orientaciones
	Eo = atan (gy ./ gx); % OjO: result in radians.
	Eo = radtodeg (Eo); % Cambia los valores de radianes a grados
	Eo = mod (Eo+360, 180); % Pasamos todos los �ngulos al primer y segundo cuadrantes
	
	% se reparten las orientaciones de los bordes en 4 grupos
	dk1 = (Eo>=0 & Eo<45);     % 0� to 45� -----> - y /
    dk2 = (Eo>=45 & Eo<90);   % 45� to 90� ----> / y |
    dk3 = (Eo>=90 & Eo<135);  % 90� to 135�  --> | y \
    dk4 = (Eo>=135 & Eo<180); % 135� to 180� --> \ y -
	
	% supresi�n no m�xima - mirando s�lo los vecinos adyacentes de cada punto
	output = uSupresion (output, Em, 'V', dk1, r, c);
	output = uSupresion (output, Em, 'D', dk2, r, c);
	output = uSupresion (output, Em, 'H', dk3, r, c);
	output = uSupresion (output, Em, 'I', dk4, r, c);
	
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