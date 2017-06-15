function outputImage = closing ( inputImage , strElType, strElSize )

    THRESHOLD = 125;

    image = uReadImage (inputImage);
    image = uThresholding (image, THRESHOLD);
    kernel = uCreateKernel (strElType, strElSize);
    outputImage = uConvolution (image, kernel, 'dilate');
    outputImage = uConvolution (outputImage, kernel, 'erode');

endfunction
