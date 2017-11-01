function outputImage = opening (inputImage, strElType, strElSize)
	
	% La apertura es una erosión seguida de una dilatación en la misma imagen
	o = erode (inputImage, strElType, strElSize);
	outputImage = dilate (o, strElType, strElSize);
end