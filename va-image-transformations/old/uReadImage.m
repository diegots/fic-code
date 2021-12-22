
function res = uReadImage (inputImage)

    image = imread (inputImage);
    if (ndims (image) == 3)
        image = rgb2gray (image);
    end

    res = uint8 (image);
endfunction

