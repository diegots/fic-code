% Operación de erosión morfológica
function outputImage = erode (inputImage, strElType, strElSize)

    % THIS IS A BLACK & WHITE EROSION!
    % BEHAVIOUR UNDEFINED WITH MORE THAN BLACK & WHITE SOURCES!

    kernel = uKernelMorfologicos (strElType, strElSize);
    %outputImage = uConvolve (inputImage, kernel, 'same', 'erode');
    %outputImage (find (outputImage)) = WHITE; % draw ones as WHITE

	% Se expande la imagen para tratar todos los puntos
	inputImageWithZeros = uExtendShrink (inputImage, kernel, 'extend', 0);
	
	% número de filas y columas de la imagen y el kernel
	[ir, ic] = size (inputImageWithZeros);
	
	% indices de la esquina superior izquierda de la imagen
	inputImageCoords = reshape([1:ir*ic], ir, ic);
	kernelIncr = inputImageCoords(1:strElSize, 1:strElSize);
	
	% puntos objeto en la imagen y kernel
	blancos_imagen = find(inputImageWithZeros);
	blancos_kernel = find (kernel);
	
	% Índices de los puntos a considerar si el kernel se coloca en la esquina
	% superior izquierda de la imagen
	kernelPuntos = kernelIncr(blancos_kernel);
	blancos_distancias =  blancos_imagen - median(kernelPuntos);
	
	inputImageWithOnes = uExtendShrink (inputImage, kernel, 'extend', 1);
	outputImage = inputImageWithOnes;
	%outputImage = inputImageWithZeros;
	for n = 1:numel(blancos_distancias)
		outputImage(blancos_imagen(n)) = ...
			all ( inputImageWithOnes(kernelPuntos + blancos_distancias(n)) );
	end
	
end