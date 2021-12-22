% Implementar una funcíon que calcule un kernel Gaussiano horizontal 1xN, a 
% partir de sigma que será pasado como parámetro, y calculando N como 
% N = 2 * |3*sigma| + 1
function kernel = gaussKernel1D (sigma)
	N = uNvalue(sigma);
	
	% Los elementos que hay que calcular por la fórmula de Gauss tienen como 
	% centro el cero en incrementan de forma simétrica respecto a él ya que son
	% distancias
	N_positivo = floor( (N-1)/2 );
	N_positivos = 1:1:N_positivo;
	N_negativos = -N_positivo:1:-1;
	distancias = [N_negativos 0 N_positivos];
	
	disp(sprintf('[gaussKernel1D] Filtro de tamaño %d', numel(distancias)))
	disp('[gaussKernel1D] Vector de distancias:')
	disp(distancias)
	
	% Calculo de cada valor según la fómula de Gauss
	kernel = exp ( - distancias.^2 / (2*pi*sigma^2)) / (2*pi*sigma^2);
	
	% Se divide cada elemento entre en el valor de la suma. Así se consigue un
	% kernel que cuyos elementos suman 1
	kernel = kernel / sum(kernel);
	
	disp('[gaussKernel1D] Kernel Gaussiano 1D calculado:')
	disp(kernel)
		
end


