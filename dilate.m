% Dilation and erosion examples
% http://homepages.inf.ed.ac.uk/rbf/HIPR2/dilate.htm

% Dilating 'ones'
function outputImage = dilate (inputImage, strElType, strElSize)

    % Threshold used to convert RGB or gray images to logical
    umbral = 0.69;

    image = imread (inputImage);
    image = im2bw (image, umbral); % transform RGB intensity levels to logical image

    % Actual size of the input image
    [r c] = size (image);

    % Reserve space to store final image
    outputImage = zeros (r, c, "uint8");

    % All relevant points: all 'ones', including those that shouldn't be 
    % processed because of the used kernel's shape.
    vectorOnes = find (image)'; %cols

    % p starts by containing all posible image indexes but is then pruned to
    % represent only the indexes that the kernel can or should process.
    p = reshape ( [1: r*c], r, c );

    strElSizeHalf = (strElSize -1) / 2;

    % square kernel
    if strcmp (strElType, "square")
	p = p (strElSizeHalf+1 : r-strElSizeHalf, strElSizeHalf+1 : c-strElSizeHalf);
        p = setdiff (vectorOnes, p);
        vectorOnes = setdiff (vectorOnes, p);

        vectorEe = [-strElSizeHalf:1:-1 0 1:strElSizeHalf](:);
	vectorEe_1 = ones(strElSize) .* vectorEe;
        vectorEe_2 = ones(strElSize) .* ( r * [-strElSizeHalf:1:-1 0 1:strElSizeHalf]);
	vectorEe = [vectorEe_1 + vectorEe_2](:);

        ee = vectorOnes + vectorEe;

    % cross kernel
    elseif strcmp (strElType, "cross")
        p = p (strElSizeHalf+1 : r-strElSizeHalf, :);
        p = p (:, strElSizeHalf+1 : c-strElSizeHalf)
        p = setdiff (vectorOnes, p);
        vectorOnes = setdiff (vectorOnes, p)

        vectorEe_1 = [-strElSizeHalf:1:-1 0 1:strElSizeHalf](:);% rows
        vectorEe_2 = r * [-strElSizeHalf:1:-1 0 1:strElSizeHalf](:); % rows
        vectorEe = union (vectorEe_1, vectorEe_2);

        ee = vectorOnes + vectorEe;

    % vertical line kernel
    elseif strcmp (strElType, "linev")
        p = p (strElSizeHalf+1 : r-strElSizeHalf, :);
        p = setdiff (vectorOnes, p);
        vectorOnes = setdiff (vectorOnes, p);

        vectorEe = [-strElSizeHalf:1:-1 0 1:strElSizeHalf](:); % rows
        ee = vectorOnes + vectorEe;

    % horizontal line kernel
    elseif strcmp (strElType, "lineh")
        p = p (:, strElSizeHalf+1 : c-strElSizeHalf);
        p = setdiff (vectorOnes, p);
        vectorOnes = setdiff (vectorOnes, p);

        vectorEe = r * [-strElSizeHalf:1:-1 0 1:strElSizeHalf](:); % rows
        ee = vectorOnes + vectorEe;

    endif

    outputImage(ee) = 255 * ones(1,1:numel(ee)); % 255 * cols

end
