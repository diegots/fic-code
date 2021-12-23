

for index = [1:20]
    imagenPath = strcat('imagenes\retinografia-rec-', int2str(index), '.jpg');
    i_orig = imread (imagenPath);

    [rows, cols] = size (i_orig);

    i_red = i_orig(:,:,1);
    i_green = i_orig(:,:,2);
    i_blue = i_orig(:,:,3);
    i_gray = rgb2gray (i_orig);

    filter = fspecial ('average', 6);
    i_green = imfilter (i_green, filter);
    i_blue = imfilter (i_blue, filter);
    i_gray = imfilter (i_gray, filter);

    figure (1)
    [C, H] = imcontour (i_gray);
    whos
    disp(C(1,[1000:1006]))
    disp(C(2,[1000:1006]))
    title(sprintf('Intensity image (%d)', index))

    % El canal rojo no parece interesante para localizar los centros
    %figure (2)
    %imcontour (i_red);
    %title(sprintf('Red channel (%d)', index))

    figure (3)
    imcontour (i_green);
    title(sprintf('Green channel (%d)', index))

    figure (4)
    imcontour (i_blue);
    title(sprintf('Blue channel (%d)', index))

    pause
end
