% Convolución 2D
% shape = 'full' | 'same'
% shape puede ser full, para realizar la convolución también por los bordes y
% devolviendo una imagen expandida.
% shame realiza la convolución sin expandir la imagen. Las zonas por las que no
% se puede pasar el kernel se dejan a cero
function outputImage = convolve (inputImage , kernel, shape)
	disp('[convolve] Llamando a uConvolve')
	outputImage = uConvolve (inputImage, kernel, shape, '2dconvo');
		
	% Implementación de convolución de MATLAB
	%outputImage = conv2(inputImage, kernel, shape);
	
end
