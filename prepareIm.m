function outputImage = prepareIm (inputImage, strElType, strElSize)

    source globals.m;
    global umbral;

    image = imread (inputImage);
    image = im2bw (image, umbral); % transform RGB intensity levels to logical image

    outputImage = image;

end
