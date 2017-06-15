% Code for calling and showing all implemented functions

% uConvolution
%mat = round (100 * rand (7,7) );
%ker = uCreateKernel ('square', 3);
%ker = uCreateKernel ('linev', 3);
%uConvolution (mat, ker, 'erode');

THRESHOLD = 125;
WHITE = 255;

% images
lenna     = 'images/lenna512.bmp';
building  = 'images/building.jpg';
landscape = 'images/landscape.jpg';

% histShrink
%new = histShrink(lenna, 50, 170);

% zoomIn2
%new = zoomIn2 (building, 'neighbor' , 2.3);
%new = zoomIn2 (building, 'bilinear' , 2.3);

% medianFilter
%new = medianFilter (landscape, 7);

% erode
%new = erode (landscape, 'square', 3);
%new = erode (building, 'linev', 5);
%landscape = uThresholding (landscape, THRESHOLD);

% dilate
%new = dilate (landscape, 'square', 5);
%landscape = uThresholding (landscape, THRESHOLD);

% opening
new = opening (building, 'cross', 5);
%building = uThresholding (building, THRESHOLD);

% closing
%new = opening (building, 'cross', 5);
%building = uThresholding (building, THRESHOLD);

% tophat
%new = tophatFilter (building, 'cross', 'white');

% show or write
uShowWriteOut (building, new, ''); % by default show
