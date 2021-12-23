% https://link.springer.com/content/pdf/10.1186%2F1687-5281-2012-19.pdf

clear
clc

mean_r = 0;
mean_g = 0;
mean_b = 0;

for index = [1 5 6 16]

    imagenPath = strcat('imagenes\retinografia-res-crop-', int2str(index), '.jpg');
    i_orig = imread (imagenPath);
    disp(sprintf('[template] leída imagen %d', index))

    r = i_orig (:,:,1);
    g = i_orig (:,:,1);
    b = i_orig (:,:,1);

    [counts_r, ~] = imhist (r);
    [counts_g, ~] = imhist (g);
    [counts_b, ~] = imhist (b);

	mean_r = mean_r + counts_r;
	mean_g = mean_g + counts_g;
	mean_b = mean_b + counts_b;
	
	disp(sprintf('[template] procesada imagen %d', index))

end

mean_r = mean_r / 4;
mean_g = mean_g / 4;
mean_b = mean_b / 4;

path = strcat ('datosPlantilla');
save (path, 'mean_r', 'mean_g', 'mean_b')


