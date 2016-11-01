% Eroding 'ones'
function outputImage = erode(inputImage, strElType, strElSize)

    image = prepareIm (inputImage);
    outputImage = erodeImpl (image, strElType, strElSize);

end
