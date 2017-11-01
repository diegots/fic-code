% Limpia pantalla y variables
clc
clear

% Imagenes
%imagen = 'images\lenna512.bmp';
%imagen = 'images\landscape.jpg';
%imagen = 'images\building.jpg';
%imagen = 'images\guitar-1.jpg';
%imagen = 'images\5-square.bmp';
imagen = 'images\5-square-reverse.png';
%%imagen = 'images\5-dot.png';
%imagen = 'images/5-white-line.png';
read_image = uReadImage (imagen);

%
% histEnhance
%
%cenValue = 0.5; % histograma original
%winSize = 1.0;
%cenValue = 0.7; % se oscurece la imagen
%winSize = 0.6;
%cenValue = 0.3; % se aclara la imagen
%winSize = 0.6;
%cenValue = 0.5; % se oscurece la imagen
%winSize = 0.2;
%o = imadjust(read_image, ...
%  [cenValue-(winSize/2), cenValue+(winSize/2)], [0 1]);
%o = histEnhance(read_image, cenValue, winSize);

%
% histAdapt
%
%%max = 127; % estos valores son convertidos en histAdapt al rango 0-1
%%min = 30;
%min = 0.17;
%max = 0.84;

% Función equivalente en MATLAB:
%o = imadjust(read_image,stretchlim(read_image),[min max]);
%o = histAdapt(read_image, min, max);
%%o = histAdapt(o, 10, 233); % Probamos a expandir el resultado anterior

%
% convolve
%
%kernel = fspecial ('sobel');
%kernel = fspecial ('prewitt');
%kernel = (repmat(1,3))/9 % filtro de media 3x3
%kernel = [1 1 1; 1 2 1; 1 1 1] / 10;
%kernel = [1 2 1; 2 4 2; 1 2 1] * (1/16);
%kernel = (repmat(1,5))/25; % tamaño 5x5
%kernel = (repmat(1,7))/49; % tamaño 7x7
%kernel = (repmat(1,9))/81; % tamaño 9x9
%kernel = fspecial('gaussian', 15,1.5); % Filtro Gaussiano 15x15
%kernel = reshape(repmat(1/15,1,15),3,5); % kernel 3x5 también funciona
%kernel = reshape(repmat(1/28,28,1),7,4); % kernel 7x4

%shape = 'same';
%shape = 'full';
%o = convolve (read_image, kernel, shape);
%o = conv2(read_image, kernel, shape);

%o = uExtendShrink (o, kernel, 'shrink'); % shrink o extend

%
% gaussKernel1D
%
%kernel = gaussKernel1D(0.5);
%kernel = gaussKernel1D(2.5);
%kernel = gaussKernel1D(1.5);
%kernel = gaussKernel1D(2);

% Para la segunda pasada de la convolución hay que rotar la imagen y volver a 
% rotar la imagen a la salida para recuperar la posición original.
% Otra opción sería rotar el filtro y mantener la imagen
%
%o = convolve (read_image, kernel); % rotando la imagen
%o = convolve (o', kernel)';
%
%o = convolve (read_image, kernel); % rotando el kernel
%o = convolve (o, kernel');

%
% gaussianFilter2D
%
%o = gaussianFilter2D (read_image, 0.4);

%
% medianFilter2D
%
%o = medianFilter2D (read_image, 25);

%
% highBoost
%
%method = 'gaussian' ;
%parameter = 5;
%
%method = 'median';
%parameter = 15;
%A = 1.2;
%o = highBoost (read_image, A, method, parameter);

%
% Kernels para operaciones morfológicas
%
%type = 'cross';
type = 'square';
%type = 'lineh';
%type = 'linev';
%kernel = uKernelMorfologicos (type, 5)


%
% Erosión
%
read_image = uThresholding (read_image, 0.5);
o = erode (read_image, type, 3); % inputImage, strElType, strElSize


%figure(1)
%imhist(read_image) % histograma original
%title ('Histograma original')
figure(2)
imhist(o) % histograma nuevo
title ('Histograma nuevo')
%
figure(3)
imshow(read_image, 'InitialMagnification','fit')
title('Imagen original')

figure(4)
% InitialMagnification elimina warning:
% Image is too big to fit on screen; displaying
imshow(o, 'InitialMagnification','fit')
title('Imagen modificada')
