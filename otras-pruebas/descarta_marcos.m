% Información del histograma por debajo del nivel 10
i = imread ('imagenes\retinografia-2.jpg');
ig = i (:,:,2);
imhist(ig)
title ('Parte del histograma del canal G de la imagen 2')

ig (ig > 10) = 255;
imshow (ig)
