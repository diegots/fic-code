function outputImage = tophatFilter (inputImage, strElType, mode)

    source globals.m;
    global strelsize;

    image = prepareIm (inputImage);

    if strcmp(mode, "white")
        imageOpened = openingImpl (image, strElType, strelsize);
        outputImage = image - imageOpened;

    elseif strcmp(mode, "black")
        imageClosed = closingImpl (image, strElType, strelsize);
        outputImage = imageClosed - image;

    else
        error ("Mode not valid");
    endif
end
