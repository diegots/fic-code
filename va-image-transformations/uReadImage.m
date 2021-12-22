
function res = uReadImage (inputImage)
% uReadImage abre la ruta pasada por parámetro como una imagen y devuelve
% una matriz de tipo double. Las imágenes RGB son convertidas a un sólo canal.
% Ejemplo de uso:
% I = uReadImage('images\lenna512.bmp');
    image = imread (inputImage);
    if (ndims (image) == 3)
        image = rgb2gray (image);
    end

    res = im2double (image);
end
