% Lectura de la imagen
index = 6;
imagenPath = strcat('imagenes\retinografia-', int2str(index), '.jpg');
i_orig = imread (imagenPath);
disp(sprintf('[pruebas] leída imagen %d', index))

i = rgb2gray (i_orig);
[r,c] = size (i);
i = histeq (i);

levels = multithresh(i, 20)
seg_I = imquantize(i,levels);
result = label2rgb(seg_I);

pos = find (eq(seg_I, 21) );
res = zeros (r,c);
res (pos) = i (pos);

se = strel('square', 7);
res = imerode(res,se);
se = strel('octagon', 6);
res = imdilate(res,se);

% Algoritmo de Hough para detectar círculos
% Tamaño del radio obtenido: 180 píxeles.
% Métodos PhaseCode y TwoStage
[centers, radii, metric] = imfindcircles(i, [175 195], ...
        'Sensitivity',0.97, ...
        'Method','PhaseCode', ...
        'EdgeThreshold',0.5);
[r,c] = size (centers)
centersStrong5 = centers(1:r,:);
radiiStrong5 = radii(1:r);

imshow (res)
viscircles(centersStrong5, radiiStrong5,'EdgeColor','b');


