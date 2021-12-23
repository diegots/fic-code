clc
clear

% El rango de valores que imfindcircles utiliza representa el tamaño del radio 
% de los círculos buscados
% Las unidades del radio están en píxeles
% Por defecto se buscan circulos claros en un fondo oscuro, si se trabaja con
% caso inverso se utiliza la propiedad ObjectPolarity con valor dark

imagenPath = strcat('imagenes\circles.png');
i_orig = imread (imagenPath);
i = rgb2gray (i_orig);

[centers, radii, metric] = imfindcircles(i, [40 60], 'ObjectPolarity','dark')
centersStrong5 = centers(1:10,:); 
radiiStrong5 = radii(1:10);
imshow (i)
viscircles(centersStrong5, radiiStrong5,'EdgeColor','b');
