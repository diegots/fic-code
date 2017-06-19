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

%originalImg = uReadImage (lenna);
%originalImg = uReadImage (building);
originalImg = uReadImage (landscape);

% histShrink
%new = histShrink(originalImg, 60, 230);

% zoomIn2
%new = zoomIn2 (originalImg, 'neighbor' , 0.5);
%new = zoomIn2 (originalImg, 'neighbor' , 2.3);
%new = zoomIn2 (originalImg, 'bilinear' , 2.3);

% medianFilter
%new = medianFilter (originalImg, 7);

% erode
%image = uThresholding (originalImg, THRESHOLD);
%new = erode (image, 'square', 3);
%new = erode (image, 'linev', 3);
%new = erode (image, 'lineh', 7);
%new = erode (image, 'cross', 5);

% dilate
%image = uThresholding (originalImg, THRESHOLD);
%new = dilate (image, 'square', 7);
%new = dilate (image, 'linev', 5);
%new = dilate (image, 'lineh', 9);
%new = dilate (image, 'cross', 3);

% opening
%image = uThresholding (originalImg, THRESHOLD);
%new = opening (image, 'cross', 5);

% closing
%image = uThresholding (originalImg, THRESHOLD);
%new = closing (image, 'cross', 5);

% tophat - white tophat 
image = uThresholding (originalImg, THRESHOLD);
new = tophatFilter (image, 'linev', 'white');

% bothat or black tophat
%image = uThresholding (originalImg, THRESHOLD);
%new = tophatFilter (image, 'square', 'black');

% show or write
uShowWriteOut (originalImg, new, ''); % by default show
%uShowWriteOut (originalImg, new, 'write'); % by default, '', display result
