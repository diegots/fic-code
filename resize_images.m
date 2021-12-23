% Las imágenes son en origen de 3504 x 2336 lo que supone un gran tiempo de 
% computación a la hora de aplicar las técnicas. El siguiente código las 
% redimensiona. También se aplica ya a las imágenes el filtro de tamaño 6, según
% requiere el algoritmo.

factor = 0.2;
filter = fspecial ('average', 6);

for index = [1:20]

	imagenPath = strcat('imagenes\retinografia-', int2str(index), '.jpg');
	i_orig = imread (imagenPath);
	disp(sprintf('[resize_images] leida imagen %d', index))
	
	%filtered = imfilter (i_orig, filter);
	%disp(sprintf('[resize_images] filtrada imagen %d', index))
	%
	%resized = imresize (filtered, factor);
	%disp(sprintf('[resize_images] recortada imagen %d', index))

	resized = imresize (i_orig, factor);
	disp(sprintf('[resize_images] recortada imagen %d', index))
	
	newImagenPath = strcat('imagenes\retinografia-rec-', int2str(index), '.jpg');
	imwrite (resized, newImagenPath, 'JPG')

end
