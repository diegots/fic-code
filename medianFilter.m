function outputImage = medianFilter ( inputImage , filterSize )

    % Read image
    mat = uReadImage (inputImage);

    % Create kernel and do the transformation
    kernel = createKernel (filterSize);
    outputImage = convolution(mat, kernel);

    if (outputImage == -1)
        return;
    endif

    % Write image to file
    outputImage = uint8 (outputImage);
    outputImageStr = "new-image.png";
    imwrite (outputImage, outputImageStr)

    % Show original and new images
    figure (1)
    imshow (uint8(mat))
    text (1, -14, "Imagen original", "fontsize", 20)

    figure (2)
    imshow (outputImage)
    text (1, -14, "Imagen nueva", "fontsize", 20)


    %
    function res = createKernel (filterSize)
        res = ones (filterSize, filterSize);
    endfunction % end script


    %
    %
    function res = convolution (mat, kernel)
        [c r] = size (kernel);
        output = idivide ([c r], 2, 'floor');
        colKer = output(1); 
        rowKer = output(2);

        if (c <= 1 || r <= 1)
            printf ("Operation stoped. Please, provide a kernel size bigger than 1.\n")
            res = -1;
            return;
        endif

        % matrix of indexes for the whole mat. From 1 : colMat*rowMat
        [colMat rowMat] = size (mat);
        p = reshape (1:colMat*rowMat, colMat, rowMat);

        if (c == colMat || r == rowMat)
            printf ("Operation stoped. Please, provide a kernel size smaller than the image size.\n")
            res = -1;
            return;
                
        endif

        % actual points that have to be calculated by the convolution
        points = p([1+colKer:colMat-colKer] , [1+rowKer:rowMat-rowKer]);
        [colPoints rowPoints] = size (points);

        % cols and rows for the inicial convolution
        cols = 1:c;
        rows = 1:r;

        % number of points that will be calculated
        number_of_points = length (points(:));
        v = p(cols, rows)(:);
        i = (repmat ( v, 1, number_of_points) );
        base = (p-1)(1:colPoints,1:rowPoints)(:)';
        index = i + base;
        
        %
        selected = mat ( index (:, 1:number_of_points) );
        values = ceil (length (selected (:,1) ) / 2); % media position

        % calculate median
        selectedSorted = sort (selected);
        medianValues = selectedSorted (values, :);

        % assign values
        res = zeros (colMat, rowMat);
        res (points) = medianValues;

    endfunction % end script

endfunction % end script
