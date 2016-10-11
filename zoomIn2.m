% http://www.giassa.net/?page_id=207

function outputImage = zoomIn2 (inputImage, mode)
	image = imread (inputImage);
	image = n2onedim (image);
	[x y] = size (image);

	new_x = 2 * x;
	new_y = 2 * y;
	
	s1 = new_x / (x-1);
	s2 = new_y / (y-1);

	outputImage = zeros (new_x,new_y);

	% Vectorized version
	c1 = 0:new_x-1;
	c2 = 0:new_y-1;
	outputImage(c1+1,c2+1) = image(1+round(c1/s1), 1+round(c2/s2));

	% Iterative version
% 	for c1 = 0:new_x-1
% 		for c2 = 0:new_y-1
% 			outputImage(c1+1,c2+1) = image(1+round(c1./s1), 1+round(c2./s2));
% 		endfor
% 	endfor
