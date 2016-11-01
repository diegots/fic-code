function outputImage = closingImpl (image, strElType, strElSize)
    image = dilateImpl (image, strElType, strElSize); % dilate first
    outputImage = erodeImpl (image, strElType, strElSize); % then erode
end
