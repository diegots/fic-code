function outputImage = erode ( inputImage , strElType, strElSize )

    THRESHOLD = 125;
    WHITE = 255;

    image = uThresholding (inputImage, THRESHOLD);
    kernel = uCreateKernel (strElType, strElSize);
    outputImage = uConvolution (image, kernel, 'erode');
    outputImage (find (outputImage)) = WHITE; % draw ones as WHITE -> 255

endfunction
