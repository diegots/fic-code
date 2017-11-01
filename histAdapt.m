% histAdapt adapta el histograma de la imagen pasada en el parámetro inputImage
% al nuevo rango comprendido entre minValue y maxValue
function outputImage = histAdapt (inputImage, minValue, maxValue)

	disp('[histAdapt] Expande o contrae el histograma en base a los valores')
	disp('y máximo pasados como argumento.')

	% Cambia la entrada del rango actual en la imagen al nuevo
	function out = expand (x, min, max)
		% expand mueve el rango de valores normalizados de la matriz de entrada a un
		% nuevo rango dado por min y max
		[a,b] = size (x);
		
		out = (((maxValue - minValue) * (x(1:end) - oldMinValue)) / (oldMaxValue-oldMinValue)) + minValue;
		out = reshape (out,a,b);
	end
	
	% Obtiene máximo y mínimo valor de intensidad de la imagen
	oldMinValue = min (min (inputImage));
	oldMaxValue = max (max (inputImage));
	
	disp(sprintf('[histAdapt] Transformando el histograma de %4.2f,%4.2f -> %4.2f,%4.2f', ...
	oldMinValue, oldMaxValue, minValue, maxValue))
	
	% Inicializa la variable de salida
	[a,b] = size (inputImage);
	outputImage = double (zeros (a,b));
	
	outputImage = expand(inputImage, minValue, maxValue);

end
