function [borVert, borHoriz] = derivatives (inputImage, operator)

	% Operador para bordes horizontales, gradiente vertical
	opH = uSpecial(operator);
	borHoriz = convolve (inputImage, opH, 'full');
	
	% Operador para bordes verticales, gradiente horizontal
	opV = opH';
	borVert = convolve (inputImage, opV, 'full');

end