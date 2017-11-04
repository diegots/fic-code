function [gx, gy] = derivatives (inputImage, operator)

	opY = uSpecial(operator);
	opX = opY';

	gy = convolve (inputImage, opY, 'full');
	gx = convolve (inputImage, opX, 'full');

end