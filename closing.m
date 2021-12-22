function outputImage = closing (inputImage, strElType, strElSize)
	
	% El cierre es una dilatación seguida de un cierre en la misma imagen
	o = dilate (inputImage, strElType, strElSize);
	outputImage = erode (o, strElType, strElSize);
end