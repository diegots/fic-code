function outputImage = dilate (inputImage, strElType, strElSize, type)

    % THIS IS A BLACK & WHITE DILATE!
    % BEHAVIOUR UNDEFINED WITH MORE THAN BLACK & WHITE SOURCES!

    kernel = uKernelMorfologicos (strElType, strElSize);

	% Se expande la imagen para tratar todos los puntos. Se rellena con px de 
	% valor cero para no contabilizar los nuevos px en la operación find posterior
	inputImageWithOnes = uExtendShrink (inputImage, kernel, 'extend', 1);
	
	% número de filas y columas de la imagen y el kernel
	[ir, ic] = size (inputImageWithOnes);
	
	% indices de la esquina superior izquierda de la imagen
	inputImageCoords = reshape([1:ir*ic], ir, ic);
	kernelIncr = inputImageCoords(1:strElSize, 1:strElSize);
	
	% puntos objeto en la imagen y kernel
	negros_imagen = find (inputImageWithOnes <= 0);
	blancos_kernel = find (kernel);
	
	disp(sprintf('[dilate] Px con valor 1 en la imagen: %d', numel(negros_imagen)))
	disp(sprintf('[dilate] Px con valor 1 en el kernel: %d', numel(blancos_kernel)))
	
	% Índices de los puntos a considerar si el kernel se coloca en la esquina
	% superior izquierda de la imagen
	kernelPuntos = kernelIncr(blancos_kernel);
	negros_distancias =  negros_imagen - median(kernelPuntos);
	
	% Extendenmos nuevamente la imagen, pero esta vez con unos. Se podría operar
	% con inputImageWithZeros. Utilizar una u otra va a causar diferencias en 
	% el resultado, según se haga erosión o dilatación.
	inputImageWithZeros = uExtendShrink (inputImage, kernel, 'extend', 0);
	
	% Inicializa variable de salida
	outputImage = inputImageWithZeros;
	
	for n = 1:numel(negros_distancias)
		% Si se cambia inputImageWithOnes por inputImageWithZeros entonces se 
		% trabaja con el marco con valores 0
		outputImage(negros_imagen(n)) = ...
			any ( inputImageWithZeros(kernelPuntos + negros_distancias(n)) );
	end
	
	%outputImage = imdilate (inputImageWithZeros, kernel); % check result 
	
	% Se encoge la imagen para devolver el mismo tamaño que a la entrada, sin
	% el marco utilizado para considerar px. de los bordes
	outputImage = uExtendShrink (outputImage, kernel, 'shrink', 0);

end