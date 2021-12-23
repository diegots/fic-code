clc
clear


tamaPlantilla = 80;
t_r = 0.5;
t_g = 2;
t_b = 1;

for index = [1:1]
        path = strcat('datosPlantilla');
        load(path);
        disp(sprintf('[templateAlg] leídos datos'))
end

tic
filter = fspecial ('average', 6);

for index = [2:3]
        w = zeros (tamaPlantilla, tamaPlantilla, 3);
        canal_r = zeros (tamaPlantilla, tamaPlantilla);
        canal_g = zeros (tamaPlantilla, tamaPlantilla);
        canal_b = zeros (tamaPlantilla, tamaPlantilla);
        valorMax = 0;

        imagenPath = strcat('imagenes\retinografia-res-', int2str(index), '.jpg');
        i_orig = imread (imagenPath);
        disp(sprintf('[templateAlg] leída imagen %d', index))

        [rows,cols,~] = size (i_orig);
        %i = imfilter (i_orig, filter); Imágenes filtradas previamente
        C = zeros (rows-tamaPlantilla, cols-tamaPlantilla);

        for r = 1:rows-tamaPlantilla
            for c = 1:cols-tamaPlantilla
                w = i_orig (r:r+tamaPlantilla-1,c:c+tamaPlantilla-1,:);

                [recuento_r, ~] = imhist (w (:,:,1), 256);
                [recuento_g, ~] = imhist (w (:,:,2), 256);
                [recuento_b, ~] = imhist (w (:,:,3), 256);

                suma_r = 0;
                for px = 1:256
                    suma_r = suma_r + (recuento_r (px) - mean_r(px))^2;
                end
                criterio_r = 1 / (1+suma_r);

                suma_g = 0;
                for px = 1:256
                    suma_g = suma_g + (recuento_g (px) - mean_g(px))^2;
                end
                criterio_g = 1 / (1+suma_g);

                suma_b = 0;
                for px = 1:256
                    suma_b = suma_b + (recuento_b (px) - mean_b(px))^2;
                end
                criterio_b = 1 / (1+suma_b);

                C(r,c) = criterio_r * t_r + criterio_g * t_g + criterio_b * t_b;
                if (C(r,c) > valorMax)
                    valorMax = C(r,c);
                    rowMax = r;
                    colMax = c;
                end
            end
        end

        toc
        beep
        imshow (i_orig)
        hold on
        plot (rowMax+tamaPlantilla/2, colMax/2, 'kx', 'MarkerSize', 12, 'LineWidth', 2)
        pause

end
