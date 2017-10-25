function outputImage = convolve (inputImage , kernel)

    % Nº de filas y columnas de la imagen original y el kernel
    [r, c] = size (inputImage);
    [kr, kc] = size (kernel);

    % Se le da la vuelta al kernel, para aplicarlo rotado, tal y como se 
    % considera en los apuntes
    kernel = reshape (kernel(end:-1:1), kr, kc);
	disp ( sprintf('[convolve] Kernel aplicado en la convolución: '))
	disp(kernel)

	% Matriz de indices de la imagen, desde 1 hasta r*c
    indexes = reshape([1:r*c],r,c);
	
	% Coordenadas de los puntos de la primera convolución
    firstMatConv = indexes ([1:kr],[1:kc]);
	firstMatConv = firstMatConv (1:end)';
	disp (sprintf('[convolve] Coordenadas de los puntos de la convolución inicial: '))
	disp (firstMatConv)
    
    % centers son las coordenadas de los puntos afectados por la convolución,
    % los puntos en la imagen original, SIN los bordes que no se pueden calcular.
    centers = indexes (ceil(kr/2):r-(floor(kr/2)), ceil(kc/2):c-(floor(kc/2)));
	disp(sprintf('[convolve] El primer elemento a convolucionar es el %d, el último %d', ...
		centers(1), centers(end)))
	    
    % número de valores que se calculan para la imagen y kernel dados
    numberOfCerters = numel (centers);
	disp(sprintf('[convolve] Nº total de puntos para calcular la convolución: %d', numberOfCerters)); 
    
	% A partir de la matriz general de indices, se obtiene otra matriz con 
	% los incrementos que hay que sumar a firstMatConv para obtener las 
	% coordenadas de cada punto
	increments = (indexes (1:r-kr+1, 1:c-kc+1) - 1);
	[ir,ic] = size(increments);
	disp(sprintf('[convolve] Tamaño de la matriz de incrementos (area de convolución): %dx%d', ir,ic))
	increments = increments (1:end); % ponemos la matriz en una única fila
	
	% Se repite la submatriz de convolución original tantas veces como número 
	% de centros o píxeles que hay que calcular.
    matConvolutions = repmat (firstMatConv, 1, numberOfCerters);
	
	% A las submatrices base se le suman cada incremento
	%AA = bsxfun(@minus,A,b) where b is the vector and A is your big matrix
	matConvolutions = bsxfun(@plus, matConvolutions,increments);
	
	% Se necesita tantos kernels como submatrices en matConvolutions
	kernel_ = kernel (1:end)';
	allKernels = repmat (kernel_,1,numberOfCerters);
    
	% Cálculo del producto y las sumas de la convolución
    matConvolutions = inputImage (matConvolutions) .* allKernels;
    matConvolutions = sum (matConvolutions, 1);
    
	% Crea una imagen vacía para guardar los resultados
	outputImage = uInitializeImage (inputImage);
    
	% Asigna a los píxeles de la convolución su nuevo valor
	outputImage(centers) = matConvolutions;
	
end