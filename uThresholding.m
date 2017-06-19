function outputImage = uThresholding (image, threshold)

    WHITE = 255;

    image(image < threshold) = 0;
    image(image >= threshold) = WHITE;
    outputImage = image;

endfunction % end script
