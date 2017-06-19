function outputImage = opening ( inputImage , strElType, strElSize )

    outputImage = erode (inputImage, strElType, strElSize);
    outputImage = dilate (outputImage, strElType, strElSize);

    %% Test octave's implementation
    %kernel = uCreateKernel (strElType, strElSize);
    %outputImage = imopen (inputImage, kernel);
    %%

endfunction
