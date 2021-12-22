% inputImage : ruta a la imagen
% minValue : valor mínimo del histograma para la imagen 
% maxValue : valor máximo del histograma para la imagen
function outputImage = histShrink (inputImage , minValue , maxValue )

    % double conversion needed to perform division below
    image = double (inputImage);

    maxOrig = max (image(:)); 
    minOrig = min (image(:));
    antigMaxMin = maxOrig - minOrig;
    nuevoMaxMin = maxValue - minValue;
    [ fil, col ] = size (image);
    numElems = fil * col;

    nuevaImagen = zeros (fil, col);
    nuevaImagen(1:numElems) = minValue + ( ( nuevoMaxMin * 
        ( double(image(1:numElems)) - minOrig ) ) / antigMaxMin );
    outputImage = uint8 ( reshape (nuevaImagen, fil, col) );
    
    % show histogram
    image = uint8 (image);
    figure (9)
    subplot (1,2,1)
    imhist (image); % imhist (image, 256) % para imágenes de 8 bits     
    axis ([0, 255]) % valores mínimo y máximo para el eje X
    xlabel ("Histograma original")

    subplot (1,2,2)
    imhist (outputImage); 
    axis ([0, 255])
    xlabel ("Histograma nuevo")

end
