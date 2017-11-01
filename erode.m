% Operación de erosión morfológica
function outputImage = erode (inputImage, strElType, strElSize)

    % THIS IS A BLACK & WHITE EROSION!
    % BEHAVIOUR UNDEFINED WITH MORE THAN BLACK & WHITE SOURCES!

    kernel = uKernelMorfologicos (strElType, strElSize);
    %outputImage = uConvolve (inputImage, kernel, 'same', 'erode');
    %outputImage (find (outputImage)) = WHITE; % draw ones as WHITE

	% Se expande la imagen para tratar todos los puntos. Se rellena con px de 
	% valor cero para no contabilizar los nuevos px en la operación find posterior
	inputImageWithZeros = uExtendShrink (inputImage, kernel, 'extend', 0);
	
	% número de filas y columas de la imagen y el kernel
	[ir, ic] = size (inputImageWithZeros);
	
	% indices de la esquina superior izquierda de la imagen
	inputImageCoords = reshape([1:ir*ic], ir, ic);
	kernelIncr = inputImageCoords(1:strElSize, 1:strElSize);
	
	% puntos objeto en la imagen y kernel
	blancos_imagen = find(inputImageWithZeros);
	blancos_kernel = find (kernel);
	
	disp(sprintf('[erode] Px con valor 1 en la imagen: %d', numel(blancos_imagen)))
	disp(sprintf('[erode] Px con valor 1 en el kernel: %d', numel(blancos_kernel)))
	
	% Índices de los puntos a considerar si el kernel se coloca en la esquina
	% superior izquierda de la imagen
	kernelPuntos = kernelIncr(blancos_kernel);
	blancos_distancias =  blancos_imagen - median(kernelPuntos);
	
	% Extendenmos nuevamente la imagen, pero esta vez con unos. Se podría operar
	% con inputImageWithZeros. Utilizar una u otra va a causar diferencias en 
	% el resultado, según se haga erosión o dilatación.
	inputImageWithOnes = uExtendShrink (inputImage, kernel, 'extend', 1);
	
	% Inicializa variable de salida
	outputImage = inputImageWithOnes;
	
	for n = 1:numel(blancos_distancias)
		% Si se cambia inputImageWithOnes por inputImageWithZeros entonces se 
		% trabaja con el marco con valores 0
		outputImage(blancos_imagen(n)) = ...
			all ( inputImageWithOnes(kernelPuntos + blancos_distancias(n)) );
	end
	
	%outputImage = imerode (inputImageWithOnes, kernel); % check result 
	
	% Se encoge la imagen para devolver el mismo tamaño que a la entrada, sin
	% el marco utilizado para considerar px. de los bordes
	outputImage = uExtendShrink (outputImage, kernel, 'shrink', 0);
	
end