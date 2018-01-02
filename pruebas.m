clc
clear


for index = [1:20]
	% Lectura de la imagen
	imagenPath = strcat('imagenes\retinografia-', int2str(index), '.jpg');
	i = imread (imagenPath);
	disp(sprintf('[pruebas] leída imagen %d', index))
	
	% Información separada de los canales RGB
	i_red = i(:,:,1);
	i_green = i(:,:,2);
	i_blue = i(:,:,3);
	
	% Convierte la imagen a escala de grises
	% Según la documentación, 'help rgb2gray', se aplica:
	%    rgb2gray converts RGB values to grayscale values by forming a weighted 
    %    sum of the R, G, and B components:
	%
    %    0.2989 * R + 0.5870 * G + 0.1140 * B
	i = rgb2gray (i);
	
	% Tamaño de la imagen
	[r c] = size (i);

	% Descarta valores oscuros que pertenecen al marco negro de la imagen. Los
	% demás píxeles de la imagen se ponen a la intensidad máxima para comprobar
	% visualmente si se pierde información en la zona de trabajo.
	i (i<=10) = 0;
	i (i>10) = 255;
	i_green (i_green<=10) = 0;
	i_green (i_green>10) = 255;
	
	% Muestra imágenes e histogramas
	figure(1), imshow(i) 
	title (sprintf('Imagen %d', index))
	%figure(2), imhist (i)

	figure(5), imshow (i_green)
	title (sprintf('Canal G de la imagen %d', index))
	%figure(6), imhist (i_green)

	%figure(3), imshow (i_red)
	%figure(4), imhist (i_red)

	%figure(7), imshow (i_blue)
	%figure(8), imhist (i_blue)

	pause
end
