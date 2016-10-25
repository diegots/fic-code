function outputImage = n2onedim (image)
    dims = ndims (image);
    if dims == 3
        outputImage = rgb2gray (image);
    elseif dims == 2
        outputImage = image;
    else
        error ("Image dimensions is not 2 or 3. Stopping.")
    endif
