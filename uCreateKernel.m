function res = uCreateKernel (type, filterSize)

    if (strcmp(type, 'median') )
        res = ones (filterSize, filterSize);

    elseif (strcmp(type, 'cross') )
        full = reshape ([1:filterSize*filterSize],filterSize,filterSize);
        point = ceil (filterSize / 2);
        v1 = full (point, :)';
        v2 = full (:, point);
        res = cat (1, v1, v2);
        res = sort (unique (res));

    elseif (strcmp(type, 'square') )
        res = reshape ([1:filterSize*filterSize],filterSize,filterSize)(:);

    elseif (strcmp(type, 'lineh') )
        full = reshape ([1:filterSize*filterSize],filterSize,filterSize);
        point = ceil (filterSize / 2);
        v1 = full (point, :)';
        res = sort (v1);
        
    elseif (strcmp(type, 'linev') )
        full = reshape ([1:filterSize*filterSize],filterSize,filterSize);
        point = ceil (filterSize / 2);
        v2 = full (:, point)';
        res = sort (v2);

    endif
endfunction % end script

