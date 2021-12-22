% histAdapt adapta el histograma de la imagen pasada en el parámetro inputImage
% al nuevo rango comprendido entre minValue y maxValue
function outputImage = histAdapt (inputImage, minValue, maxValue)

	disp('[histAdapt] Expande o contrae el histograma en base a los valores')
	disp('            y máximo pasados como argumento.')

	% Cambia la entrada del rango actual en la imagen al nuevo
	function out = expand (x, minValue, maxValue)
		
		% Máximo y mínimo actuales de la imagen de entrada
		oldMax = max(max(x));
		oldMin = min(min(x));

		disp(sprintf('[histAdapt] Transformando el histograma de %4.2f,%4.2f -> %4.2f,%4.2f', ...
		oldMin, oldMax, minValue, maxValue))

		% Nuevo valor para cada punto. Calculado tal como se indica en la fórmula
		% de la p. 17 del las transparencias de preprocesado
		out = (((maxValue - minValue) * (x(1:end) - oldMin)) / (oldMax - oldMin)) + minValue;
		
		realMin = min (min(out));
		realMax = max (max(out));
		disp(sprintf('[histAdapt] Ahora la imagen está entre %4.2f y %4.2f', realMin, realMax))
		
		% Devuelve la matriz con las mismas proporciones que a la entrada a x b
		[a,b] = size (x);
		out = reshape (out,a,b);
	end % fin de la función expand
	
	% Inicializa la variable de salida
	[a,b] = size (inputImage);
	outputImage = double (zeros (a,b));
	outputImage = expand(inputImage, minValue, maxValue);

end
