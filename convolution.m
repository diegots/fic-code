% Kernel is always an square of odd size: 3x3, 5x5, ...

function outputImage = convolution (image, kernel)

    filterSize = numel (kernel);

    function m = subMatrix(p, filterSide, y, x)

        left = [-filterArea:+1:-1];
        right = [1:filterArea];

        y_axis = [ left 0 right ];
        m1 = ones(filterSide) .* y_axis(:);

        x_axis = y .* y_axis;
        clear left right y_axis;
        m2 = ones(filterSide) .* x_axis;
        clear x_axis;
        
        m3 = (m1 + m2)(:);
        clear m1 m2;
        m = p(:)' .+ m3;
    end

    % Assuming filter is a square: filterSize = sqrt(filterSize) * sqrt(filterSize).
    filterSide = sqrt (filterSize);
    filterArea = (filterSide-1) / 2;
    
    % Read input image, initialize output image
    [y x] = size (image);

    coords = reshape ([1:y*x], y, x);
    first = median ( coords ([1:filterSide]    , [1:filterSide])    (:) );
    last  = median ( coords ([y-filterSide+1:y], [x-filterSide+1:x])(:) );
    
    f1 = filterSide - filterArea;
    f2 = y - filterArea;
    f3 = x - filterArea;
    pixels = coords(f1:f2, f1:f3);

    m = subMatrix (pixels, filterSide, y, x);
    
    outputImage = zeros (y, x);
    t = sum( image(m) .* kernel );
    outputImage( pixels(:) ) = t(:);
end
