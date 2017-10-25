function kernel = gaussKernel2D (sigma)

	N = uNvalue(sigma);
	
	% A partir de N e igual que para el caso de gaussKernel1D se calculan dos 
	% matrices de distancias, una para el eje X y otra para el Y.
	N_positivo = floor( (N-1)/2 );
	N_positivos = 1:1:N_positivo;
	N_negativos = -N_positivo:1:-1;
	distancias = [N_negativos 0 N_positivos];
	x = repmat(distancias, numel(distancias), 1);

	distancias = [fliplr(N_positivos) 0 fliplr(N_negativos)];
	y = repmat(distancias', 1, numel(distancias));
	disp('[gaussKernel2D] Matriz de distancias eje X:')
	disp(x)
	disp('[gaussKernel2D] Matriz de distancias eje Y:')
	disp(y)
	
	% Calculo de cada valor según la fómula de Gauss (para dos valores)
	kernel = exp ( - (x.^2 + y.^2) / (2*pi*sigma^2)) / (2*pi*sigma^2);
	
	% Se divide cada elemento entre en el valor de la suma. Así se consigue un
	% kernel que cuyos elementos suman 1
	kernel = kernel / sum(sum(kernel));
	
	disp('[gaussKernel2D] kernel Gaussiano calculado:')
	disp(kernel)
	

end