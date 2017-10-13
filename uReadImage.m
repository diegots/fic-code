
function res = readImage (inputImage)
% Ejemplo de carga de imagen:
% uReadImage('images\lenna512.bmp')
    image = imread (inputImage);
    if (ndims (image) == 3)
        image = rgb2gray (image);
    end

    res = uint8 (image);
end
