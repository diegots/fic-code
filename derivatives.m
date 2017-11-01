function [gx, gy] = derivatives (inputImage, operator)

	opY = uSpecial(operator);
	opX = opY';

	gy = convolve (inputImage, opY, 'same');
	gx = convolve (inputImage, opX, 'same');

end