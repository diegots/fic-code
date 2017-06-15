function outputImage = opening ( inputImage , strElType, strElSize )

    THRESHOLD = 125;

    image = uThresholding (inputImage, THRESHOLD);
    kernel = uCreateKernel (strElType, strElSize);
    outputImage = uConvolution (image, kernel, 'erode');
    outputImage = uConvolution (outputImage, kernel, 'dilate');

endfunction
