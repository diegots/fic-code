function res = uCreateKernel (type, filterSize)

    % Median kernel is just an ones's square with the desired size
    if (strcmp(type, 'median') )
        res = ones (filterSize, filterSize);

    elseif (strcmp(type, 'cross') )
        res = zeros (filterSize,filterSize);
        reshape ([1:filterSize*filterSize],filterSize,filterSize);
        full = reshape ([1:filterSize*filterSize],filterSize,filterSize);
        point = ceil (filterSize / 2);
        v1 = full (point, :)';
        v2 = full (:, point);
        res (v1) = 1;
        res (v2) = 1;

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

