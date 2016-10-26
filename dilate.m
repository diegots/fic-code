% Dilation and erosion examples
% http://homepages.inf.ed.ac.uk/rbf/HIPR2/dilate.htm

% Dilating 'ones'
function outputImage = dilate (inputImage, strElType, strElSize)

    % Threshold used to obtain binary images
    umbral = 0.69;

    image = imread (inputImage);
    image = im2bw (image, umbral); % transform RGB intensity levels to logical image

    [r c] = size (image);
    vr = 1:r;
    vc = 1:c;

    outputImage = zeros (r, c, "uint8");

    % All relevant points: all 'ones', including those that shouldn't be 
    % processed because of the used kernel.
    vectorOnes = find (image)'; %cols

    p = reshape ( [1: r*c], r, c);
    strElSizeHalf = (strElSize -1) / 2;

    if strcmp (strElType, "square")

    elseif strcmp (strElType, "cross")

    elseif strcmp (strElType, "linev")
        p = p (strElSizeHalf+1 : r-strElSizeHalf, :);
        p = setdiff (vectorOnes, p);
        vectorOnes = setdiff (vectorOnes, p);

        vectorEe = [-strElSizeHalf:1:-1 0 1:strElSizeHalf](:); % rows
        ee = vectorOnes + vectorEe;

    elseif strcmp (strElType, "lineh")
        p = p (:, strElSizeHalf+1 : c-strElSizeHalf);
        p = setdiff (vectorOnes, p);
        vectorOnes = setdiff (vectorOnes, p);

        vectorEe = r * [-strElSizeHalf:1:-1 0 1:strElSizeHalf](:); % rows
        ee = vectorOnes + vectorEe;

    endif

    outputImage(ee) = 255 * ones(1,1:numel(ee)); % 255 * cols

end
