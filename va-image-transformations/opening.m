function outputImage = opening (inputImage, strElType, strElSize)
	
	% La apertura es una erosi�n seguida de una dilataci�n en la misma imagen
	o = erode (inputImage, strElType, strElSize);
	outputImage = dilate (o, strElType, strElSize);
end