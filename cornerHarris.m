% Implementar el detector de esquinas de Harris que utilice Gaussianas tanto 
% para la diferenciación como para la integración. La función permitirá 
% establecer la escala de diferenciación sigmaD, la escala de integración 
% sigmaI, y el valor del umbral para esquinas (t)
% https://dsp.stackexchange.com/questions/10483/what-are-integration-scale-and-differentiation-scale
% http://www.cs.cornell.edu/courses/cs4670/2015sp/lectures/lec07_harris_web.pdf
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

	
	[r c] = size(inputImage);
	
	k = 0.04;
	dx = uSpecial ('CentralDiff');
	kernelD = gaussKernel2D (sigmaD);
	kernelI = gaussKernel2D (sigmaI);
	
	Bx = gaussianFilter2D (inputImage, sigmaD);
	Bx = uExtendShrink (Bx, kernelD, 'shrink', 0);
	
	By = gaussianFilter2D (inputImage, sigmaD);
	By = uExtendShrink (By, kernelD, 'shrink', 0);

	Lx = conv2(Bx, dx, 'same');  
	Ly = conv2(By, dx', 'same'); 

	Lx2 = Lx.^2;
	Ly2 = Ly.^2;
	Lxy = Lx .* Ly;

	Mx = sigmaD^2 .* gaussianFilter2D (Lx2, sigmaI);
	Mx = uExtendShrink (Mx, kernelI, 'shrink', 0);
	
	My = sigmaD^2 .* gaussianFilter2D (Ly2, sigmaI);
	My = uExtendShrink (My, kernelI, 'shrink', 0);
	
	Mxy = sigmaD^2 .* gaussianFilter2D (Lxy, sigmaI);
	Mxy = uExtendShrink (Mxy, kernelI, 'shrink', 0);
	
	R = (Mx .* My - Mxy.^2) - (k * (Mx + My).^2);
	
	% Descarta los valores que no superen el umbral
	outputImage = find(R>t);

%	% Supresión no máxima
%	Em = sqrt (gy.^2 + gx.^2); % Matriz de magnitudes
%	
%	% Matriz de orientaciones
%	Eo = atan (gy ./ gx); % OjO: result in radians.
%	Eo = radtodeg (Eo); % Cambia los valores de radianes a grados
%	Eo = mod (Eo+360, 180); % Pasamos todos los ángulos al primer y segundo cuadrantes
%	
%	% se reparten las orientaciones de los bordes en 4 grupos
%	dk1 = (Eo>=0 & Eo<45);    % 0º to 45º -----> - y /
%    dk2 = (Eo>=45 & Eo<90);   % 45º to 90º ----> / y |
%    dk3 = (Eo>=90 & Eo<135);  % 90º to 135º  --> | y \
%    dk4 = (Eo>=135 & Eo<180); % 135º to 180º --> \ y -
%	
%	% supresión no máxima - mirando sólo los vecinos adyacentes de cada punto
%	output = uSupresion (output, Em, 'V', dk1, r, c);
%	output = uSupresion (output, Em, 'D', dk2, r, c);
%	output = uSupresion (output, Em, 'H', dk3, r, c);
%	output = uSupresion (output, Em, 'I', dk4, r, c);	
%	
%	%function bor = uSupresion (bordes, magnitud, direccion, dk, r, c)	
%	outputImage = uSupresion ();
	
	
	% Muestra resultado
	figure(8)
	imshow(inputImage)
	hold on
	
	for e = outputImage'
		[i,j] = ind2sub([r c], e);
		disp(sprintf('[cornerHarris] e:%d -> (i:%d, j:%d)', e, i, j))
		plot ([j], [i], '*')
		%plot([0 10],[0 9],'+','LineWidth',20,'MarkerSize',50)
	end
	
	
	
	

end

%% información: https://technicache.wordpress.com/2010/11/24/harris-corner-detector-in-matla/
%% Implementación del detector de esquinas Harris
%% N : tamaño de la zona de vecindad
%% t : valor del umbral para las esquinas
%function outputImage = cornerHarris (inputImage, N, t)
%    k = 0.04; % entre 0.04 y 0.06
%
%    [r,c] = size (inputImage);
%
%    % derivatives
%    %gx = [-1 0; 0 1];
%    %gy = [0 -1; 1 0];
%
%    gx = fspecial ('sobel', 3);
%    gy = fspecial ('sobel', 3)';
%
%    ix = uConvolutionGeneral (inputImage, gx);
%    iy = uConvolutionGeneral (inputImage, gy);
%
%    mat = double (inputImage);
%    [r, c] = size (mat);
%    kr = N;
%    kc = N;
%
%    indexes = reshape(1:r*c,r,c);
%    firstMatConv = indexes (1:kr,1:kc)(:);
%    centers = indexes (ceil(kr/2):r-(floor(kr/2)), ceil(kc/2):c-(floor(kc/2)));
%    numberOfCerters = numel (centers);
%    increments = (indexes (1:r-ceil(kr/2), 1:c-ceil(kc/2)) - 1)(:)';
%    matConvolutions = repmat (firstMatConv, 1, numberOfCerters)+increments;
%
%    ix2 = sum(ix(matConvolutions).^2, 1);
%    iy2 = sum(iy(matConvolutions).^2, 1);
%    ixiy = sum(ix(matConvolutions) .* iy(matConvolutions), 1);
%
%    v = 1:numberOfCerters;
%    S = reshape ([ix2(v); ixiy(v); ixiy(v); iy2(v)],2,2,numberOfCerters);
%
%    % create objects
%    dets = zeros(1,numberOfCerters);
%    traces = zeros(1,numberOfCerters);
%    M = zeros (1,numberOfCerters);
%    
%    for i = v
%        s_ = S(:,:,i);
%        dets(i) = det (s_);
%        traces(i) = trace (s_) ^ 2;
%
%        %M(i) = det (S(:,:,i)) - k * (trace (S(:,:,i)) ^ 2);
%    endfor
%    M(v) = dets(v) - k * traces(v);
%
%    M = reshape(M, r-(floor(kr/2)*2), c-floor(kc/2)*2);
%    coners_ = find (M>t);
%    corners = centers (coners_); % corners detected. Absolute indexes
%
%    % Non-max suppression
%    a = atan (iy./ix);
%    nan_pos = isnan(a); % atan calculus produces some NaN values on the matrix borders
%    a(nan_pos) = 0; % removes those NaN values
%    Eo = mod((180 * a / pi) + 360, 180); % orientation matrix
%    Em = sqrt (ix.^2 .+ iy.^2); % magnitude matrix
%
%    d1 = (Eo>0 & Eo<45);    % 0º to 45º 
%    d2 = (Eo>=45 & Eo<90);  % 45º to 90º 
%    d3 = (Eo>=90 & Eo<135); % 90º to 135º 
%    d4 = (Eo>=135);         % 135º to 180º
%
%    cd1 = intersect (corners, find(d1));
%    cd1( Em(cd1)<Em(cd1+1) ) = []; 
%    cd1( Em(cd1)<Em(cd1-1) ) = []; 
%
%    cd2 = intersect (corners, find(d2));
%    cd2( Em(cd2)<Em(cd2-r-1) ) = [];
%    cd2( Em(cd2)<Em(cd2+r+1) ) = [];
%
%    cd3 = intersect (corners, find(d3));
%    cd3( Em(cd3)<Em(cd3-r) ) = [];
%    cd3( Em(cd3)<Em(cd3+r) ) = [];
%
%    cd4 = intersect (corners, find(d4));
%    cd4( Em(cd4)<Em(cd4-r+1) ) = [];
%    cd4( Em(cd4)<Em(cd4+r-1) ) = [];
%
%    corners = union (cd1, cd2);
%    corners = union (corners, cd3);
%    corners = union (corners, cd4);
%
%    function [y,x] = get_relative_indexes (rows,cols,points)
%        % Get corners coordinates (x,y)
%        p = zeros (rows,cols);
%        p(points) = 1;
%        [y,x] = find(p);
%    endfunction
%
%    % Show results
%    [y,x] = get_relative_indexes (r,c,corners);
%    printf('Detected points: %s\n', num2str(numel(corners)));
%    [v0,v1] = drawCircle (x,y,3);
%    %figure (9,'visible','off')
%    figure (9);
%    imshow(inputImage)
%    hold on
%    %plot(v0,v1,"color","r","-");
%    plot(x,y,"color","r","*");
%    hold off
%    print (gcf(), "outputImage.png", '-dpng')
%
%    outputImage = inputImage;
%
%    function [v0,v1] = drawCircle (x, y, r)
%
%        t = linspace(0,2*pi,30)';
%        cos_x = r * cos(t);
%        sin_y = r * sin(t);
%
%        e = numel (x); % let's repeat t
%        cos_x_rep = repmat (cos_x,1,e);
%        sin_y_rep = repmat (sin_y,1,e);
%
%        v0 = cos_x_rep + x';  
%        v1 = sin_y_rep + y';
%
%    endfunction
%
%endfunction
%
%