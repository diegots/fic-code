% inputImage : ruta a la imagen
% minValue : valor mínimo del histograma para la imagen 
% maxValue : valor máximo del histograma para la imagen
function outputImage = histShrink (inputImage , minValue , maxValue )

    if (exist(inputImage) != 2)
        printf('You must provide "inputImage"\n\n');
        return
    endif
    if (exist('minValue') != 1)
        printf('"minValue" parameter is missing\n\n');
        return
    endif
    if (exist('maxValue') != 1)
        printf('"maxValue" parameter is missing\n\m');
        return
    endif


    image = imread (inputImage);
    ndims(image)
    if (ndims (image) == 3)
        image = rgb2gray (image);
    end
    image = double (image);
    maxOrig = max (image(:)); 
    minOrig = min (image(:));
    antigMaxMin = maxOrig - minOrig;
    nuevoMaxMin = maxValue - minValue;
    [ fil, col ] = size (image);
    numElems = fil * col;

    nuevaImagen = zeros (fil, col);
    nuevaImagen(1:numElems) = minValue + ( ( nuevoMaxMin * 
        ( double(image(1:numElems)) - minOrig ) ) / antigMaxMin );
    nuevaImagen = uint8 ( reshape (nuevaImagen, fil, col) );
    
    image = uint8 (image);
    figure (1)
    subplot (1,2,1)
    imhist (image); % imhist (image, 256) % para imágenes de 8 bits     
    axis ([0, 255]) % valores mínimo y máximo para el eje X
    subplot (1,2,2)
    imhist (nuevaImagen); 
    axis ([0, 255])

    figure (2)
    imshow (image);
    text (1, -14, "Imagen original", "fontsize", 20)

    figure (3)
    imshow (nuevaImagen);
    text (1, -14, "Imagen nueva", "fontsize", 20)

end
