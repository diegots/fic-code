clc
clear

for index = [1:20]
	% Lectura de la imagen
	imagenPath = strcat('imagenes\retinografia-rec-', int2str(index), '.jpg');
	i_orig = imread (imagenPath);
        %figure (1)
        %imshow(i_orig)
        %title  (sprintf('Imagen %d original', index))
	disp(sprintf('[pruebas] leída imagen %d', index))

	% Tamaño de la imagen
	[r c, ~] = size (i_orig);
        centro = r/2
        mayor_r = r
        menor_r = 1

	% Información separada de los canales RGB
	i_red = i_orig(:,:,1);
	i_green = i_orig(:,:,2);
	i_blue = i_orig(:,:,3);

	% Convierte la imagen a escala de grises
	% Según la documentación, 'help rgb2gray', se aplica:
	%    rgb2gray converts RGB values to grayscale values by forming a weighted
	%    sum of the R, G, and B components:
	%
	%    0.2989 * R + 0.5870 * G + 0.1140 * B
	i = rgb2gray (i_orig);

	% Descarta valores oscuros que pertenecen al marco negro de la imagen. Los
	% demás píxeles de la imagen se ponen a la intensidad máxima para comprobar
	% visualmente si se pierde información en la zona de trabajo.
	i (i<=10) = 0;
	i_red (i_red<=10) = 0;
	i_green (i_green<=10) = 0;
	i_blue (i_blue<=10) = 0;

        % Suavizado con un filtro Gaussiano
        filter = fspecial ('gaussian', 11, 15);
        i_green = imfilter (i_green, filter, 'replicate', 'same');

	% Se utiliza una ecualización del histograma para aumentar el contraste
        % pero sólo en el canal G, que es el que ofrece más contraste de 
        % forma natural
	i_green = histeq(i_green);

        % Se reduce la información de la imagen mediante la segmentación 
        % previa por el método de Otsu multinivel
        levels = multithresh(i_green, 19);
        seg_I = imquantize(i_green,levels);
        result = label2rgb(seg_I);
        %figure (2)
        %imshow(result)
        %title  (sprintf('Imagen %d tras aplicar Otsu multinivel', index))

        % Se descarta la información que no pertenece al umbral de interés 
        % dado por Otsu
        pos = find (eq(seg_I,  20));
        otsu_multi_level = zeros (r,c);
        otsu_multi_level(pos) = i(pos);
        %figure (3)
        %imshow(otsu_multi_level)

        % Erosión  con EE de tipo disco
        se = strel('disk', 15, 8);
        erosionada = imerode(otsu_multi_level,se);
        figure (4)
        imshow (erosionada)
        title  (sprintf('Imagen %d erosionada', index))

        % Componentes conexas de la imagen binaria generada tras la erosión
        [componentes, NUM] = bwlabel (erosionada);

        % Se busca la región de más area, que es la que tendrá el disco 
        % óptico
        num_px = 0;
        good_label = 0;
        for label = 1:NUM

            [r_,c_] = ind2sub (size(i), find (componentes == label));
            max_ = max (r_)
            min_ = min (r_)

            n = numel (componentes (componentes == label))
            if (max_ > centro & min_ > centro) % mitad inferior de la imagen
                if (min_ < menor_r & n > num_px)
                    good_label = label;
                    num_px = n;
                    menor_r = min_;
                else
                    disp(sprintf ('[pruebas] no cumple (mitad inferior)!'))
                end
            elseif  (max_ < centro & min_ < centro) % mitad superior de la imagen
                if (max_ < mayor_r & n > num_px)
                    good_label = label;
                    num_px = n;
                    mayor_r = max_;
                else
                    disp(sprintf ('[pruebas] no cumple (mitad superior)!'))
                end
            else
                good_label = label;
                disp(sprintf ('[pruebas] sobre el centro!'))
                break;
            end
        end
        componentes (not (componentes == good_label)) = 0;
        %figure (5)
        %imshow (componentes)

        % Se muestra en negro la zona seleccionada sobre la imagen original
        i(find (componentes)) = 0;
        figure (7)
        imshow (i)
        title (sprintf('Imagen %d con la zona reconocida como dísco óptico', index))

	disp(sprintf('[pruebas] imagen %d procesada', index))
	pause
end
