function outputImage = dilate ( inputImage , strElType, strElSize )

    THRESHOLD = 125;

    image = uThresholding (inputImage, THRESHOLD);
    kernel = uCreateKernel (strElType, strElSize);
    outputImage = uConvolution (image, kernel, 'dilate');

endfunction
