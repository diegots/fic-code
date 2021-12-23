clc
clear

% http://ieeexplore.ieee.org/stamp/stamp.jsp?tp=&arnumber=1663780
% https://www.ncbi.nlm.nih.gov/pmc/articles/PMC4786964/#ref7

for index = [1:5]
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

        % previa por el m�todo de Otsu multinivel
        levels = multithresh(i, 2)
        seg_I = imquantize(i,levels);
        result = label2rgb(seg_I);

         
        T = levels (1) + levels (2) / 1.5
        im1 = i;
        im1 (im1 < T) = 0;
        im1 (im1 >= T) = 255;
        imshow (result)

	%% Descarta valores oscuros que pertenecen al marco negro de la imagen. Los
	%% dem�s p�xeles de la imagen se ponen a la intensidad m�xima para comprobar
	%% visualmente si se pierde informaci�n en la zona de trabajo.
        %i (i<=10) = 0;
	%i_red (i_red<=10) = 0;
	%i_green (i_green<=10) = 0;
	%i_blue (i_blue<=10) = 0;

	%% Se utiliza una ecualizaci�n del histograma para aumentar el contraste
	%i = histeq(i);
	%i_green = histeq(i_green);

        %% Aplica Unsharp Masking
        %i_green = imsharpen(i_green,'Radius',2,'Amount',1);

        %% previa por el m�todo de Otsu multinivel
        %levels = multithresh(i_green, 5);
        %seg_I = imquantize(i_green,levels);
        %result = label2rgb(seg_I);

        %% Se descarta la informaci�n que no pertenece al umbral de inter�s
        %pos = find (eq(seg_I,  4) );
        %mi_ = min (i_green(pos));
        %i (find (eq(seg_I,  1))) = mi_;
        %i (find (eq(seg_I,  2))) = mi_;
        %i (find (eq(seg_I,  3))) = mi_;

        %% Erosi�n seguida de dilataci�n con distinto EE
        %se = strel('square', 3);
        %i = imerode(i, se);
        %se = strel('octagon', 6);
        %i = imdilate(i, se);

        %%Algoritmo de Hough para detectar c�rculos
        %% Tama�o del radio obtenido: 180 p�xeles.
        %% M�todos PhaseCode y TwoStage
        %[centers, radii, metric] = imfindcircles(i, [175 195],...
        %        'Sensitivity',0.99, ...
        %        'Method','PhaseCode', ... 
        %        'EdgeThreshold',0.1);
        %[r,c] = size (centers);
        %centersStrong5 = centers(1:r,:);
        %radiiStrong5 = radii(1:r);
        %imshow (i)
        %viscircles(centersStrong5, radiiStrong5,'EdgeColor','b');

        %str = 'Unsharp mask, ecualizaci�n y Hough para c�rculos en imagen %d';
        %title (sprintf(str, index))

	disp(sprintf('[pruebas] imagen %d procesada', index))
	pause
end
