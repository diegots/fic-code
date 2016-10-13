function outputImage = n2onedim (image)
	dims = ndims (image);
	if dims == 3
		outputImage = rgb2gray (image);
	end
	outputImage = image;
