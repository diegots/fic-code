function outputImage = closing (inputImage , strElType, strElSize)

    outputImage = dilate (inputImage, strElType, strElSize);
    outputImage = erode (outputImage, strElType, strElSize);

    %% Test octave's implementation
    %kernel = uCreateKernel (strElType, strElSize);
    %outputImage = imclose (image, kernel);
    %% 

endfunction
