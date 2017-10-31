% Implementar el algoritmo realce de contraste "Window-level contrast
% enhancement", especificando el nivel de gris central y el tama�o de
% ventana
function outputImage = histEnhance (inputImage, cenValue, winSize)

  outputImage = uInitializeImage (inputImage);
  [a,b] = size (inputImage);

  % intensidades m�nima y m�xima
  maxVal = cenValue+winSize/2;
  minVal = cenValue-winSize/2;

  disp(sprintf('[histEnhance] Valores m�nimo, m�ximo y central: %4.2f %4.2f %4.2f', ...
    minVal, maxVal, cenValue)); 

  % Coordenadas de las intensidades dentro de las ventana
  cordsWindow = inputImage>cenValue-winSize/2 & inputImage<cenValue+winSize/2;

  % coordenadas de los valores sobre el m�ximo y bajo el m�nimo
  cordsLow = inputImage<maxVal; 
  cordsHigh = inputImage>maxVal;

  outputImage (cordsLow) = 0;
  outputImage (cordsHigh) = 1;

  % Los valores dentro de la venta se reparten mediante la ecuaci�n de la recta:
  % y = mx + b
  % siendo m = 1/w y w=winsize
  % se despeja b de: 0 = 1/w (c - w/2) + b
  b = - (1/winSize) * minVal;
  outputImage(1:end) = 1/winSize * inputImage(1:end) + b;
  

end