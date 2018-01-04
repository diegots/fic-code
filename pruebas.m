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
	i_red (i_red<=10) = 0;
	i_green (i_green<=10) = 0;
	i_blue (i_blue<=10) = 0;

	% Se busca aumentar el contraste para poder descartar las venas
	% Según http://ieeexplore.ieee.org/document/6428782/?part=1 el canal verde
	% es especialmente apropiado

	% Se utiliza una ecualización del histograma para aumentar el contraste
	i = histeq(i);
	i_red = histeq(i_red);
	i_green = histeq(i_green);
	i_blue = histeq(i_blue);

        levels = multithresh(i_green, 9);
        seg_I = imquantize(i_green,levels);
        result = label2rgb(seg_I);
        figure (9)
        imshowpair(i_green,result,'montage')
        str = horzcat ('Canal G de la imagen %d ecualizado (izq) y tras ', ...
                'segmentación multinivel (der)');
	title (sprintf(str, index))

	% Muestra imágenes e histogramas
	%hFigure = figure(1);
        %imshow(i)
        %set (hFigure, 'ToolBar', 'none');
	%title (sprintf('Imagen %d', index))

	%hFigure = figure(2);
        %imhist (i)
        %set (hFigure, 'ToolBar', 'none');
	%title (sprintf('Histograma de la imagen %d', index))
	%axis tight

	%hFigure = figure(3);
        %imshow (i_red)
        %set (hFigure, 'ToolBar', 'none');
	%title (sprintf('Canal R de la imagen %d', index))

	%hFigure = figure(4);
        %imhist (i_red)
        %set (hFigure, 'ToolBar', 'none');
	%title (sprintf('Histograma del canal R en la imagen %d', index))
	%axis tight

	%hFigure = figure(5);
        %imshow (i_green)
        %set (hFigure, 'ToolBar', 'none');
	%title (sprintf('Canal G de la imagen %d', index))

	%hFigure = figure(6);
        %imhist (i_green)
        %set (hFigure, 'ToolBar', 'none');
	%title (sprintf('Histograma del canal G en la imagen %d', index))
	%axis tight

	%hFigure = figure(7);
        %imshow (i_blue)
        %set (hFigure, 'ToolBar', 'none');
	%title (sprintf('Canal B de la imagen %d', index))

	%hFigure = figure(8);
        %imhist (i_blue)
        %set (hFigure, 'ToolBar', 'none');
	%title (sprintf('Histograma del canal B en la imagen %d', index))
	%axis tight

	pause
end
