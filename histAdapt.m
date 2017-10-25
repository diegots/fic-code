function outputImage = histAdapt (inputImage, minValue, maxValue)
% histAdapt adapta el histograma de la imagen pasada en el parámetro inputImage
% al nuevo rango comprendido entre minValue y maxValue

  MAX_INTENSITY = 255; %  se consideran imágenes de 8 bits/pixel

  % Cambia la entrada del rango 0-1 al nuevo
  function out = expand (x, min, max)
  % expand mueve el rango de valores normalizados de la matriz de entrada a un
  % nuevo rango dado por min y max
    [a,b] = size (x);
    out = ((max-min) * x(1:end)) + min;
    out = reshape (out,a,b);
  end

  % Obtiene máximo y mínimo valor de intensidad de la imagen
  oldMinValue = min (min (inputImage));
  oldMaxValue = max (max (inputImage));

  % Si la imagen no está normalizada, se normaliza
  if (oldMaxValue > 1)
    disp('Normalizando la imagen entre 0-1.')
    inputImage = uNormalize(image, oldMinValue, oldMaxValue);

    oldMinValue = oldMinValue / MAX_INTENSITY;
    oldMaxValue = oldMaxValue / MAX_INTENSITY;
    minValue = minValue / MAX_INTENSITY;
    maxValue = maxValue / MAX_INTENSITY;
  end

  if (maxValue > 1)
    minValue = minValue / MAX_INTENSITY;
    maxValue = maxValue / MAX_INTENSITY;
  end

  disp(sprintf('Transformando el histograma de %4.2f-%4.2f a %4.2f-%4.2f', ...
  oldMinValue, oldMaxValue, minValue, maxValue))

  % Inicializa la variable de salida
  [a,b] = size (inputImage);
  outputImage = double (zeros (a,b));

  outputImage = expand(inputImage, minValue, maxValue);

end
