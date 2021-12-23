% Lectura de la imagen
index = 1;
imagenPath = strcat('imagenes\retinografia-', int2str(index), '.jpg');
i_orig = imread (imagenPath);
disp(sprintf('[pruebas] leída imagen %d', index))

i = rgb2gray (i_orig);
i = histeq (i);
levels = multithresh(i, 1)
seg_I = imquantize(i,levels);
result = label2rgb(seg_I);
pos = find (eq(seg_I, 1));
i (pos) = 0;

i = histeq (i);
levels = multithresh(i, 1)
seg_I = imquantize(i,levels);
result = label2rgb(seg_I);
pos = find (eq(seg_I, 1));
i (pos) = 0;

se = strel('square', 5);
erosionada = imerode(i,se);
se = strel('octagon', 6);
dilatada = imdilate(i,se);

levels = multithresh(i, 1)
seg_I = imquantize(i,levels);
result = label2rgb(seg_I);
pos = find (eq(seg_I, 1));
i (pos) = 0;

i = histeq (i);

levels = multithresh(i, 1)
seg_I = imquantize(i,levels);
result = label2rgb(seg_I);
pos = find (eq(seg_I, 1));
i (pos) = 0;

se = strel('square', 5);
erosionada = imerode(i,se);
se = strel('octagon', 6);
dilatada = imdilate(i,se);

levels = multithresh(i, 1)
seg_I = imquantize(i,levels);
result = label2rgb(seg_I);
pos = find (eq(seg_I, 1));
i (pos) = 0;

se = strel('square', 5);
erosionada = imerode(i,se);
se = strel('octagon', 6);
dilatada = imdilate(i,se);

% Algoritmo de Hough para detectar círculos
% Tamaño del radio obtenido: 180 píxeles.
% Métodos PhaseCode y TwoStage
[centers, radii, metric] = imfindcircles(i, [175 195], ...
        'Sensitivity',0.99, ...
        'Method','PhaseCode', ...
        'EdgeThreshold',0.1);
[r,c] = size (centers);
centersStrong5 = centers(1:r,:);
radiiStrong5 = radii(1:r);

imshow (i)
viscircles(centersStrong5, radiiStrong5,'EdgeColor','b');


