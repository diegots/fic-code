function outputImage = uInitializeImage (inputImage)
  % Inicializa la variable de salida a ceros
  [a,b] = size (inputImage);
  outputImage = double (zeros (a,b));
end
