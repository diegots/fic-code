function res = uThresholding (image, threshold)

    image = uReadImage (image);

    image(image<threshold)=0;
    image(image>=threshold)=255;
    res = image;

endfunction % end script
