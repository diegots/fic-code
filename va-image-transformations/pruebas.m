% Limpia pantalla y variables
clc
clear

% Imagenes
%imagen = 'images\gradiente.png';
%imagen = 'images\gradiente-horizontal.png';
%imagen = 'images\circle.png';
%imagen = 'images\square.png';
%imagen = 'images\star.png';
%imagen = 'images\down.png';
%imagen = 'images\4-orientaciones.png';
imagen = 'images\lenna512.bmp';
%imagen = 'images\landscape.jpg';
%imagen = 'images\building.jpg';
%imagen = 'images\guitar-1.jpg';
%imagen = 'images\5-square.bmp';
%imagen = 'images\5-square-reverse.png';
%imagen = 'images\5-dot.png';
%imagen = 'images\5-white-line.png';
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
%min = 0.17;
%max = 0.84;

%o = histAdapt(read_image, min, max);
%o = histAdapt(o, 0.1, 0.5); % Probamos a expandir el resultado anterior
%o = histAdapt(o, 0.01, 0.99); 

%o = imadjust(read_image,stretchlim(read_image),[min max]); % impl MATLAB
%o = imadjust(o,stretchlim(o),[0.1 0.5]);
%o = imadjust(o,stretchlim(o),[0.01 0.99]);

%
% convolve
%
%kernel = fspecial ('sobel')
%kernel = fspecial ('prewitt');
%kernel = (repmat(1,3))/9 % filtro de media 3x3
%kernel = [1 1 1; 1 2 1; 1 1 1] / 10;
%kernel = [1 2 1; 2 4 2; 1 2 1] * (1/16);
%kernel = (repmat(1,5))/25; % tama�o 5x5
%kernel = (repmat(1,7))/49; % tama�o 7x7
%kernel = (repmat(1,9))/81; % tama�o 9x9
%kernel = fspecial('gaussian', 10,1.5); % Filtro Gaussiano 15x15
%kernel = reshape(repmat(1/15,1,15),3,5); % kernel 3x5 tambi�n funciona
%kernel = reshape(repmat(1/28,28,1),7,4); % kernel 7x4

%shape = 'same';
%shape = 'same';
%o = convolve (read_image, kernel, shape);
%gy = conv2(read_image, kernel, shape);
%gx = conv2(read_image, kernel', shape);
%disp('y:')
%disp(gy(33:39,300:320))
%disp('x:')
%disp(gx(33:39,300:320))
%read_image(33:39,300:320)

%o = uExtendShrink (o, kernel, 'shrink'); % shrink o extend

%
% gaussKernel1D
%
%kernel = gaussKernel1D(0.5);
%kernel = gaussKernel1D(2.5);
%kernel = gaussKernel1D(1.5);
%kernel = gaussKernel1D(2);

% Para la segunda pasada de la convoluci�n hay que rotar la imagen y volver a 
% rotar la imagen a la salida para recuperar la posici�n original.
% Otra opci�n ser�a rotar el filtro y mantener la imagen
%
%o = convolve (read_image, kernel, shape); % rotando la imagen
%o = convolve (o', kernel, shape)';
%
%o = convolve (read_image, kernel, shape); % rotando el kernel
%o = convolve (o, kernel', shape);

%
% gaussianFilter2D
%
%o = gaussianFilter2D (read_image, 2.5);

%
% medianFilter2D
%
%filterSize = 10;
%o = medianFilter2D (read_image, filterSize);
%kernel = ones (filterSize); % kernel ficticio para utilizar en el recorte
%o = uExtendShrink (o, kernel, 'shrink'); % shrink o extend

%
% highBoost
%
%method = 'gaussian' ;
%parameter = 9;
%
%method = 'median';
%parameter = 13;
%
%A = 1.5;
%o = highBoost (read_image, A, method, parameter);

%
% Kernels para operaciones morfol�gicas
%
%strElType = 'cross';
%strElType = 'square';
%strElType = 'lineh';
%strElType = 'linev';
%kernel = uKernelMorfologicos (strElType, 5)


%
% Erosi�n
%
%read_image = uThresholding (read_image, 0.5);
%o = erode (read_image, strElType, 3); % inputImage, strElType, strElSize

%
% Dilataci�n
%
%read_image = uThresholding (read_image, 0.7);
%o = dilate (read_image, strElType, 7); % inputImage, strElType, strElSize

%
% Apertura: erosi�n -> dilataci�n
%
%read_image = uThresholding (read_image, 0.7);
%o = opening (read_image, strElType, 7); % inputImage, strElType, strElSize

%
% Cierre: dilataci�n -> erosi�n
%
%read_image = uThresholding (read_image, 0.7);
%o = closing (read_image, strElType, 7); % inputImage, strElType, strElSize

%
% TopHat
%
% blanco: imagen - apertura = imagen - (erosi�n -> dilataci�n)
% negro: cierre - imagen = (dilataci�n -> erosi�n) - imagen
%read_image = uThresholding (read_image, 0.6);
%o = tophatFilter (read_image, strElType, 7, 'black'); % inputImage, strElType, strElSize

%
% Derivatives
%
%operator = 'Roberts';
%operator = 'CentralDiff';
%operator = 'Prewitt';
%operator = 'Sobel';

%gy = conv2(read_image, operator);

%[gx, gy] = derivatives (read_image, operator);
%o = uExtendShrink (gx, ones(2), 'shrink'); % shrink o extend

% Se ajusta el histograma para visualizar las zonas sin cambios en el entorno
% de 0.5 y las zonas de cambios como puntos negros o blancos seg�n el cambio sea
% de menos a m�s o de m�s a menos.
%o = histAdapt(o, 0, 1); 


%
% Canny
%
%uBajo = 0.05;
%sigma=1;
%o = edgeCanny (read_image, sigma, uBajo, uBajo*4);
%o = edge(read_image,'canny', [uBajo uBajo*4], sigma);

%
% Harris
%
%sigmaI=2.5;
%sigmaD=0.7*sigmaI;
%t=0.00002;
%outputImage = cornerHarris (read_image, sigmaD, sigmaI, t);
%
%%
%% Visualiza los resultados (depuraci�n Harris)
%% 
%figure(8)
%imshow(read_image)
%hold on
%
%[r c] = size (read_image);
%for e = find(outputImage(1:end))
%%	for e = puntos' % todos los valores dados por Harris
%	[i,j] = ind2sub([r c], e);
%	%disp(sprintf('[cornerHarris] e:%d -> (i:%d, j:%d)', e, i, j))
%	plot ([j], [i], 'ro--')
%	%plot([0 10],[0 9],'+','LineWidth',20,'MarkerSize',50)
%end

%
%
%%figure(1)
%%imhist(read_image) % histograma original
%%title ('Histograma original')
%figure(2)
%imhist(o) % histograma nuevo
%title ('Histograma nuevo')
%axis tight  % los rangos de valores son los rangos de los datos
%%
%figure(3)
%imshow(read_image, 'InitialMagnification','fit')
%title('Imagen original')
%
%figure(4)
%% InitialMagnification elimina warning:
%% Image is too big to fit on screen; displaying
%imshow(o, 'InitialMagnification','fit')
%title('Imagen modificada')
%