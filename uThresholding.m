function res = uThresholding (image, threshold)

    image(image<threshold)=0;
    image(image>=threshold)=1;
    res = image;

endfunction % end script
