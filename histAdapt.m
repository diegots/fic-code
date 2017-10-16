function outputImage = histAdapt (inputImage, minValue, maxValue)

  % Normalize entre el rango 0-1
  function out =  normalize (x, min, max)
    out = (x(1:end)/(max-min)) - (min/(max-min));
  end

  % Cambia la entrada del rango 0-1 al nuevo
  function out = expand (x, min, max)
    out = ((max-min) * x(1:end)) + min;
  end

  image = double(uReadImage(inputImage));

  t = min (image);
  oldMinValue = min (t) % mínima intensidad en la imagen
  t = max (image);
  oldMaxValue = max (t) % maxima intensidad en la imagen

  disp(sprintf('Transformando el histograma de %u-%u a %u-%u', ...
    oldMinValue, oldMaxValue, minValue, maxValue))

  % Inicializa la variable de salida
  [a,b] = size (image);
  outputImage = double (zeros (a,b));

  % Normaliza los valores de la imagen de entrada entre 0-1
  image_norm = normalize(image, oldMinValue, oldMaxValue);
  outputImage = expand(image_norm, minValue, maxValue);

  image = uint8 (image);
  outputImage = reshape( uint8 (outputImage), a, b);

  figure(1)
  imhist(image);
  figure(2)
  imhist(outputImage);
end
