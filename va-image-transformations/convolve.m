% Convoluci�n 2D
% shape = 'full' | 'same'
% shape puede ser full, para realizar la convoluci�n tambi�n por los bordes y
% devolviendo una imagen expandida.
% shame realiza la convoluci�n sin expandir la imagen. Las zonas por las que no
% se puede pasar el kernel se dejan a cero
function outputImage = convolve (inputImage , kernel, shape)
	disp('[convolve] Llamando a uConvolve')
	outputImage = uConvolve (inputImage, kernel, shape, '2dconvo');
		
	% Implementaci�n de convoluci�n de MATLAB
	%outputImage = conv2(inputImage, kernel, shape);
	
end
