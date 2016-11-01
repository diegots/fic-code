function outputImage = opening (inputImage, strElType, strElSize)

    image = prepareIm (inputImage);
    outputImage = openingImpl (image, strElType, strElSize);
end
