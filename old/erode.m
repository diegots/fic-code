function outputImage = erode (inputImage , strElType, strElSize)

    % THIS IS A BLACK & WHITE EROSION!
    % BEHAVIOUR UNDEFINED WITH MORE THAN BLACK & WHITE SOURCES!

    WHITE = 255;

    image = inputImage;

    kernel = uCreateKernel (strElType, strElSize);
    outputImage = uConvolution (image, kernel, 'erode');
    outputImage (find (outputImage)) = WHITE; % draw ones as WHITE

endfunction
