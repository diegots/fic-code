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

    % Relevant points
    vectorOnes = find (image)'; %cols

    strElSizeHalf = (strElSize -1) / 2;
    if strcmp (strElType, "square")

    elseif strcmp (strElType, "cross")

    elseif strcmp (strElType, "linev")
        % Pruning edge elements
        topRow = [1:r:r*c];
        vectorOnes = setdiff(vectorOnes, topRow);
        bottomRow = [r:r:r*c];
        vectorOnes = setdiff(vectorOnes, bottomRow);

        vectorEe = [-strElSizeHalf:1:-1 0 1:strElSizeHalf](:); % rows
        ee = vectorOnes + vectorEe;

    elseif strcmp (strElType, "lineh")

    endif

    whos
    outputImage(ee) = 255 * ones(1,1:numel(ee)); % 255 * cols

end
