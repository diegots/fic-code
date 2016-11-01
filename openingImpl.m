function outputImage = openingImpl (image, strElType, strElSize)

    image = erodeImpl (image, strElType, strElSize); % erode first
    outputImage = dilateImpl (image, strElType, strElSize); % then dilate
end
