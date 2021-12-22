% Valor del parámetro N utilizado en los filtros de Gauss
function N = uNvalue (sigma)
	N = 2 * abs(2 * sigma) + 1;
end