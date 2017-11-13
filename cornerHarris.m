% Implementar el detector de esquinas de Harris que utilice Gaussianas tanto 
% para la diferenciación como para la integración. La función permitirá 
% establecer la escala de diferenciación sigmaD, la escala de integración 
% sigmaI, y el valor del umbral para esquinas (t)
% https://dsp.stackexchange.com/questions/10483/what-are-integration-scale-and-differentiation-scale
% http://www.cs.cornell.edu/courses/cs4670/2015sp/lectures/lec07_harris_web.pdf
% https://www.youtube.com/watch?v=9-F5-gUIAWE
function outputImage = cornerHarris (inputImage, sigmaD, sigmaI, t)
% Algoritmo
% => Montar la matriz S:
%     a. Obtener las derivadas en cada eje con un operador tipo centralDiff
%     b. Suavizar las derivadas con el kernel sigmaD
%     c. Obtener las 4 componentes de la matriz S (producto y cuadrados)
%     d. Aplicar un filtro Gaussiano con sigmaI las componentes obtenidas antes
%     e. Calcular el producto de sigmaD^2 y el resultado anterior
% => Determinante y traza
%     a. considerar un k = [0.04,0.06]
%     b. Para cada ventana se tiene la matriz S creada anteriormente
%        Obtener M(r,c) = det(S(r,c)) - k*traza(S(r,c))^2
% => Descartar puntos que no superan el umbral
%     a. Aplicar M = M(M>t)
% => Obtener los máximos locales del vecindario con una función. Supresión no 
%    máxima
% => Una vez se tiene la matriz final hay que representarla para verla
%    plot(0,0,'+','LineWidth',250,'MarkerSize',100)
%    plot([0 10],[0 9],'+','LineWidth',20,'MarkerSize',50)

	%
	%
	%
	% Inicializaciones
	[r c] = size(inputImage);
	output_ = zeros (r,c); % variable para realizar la supresión
	output = zeros (r,c);
	outputImage = zeros (r,c);
	k = 0.04; % constante del algoritmo de Harris
	dx = uSpecial ('CentralDiff');
	kernelD = gaussKernel2D (sigmaD);
	kernelI = gaussKernel2D (sigmaI);
	
	%
	% Obtención de los puntos relevantes mediante la matriz de Harris
	%
	
	% Se aplica filtro gaussiano con sigmaD a las imágenes
	Bx = gaussianFilter2D (inputImage, sigmaD);
	Bx = uExtendShrink (Bx, kernelD, 'shrink', 0);
	
	By = gaussianFilter2D (inputImage, sigmaD);
	By = uExtendShrink (By, kernelD, 'shrink', 0);

	% derivadas obtenidas con el kernel dx
	%Lx = conv2(Bx, dx, 'same');  
	%Ly = conv2(By, dx', 'same'); 
	Lx = convolve (Bx , dx, 'same');
	Ly = convolve (By , dx', 'same');

	% componentes de la matriz de Harris
	Lx2 = Lx.^2;
	Ly2 = Ly.^2;
	Lxy = Lx .* Ly;

	% Aplicación de la Gaussiana con sigmaI a los elementos de la matriz de 
	% Harris
	Mx = sigmaD^2 .* gaussianFilter2D (Lx2, sigmaI);
	Mx = uExtendShrink (Mx, kernelI, 'shrink', 0);
	
	My = sigmaD^2 .* gaussianFilter2D (Ly2, sigmaI);
	My = uExtendShrink (My, kernelI, 'shrink', 0);
	
	Mxy = sigmaD^2 .* gaussianFilter2D (Lxy, sigmaI);
	Mxy = uExtendShrink (Mxy, kernelI, 'shrink', 0);
	
	% Respuesta del detector: R = det(H) - k*tr(H)^2
	R = (Mx .* My - Mxy.^2) - (k * (Mx + My).^2);
	disp(sprintf('[cornerHarris] nº de puntos en R: %d', numel(R)))
	% Descarta los valores que no superen el umbral t
	puntos = find(R>t);
	disp(sprintf('[cornerHarris] nº de puntos en R que superan t: %d', numel(puntos)))
	
	% La supresión se lleva a cabo sobre output_
	output_(puntos) = inputImage(puntos);
	
	%
	% Supresión no máxima
	%
	% Aplicamos supresión no máxima sobre las "nubes" de puntos
	% para obtener un máximo local
	operator = uSpecial('Sobel');
	
	% derivadas
	[gy, gx] = derivatives (output_, 'Sobel');

	% Recorta los bordes bordes que se añaden la convolución tipo full
	gy = uExtendShrink (gy, operator, 'shrink', 0);
	gx = uExtendShrink (gx, operator, 'shrink', 0);

	Em = sqrt (gy.^2 + gx.^2); % Matriz de magnitudes
	
	% Matriz de orientaciones
	Eo = atan (gy ./ gx); % OjO: result in radians.
	Eo = radtodeg (Eo); % Cambia los valores de radianes a grados
	Eo = mod (Eo+360, 180); % Pasamos todos los ángulos al primer y segundo cuadrantes
	
	% se reparten las orientaciones de los bordes en 4 grupos
	dk1 = (Eo>=0 & Eo<45);     % 0º to 45º -----> - y /
    dk2 = (Eo>=45 & Eo<90);   % 45º to 90º ----> / y |
    dk3 = (Eo>=90 & Eo<135);  % 90º to 135º  --> | y \
    dk4 = (Eo>=135 & Eo<180); % 135º to 180º --> \ y -
	
	% supresión no máxima - mirando sólo los vecinos adyacentes de cada punto
	output = uSupresion (output, Em, 'V', dk1, r, c);
	output = uSupresion (output, Em, 'D', dk2, r, c);
	output = uSupresion (output, Em, 'H', dk3, r, c);
	output = uSupresion (output, Em, 'I', dk4, r, c);
	
	disp(sprintf('[cornerHarris] nº de puntos después de la supresión: %d', numel(find(output))))
	outputImage = output; % resultado final
	
%	%
%	% Visualiza los resultados (depuración Harris)
%	% 
%	figure(8)
%	imshow(inputImage)
%	hold on
%
%	%for e = find(outputImage(1:end))
%	for e = puntos' % todos los valores dados por Harris
%		[i,j] = ind2sub([r c], e);
%		%disp(sprintf('[cornerHarris] e:%d -> (i:%d, j:%d)', e, i, j))
%		plot ([j], [i], '*')
%		%plot([0 10],[0 9],'+','LineWidth',20,'MarkerSize',50)
%	end

end