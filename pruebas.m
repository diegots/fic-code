% Imagenes
%imagen = 'images\lenna512.bmp';
%imagen = 'images\landscape.jpg';
imagen = 'images\guitar-1.jpg';

%
% histEnhance
%

%
% histAdapt
%
min = 30;
max = 127;
o = histAdapt(imagen, min, max);

%imwrite (o, 'test.jpg');
%o = histAdapt('test.jpg', 0, 233); % Probamos a expandir el resultado anterior

figure(3)
imshow(o)
