function outputImage = tophatFilter ( inputImage , strElType, mode )

    THRESHOLD = 125;
    strElSize = 3;


    if (strcmp(mode, 'white'))
        %outputImage = opening (inputImage, strElType, strElSize);
        %outputImage = inputImage - outputImage;

        % Octav'e default implementation
        kernel = uCreateKernel (strElType, strElSize);
        outputImage = imtophat (inputImage, kernel);

    elseif strcmp(mode, 'black')
        black = closing (inputImage, strElType, strElSize);
        outputImage = black - inputImage;

        %% Test octave's default implementation
        % To compare this impl with octave's to it with an BLACK & WHITE input
        % image. Octave impl works with gray-scale images.
        %kernel = uCreateKernel (strElType, strElSize);
        %outputImage = imbothat (inputImage, kernel);
    else
        printf('Please select while or black mode.\n')
    endif

endfunction
