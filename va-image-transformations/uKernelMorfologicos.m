function res = uKernelMorfologicos (type, filterSize)

	disp(sprintf('[uKernelMorfologicos] Creando kernel tipo "%s" con tamaño %d', ...
		type, filterSize))

    if (strcmp(type, 'cross') )
        
		res = zeros (filterSize,filterSize);
        full = reshape ([1:filterSize*filterSize],filterSize,filterSize);
        point = ceil (filterSize / 2);
        v1 = full (point, :)';
        v2 = full (:, point);
        res (v1) = 1;
        res (v2) = 1;

    elseif (strcmp(type, 'square') )
        res = ones(filterSize,filterSize);

    elseif (strcmp(type, 'lineh') )
        res = zeros (filterSize,filterSize);
        full = reshape ([1:filterSize*filterSize],filterSize,filterSize);
        point = ceil (filterSize / 2);
        v1 = full (point, :)';
        res (v1) = 1;
       
    elseif (strcmp(type, 'linev') )
        res = zeros (filterSize,filterSize);
        full = reshape ([1:filterSize*filterSize],filterSize,filterSize);
        point = ceil (filterSize / 2);
        v2 = full (:, point);
        res (v2) = 1;

    end
end % end script

