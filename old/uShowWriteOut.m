function uShowWriteOut (original, new, mode)

%    [a b] = size (original);
%    if (a == 1) % assuming is a string
%        printf ('Reading original image\n')
%        original = uReadImage (original);
%    endif

    if (strcmp (mode, 'both'))
        write (new);
        show (original, new);
    elseif (strcmp (mode, 'write'))
        write (new);

    else % just show
        show (original, new)

    endif

    function write (new)
        outputImageStr = "new-image.png";
        imwrite (uint8(new), outputImageStr);
        printf("Written new image to '%s'\n", outputImageStr);
    endfunction

    function show (original, new)
        figure (1)
        imshow (uint8(original))
        text (1, -14, "Imagen original", "fontsize", 20)

        figure (2)
        imshow (uint8(new))
        text (1, -14, "Imagen nueva", "fontsize", 20)
    endfunction

endfunction
