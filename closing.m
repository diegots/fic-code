function outputImage = closing (inputImage, strElType, strElSize)

    image = prepareIm (inputImage);
    outputImage = closingImpl (image, strElType, strElSize);
end
