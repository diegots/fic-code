clc
clear

for index = [5:5]
	% Lectura de la imagen
	imagenPath = strcat('imagenes\retinografia-', int2str(index), '.jpg');
	i_orig = imread (imagenPath);
	disp(sprintf('[pruebas] le�da imagen %d', index))

	% Informaci�n separada de los canales RGB
	i_red = i_orig(:,:,1);
	i_green = i_orig(:,:,2);
	i_blue = i_orig(:,:,3);

	% Convierte la imagen a escala de grises
	% Seg�n la documentaci�n, 'help rgb2gray', se aplica:
	%    rgb2gray converts RGB values to grayscale values by forming a weighted
	%    sum of the R, G, and B components:
	%
	%    0.2989 * R + 0.5870 * G + 0.1140 * B
	i = rgb2gray (i_orig);

	% Tama�o de la imagen
	[r c] = size (i);

	% Descarta valores oscuros que pertenecen al marco negro de la imagen. Los
	% dem�s p�xeles de la imagen se ponen a la intensidad m�xima para comprobar
	% visualmente si se pierde informaci�n en la zona de trabajo.
	i (i<=10) = 0;
	i_red (i_red<=10) = 0;
	i_green (i_green<=10) = 0;
	i_blue (i_blue<=10) = 0;

        % Suavizado con un filtro Gaussiano
        filter = fspecial ('gaussian', 3, 4);
        i_green = imfilter (i_green, filter, 'replicate', 'same');

	% Se utiliza una ecualizaci�n del histograma para aumentar el contraste
	i = histeq(i);
	i_red= histeq(i_red);
	i_green = histeq(i_green);
	i_blue = histeq(i_blue);

        figure (1)
        imhist (i_red)
        title (sprintf('Histograma del canal R imagen %d'))

        % Se reduce la informaci�n de la imagen mediante la segmentaci�n 
        % previa por el m�todo de Otsu multinivel
        levels = multithresh(i_green, 9);
        seg_I = imquantize(i_green,levels);
        result = label2rgb(seg_I);

        % Se descarta la informaci�n que no pertenece al umbral de inter�s
        pos = find (not(eq(seg_I,  10)));
        otsu_multi_level = i;
        otsu_multi_level(pos) = 0;

        %imshow(otsu_multi_level)

        % Erosi�n seguida de dilataci�n con distinto EE
        se = strel('square', 3);
        erosionada = imerode(otsu_multi_level,se);
        se = strel('octagon', 6);
        dilatada = imdilate(erosionada,se);

        %Algoritmo de Hough para detectar c�rculos
        % Tama�o del radio obtenido: 180 p�xeles.
        % M�todos PhaseCode y TwoStage
        [centers, radii, metric] = imfindcircles(i, [175 195],...
                'Sensitivity',0.99, ...
                'Method','PhaseCode', ... 
                'EdgeThreshold',0.1);
        [r,c] = size (centers);
        centersStrong5 = centers(1:r,:);
        radiiStrong5 = radii(1:r);
        %imshow (i)
        %viscircles(centersStrong5, radiiStrong5,'EdgeColor','b');

        %imshowpair(otsu_multi_level,dilatada,'montage')
        %str = horzcat ('Imagen %d con ecualizaci�n y Hough PhaseCode');
	%title (sprintf(str, index))

	disp(sprintf('[pruebas] imagen %d procesada', index))

	pause
end
