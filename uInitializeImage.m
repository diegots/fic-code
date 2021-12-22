function outputImage = uInitializeImage (inputImage, fillValue)
  % Inicializa la variable de salida a ceros
  [a,b] = size (inputImage);
  outputImage = double (fillValue * ones (a,b));
end
