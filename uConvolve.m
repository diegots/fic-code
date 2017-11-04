% uConvolve recibe como parámetros:
% inputImage: la imagen sobre la cual hacer la convolución
% kernel: a utilizar en la convolución en formato MxN
% shape: al igual que conv2 se puede calcular la convolución 'full' que 
% 	convoluciona la imagen y crea una imagen más grande para poder incluir los 
% 	bordes o bien 'same' que devuelve el mismo tamaño de imagen.
% operation: puede ser '2dconvo' para realizar la convolución normal o 'median'
%	para aplicar un filtro de medianas
function outputImage = uConvolve (inputImage, kernel, shape, operation)

	FILLVALUE = 1;

	% Nº de filas y columnas de la imagen original y el kernel
    [r, c] = size (inputImage);
    [kr, kc] = size (kernel);
	disp(sprintf('[uConvolve] Tamaño del a imagen original: %dx%d', r, c))

	
    % Se le da la vuelta al kernel, para aplicarlo rotado, tal y como se 
    % considera en los apuntes
    kernel = reshape (kernel(end:-1:1), kr, kc);
	disp ( sprintf('[uConvolve] Kernel aplicado en la convolución: '))
	disp(kernel)

	
	% En caso de querer hacer la convolución de tipo 'full' hay que agrandar la 
	% imagen de origen con un borde de valor 0 y luego situar la imagen en el 
	% centro
	if (strcmp (shape, 'full'))
		inputImage = uExtendShrink(inputImage, kernel, 'extend', FILLVALUE);
		[r,c] = size(inputImage);
		outputImage = inputImage;
	else
		% Crea una imagen vacía para guardar los resultados
		outputImage = uInitializeImage (inputImage, FILLVALUE);
	end

	
	% Matriz de indices para toda la imagen original, desde 1 hasta r*c
    indexes = reshape([1:r*c],r,c);
	
	
	% Coordenadas de los puntos de la primera convolución
    firstMatConv = indexes ([1:kr],[1:kc]);
	% Para que la implementación funcione, firstMatConv siempre deben tener 
	% formato columna
	firstMatConv = firstMatConv (1:end)';
	[r_,c_] = size(firstMatConv);
	if (r_ == 1) % Si hay sólo una columna, se traspone la matriz
		disp('[uConvolve] Cambiando firstMatConv a formato columna')
		firstMatConv = firstMatConv';
	end
	disp (sprintf('[uConvolve] Coordenadas de los puntos de la convolución inicial: '))
	disp (firstMatConv)
    

    % centers son las coordenadas de los puntos afectados por la convolución,
    % los puntos en la imagen original, SIN los bordes que no se pueden calcular.
	% Se consideran kernels de lado impar o par para las filas y/o columnas.
    if (mod(kr,2)) % rows impares
		center_rows = ceil(kr/2) : r-(floor(kr/2));
	else % rows pares
		center_rows = (kr/2)+1 : r-(kr/2)+1;
	end
	
	if (mod(kc,2)) % cols impares
		center_cols = ceil(kc/2) : c-(floor(kc/2));
	else % cols pares
		center_cols = kc/2 : c-(kc/2);
	end
	
	centers = indexes (center_rows, center_cols);
	disp(sprintf('[uConvolve] El primer elemento a convolucionar es el %d, el último %d', ...
		centers(1), centers(end)))
	    
    % número de valores que se calculan para la imagen y kernel dados
    numberOfCerters = numel (centers);
	disp(sprintf('[uConvolve] Nº total de puntos para calcular la convolución: %d', numberOfCerters)); 

    
	% A partir de la matriz general de indices, se obtiene otra matriz con 
	% los incrementos que hay que sumar a firstMatConv para obtener las 
	% coordenadas de cada punto
	increments = (indexes (1:r-kr+1, 1:c-kc+1) - 1);
	[ir,ic] = size(increments);
	disp(sprintf('[uConvolve] Tamaño de la matriz de incrementos (area de convolución): %dx%d', ir,ic))
	increments = increments (1:end); % ponemos la matriz en una única fila
	
	% Se repite la submatriz de convolución original tantas veces como número 
	% de centros o píxeles que hay que calcular.
    matConvolutions = repmat (firstMatConv, 1, numberOfCerters);

	
	% A las submatrices base se le suman cada incremento
	%AA = bsxfun(@minus,A,b) where b is the vector and A is your big matrix
	matConvolutionsIncr = bsxfun(@plus, matConvolutions,increments);
	
	% Se necesita tantos kernels como submatrices en bsxfunOutput
	kernel_ = kernel (1:end)';
	%Se espera que el kernel esté el columna
	[r_,c_] = size(kernel_);
	if (r_ == 1) % Si hay sólo una columna, se traspone la matriz
		disp('[uConvolve] Cambiando kernel_ a formato columna')
		kernel_ = kernel_';
	end
	
	
	% Operación de convolución 2D habitual: produtos y sumas
	if (strcmp (operation, '2dconvo'))
		allKernels = repmat (kernel_,1,numberOfCerters);
		
		% Cálculo del producto y las sumas de la convolución
		matConvolutionsPrd = inputImage (matConvolutionsIncr) .* allKernels;
		matConvolutionsOut = sum (matConvolutionsPrd, 1);	

	% Operación de filtrado de mediana sobre los vecinos de los puntos centros
	elseif (strcmp (operation, 'median'))
		
		matConvolutionsPnt = inputImage (matConvolutionsIncr);
		matConvolutionsSrt = sort(matConvolutionsPnt); % ordena cada columna
		disp('[uConvolve] Valores de la primera ventana ordenados:')
		disp(reshape(matConvolutionsSrt(:,1),kr,kc))
		
		kernelNumElements = numel(kernel);
		if (mod(kr,2) == 0) % kernel de lado par
			disp(sprintf('[uConvolve] Kernel de lado par. Se toma el valor inferior del par central (%dº) para la mediana: %6.4f', ...
				kernelNumElements/2, matConvolutionsSrt(kernelNumElements/2, 1)))
			matConvolutionsOut = matConvolutionsSrt(kernelNumElements/2,: );

		else % kernel de lado impar
			disp(sprintf('[uConvolve] Kernel de lado impar. Se toma el valor central (%dº) para la mediana: %6.4f', ...
				ceil(kernelNumElements/2), matConvolutionsSrt(ceil(kernelNumElements/2), 1)))
			matConvolutionsOut = matConvolutionsSrt( ceil(kernelNumElements/2),: );
		end
	end
	
	% Asigna a los píxeles de la convolución su nuevo valor
	outputImage(centers) = matConvolutionsOut;
	
end