
function res = uReadImage (inputImage)
    image = imread (inputImage);
    %ndims(image)
    if (ndims (image) == 3)
        image = rgb2gray (image);
    end
    res = double (image);
endfunction

