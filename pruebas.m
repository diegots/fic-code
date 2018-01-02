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
	i (i<=10) = 10;
	i_red (i_red<=10) = 10;
	i_green (i_green<=10) = 10;
	i_blue (i_blue<=10) = 10;

	% Se busca aumentar el contraste para poder descartar las venas
	% Según http://ieeexplore.ieee.org/document/6428782/?part=1 el canal verde
	% es especialmente apropiado
	
	% Se utiliza una expansión de histograma para aumentar el contraste
	a = 0.01;
	b = 0.99;
	i = imadjust(i,stretchlim(i),[a, b]);
	i_red = imadjust(i_red,stretchlim(i_red),[a, b]);
	i_green = imadjust(i_green,stretchlim(i_green),[a, b]);
	i_blue = imadjust(i_blue,stretchlim(i_blue),[a, b]);
	
	% Muestra imágenes e histogramas
	figure(1), imshow(i) 
	title (sprintf('Imagen %d', index))
	figure(2), imhist (i)
	title (sprintf('Histograma de la imagen %d', index))
	axis tight
	
	figure(3), imshow (i_red)
	title (sprintf('Canal R de la imagen %d', index))
	figure(4), imhist (i_red)
	title (sprintf('Histograma del canal R en la imagen %d', index))
	axis tight
	
	figure(5), imshow (i_green)
	title (sprintf('Canal G de la imagen %d', index))
	figure(6), imhist (i_green)
	title (sprintf('Histograma del canal G en la imagen %d', index))
	axis tight
	
	figure(7), imshow (i_blue)
	title (sprintf('Canal B de la imagen %d', index))
	figure(8), imhist (i_blue)
	title (sprintf('Histograma del canal B en la imagen %d', index))
	axis tight
	
	pause
end
