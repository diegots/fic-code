function outputImage = erode ( inputImage , strElType, strElSize )

    THRESHOLD = 125;

    image = uReadImage (inputImage);
    image = uThresholding (image, THRESHOLD);
    kernel = uCreateKernel (strElType, strElSize);
    outputImage = uConvolution (image, kernel, 'dilate');

    figure (1)
    imshow (image)
    text (1, -14, "Imagen original", "fontsize", 20)

    figure (2)
    imshow (outputImage)
    text (1, -14, "Imagen nueva", "fontsize", 20)

endfunction
