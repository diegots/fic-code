% Dilation and erosion examples
% http://homepages.inf.ed.ac.uk/rbf/HIPR2/dilate.htm

% Dilating 'ones'
function outputImage = dilate (inputImage, strElType, strElSize)

    image = prepareIm (inputImage);
    outputImage = dilateImpl (image, strElType, strElSize);

end
