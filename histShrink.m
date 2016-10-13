% Working with matrices
% https://www.gnu.org/software/octave/doc/v4.0.1/Matrices.html

% Image concepts
% https://www.mathworks.com/help/matlab/creating_plots/image-types.html
% https://en.wikipedia.org/wiki/Grayscale

% Vectorizing
% https://www.gnu.org/software/octave/doc/v4.0.3/Basic-Vectorization.html
% http://www.variousconsequences.com/2008/10/is-octave-slow.html
% https://software.intel.com/en-us/articles/using-intel-mkl-in-gnu-octave

function outputImage = histShrink (inputImage, minValue, maxValue)
	% Assuming a default intensity range of 256 values, from 0 to 
	% 255.
	min = 0;
	max = 255;
	a = maxValue - minValue;
	b = max - min;

	image = imread (inputImage); % load image data
	image = n2onedim (image); 

	image = double (image);
	[rows cols] = size (image); % get number of cols and rows of image
	outputImage = (zeros(rows,cols));

	% Vectorized version
	outputImage = minValue + ((a * (image (1:rows*cols) - min))/b);

	% Loop version, slower
%	for n=1:(rows * cols)
%		i = image (n);
%		i = (minValue + ( (a*(i-min)) / b ) );
%		outputImage (n) = round(i);
%	endfor

	image = uint8 (image);
	outputImage = uint8 (outputImage);

	subplot(1,2,1); imhist(image);
	subplot(1,2,2); imhist(outputImage);
