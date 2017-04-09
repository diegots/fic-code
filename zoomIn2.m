% zoomIn2 applies Interpolation to an image. Implements Nearest Neighbor and
% Bilinear algorithm
% inputImage: greyescale image
% mode: either "neighbor" or bilinear
% escale: factor applied to inputImage
function outputImage = zoomIn2 ( inputImage , mode, escale)
    
    if (exist(inputImage) != 2)
        printf('You must provide "inputImage"\n\n');
        return
    endif
    if (exist('mode') != 1)
        printf('"mode" parameter is missing\n\n');
        return
    endif
    if (exist('escale') != 1)
        printf('"escale" parameter is missing\n\n');
        return
    endif
    
    image = imread (inputImage);
    
    dims = ndims (image);
    if dims == 3
        image = rgb2gray (image);
    endif
    
    [rows cols] = size (image)
    
    %
    % Bilinear Interpolation
    %
    if strcmp (mode, 'bilinear')
        printf('Interpolating image by bilinear method\n');
    
    %
    % Neighbor Interpolation
    %
    elseif strcmp (mode, 'neighbor')
        printf('Interpolating image by neighbor method\n');
        
        cols_grow_vector = calc_grow_vectors(cols); % get cols
        %rows_grow_vector = calc_grow_vectors(rows); % get rows
        
        
        %i = [1;2;3];
        %cols_grow_vector(i)
        %ones(1, cols_grow_vector(i)) 

        
        %i = image (1,[1:cols]); % select rows or cols
        %o = uint8 (ones(1, cols_grow_vector(1)));
        %(i .* o')(:)';
        
        %i = image ([1:rows], 1); % select rows or cols
        %o = uint8 (ones(rows_grow_vector(1), 1));
        %r =(i' .* o)(:);
        
    else
        printf('Bad mode requested. Try again with "bilinear" or "neighbor"\n\n');
        return
    endif
    
    
    % Helper function: calculates grow vectors
    function v = calc_grow_vectors (vector)
        new_vector = floor ( escale * vector );
        vec_grow = zeros (1, cols);
        
        div = floor (new_vector / vector);
        modulus = mod(new_vector, vector);
        
        odd_range = 1 : 2 : vector;
        even_range = 2 : 2 : vector;
        
        vec_grow(odd_range) = 1 * div;
        vec_grow(even_range) = 1 * div;

        % add modulus part if modulus is not 0
        if modulus >= 1
            odd_extra = 1 : 2 : modulus+1;
            even_extra = 2 : 2 : modulus-1;
            
            [r c] = size (odd_extra);
            if r >= 1 && c >= 1
                vec_grow(odd_extra) += 1;
            endif
            
            [r c] = size (even_extra);
            if r >= 1 && c >= 1
                vec_grow(even_extra) += 1;
            endif
        endif
        
        v = vec_grow;
               
    endfunction % end calc_grow_vectors
    
endfunction % end script