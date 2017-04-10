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
    
    [rows cols] = size (image);
    
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
        
        col_repetitions = calc_grow_vectors(cols); % get cols
        positions = 1 : length ( image'(:) );
        repetitions = repmat (col_repetitions(:), 1, rows)(:)';
        whos
        s = repelems (image', [positions; repetitions]);
        s = reshape (s, sum(col_repetitions), rows);
        
        row_repetitions = calc_grow_vectors (rows); % get rows
        [rows, cols] = size (s);
        positions = 1 : length ( s(:) );
        repetitions = repmat (row_repetitions(:), 1, rows)(:)';
        s = repelems (s', [positions; repetitions]);
        s = reshape (s, sum(row_repetitions), sum(col_repetitions));
        [rows, cols] = size (s);
        
        printf("New image size: %dx%d px\n", rows, cols);
        imshow (s)
        imwrite (s, "new-image.png")
        
    else
        printf('Bad mode requested. Try again with "bilinear" or "neighbor"\n\n');
        return
    endif
    
    
    % Helper function: calculates grow vectors
    function v = calc_grow_vectors (vector)
        new_vector = floor ( escale * vector )
        vec_grow = zeros (1, vector);
        
        div = floor (new_vector / vector);
        modulus = mod(new_vector, vector);
        
        odd_range = 1 : 2 : vector;
        even_range = 2 : 2 : vector;
        
        vec_grow(odd_range) = 1 * div;
        vec_grow(even_range) = 1 * div;

        % add modulus part if modulus is not 0
        if modulus >= 1
            
            if escale >= 1 % Increasing image
                p = 1 : (new_vector / modulus) : vector;
                p = floor(p);
            else % Reducing image
                p = 1 : (1 / escale) : vector;
                p = floor(p);
            endif
            
            odd_extra = p (mod(p,2)!=0);
            even_extra = p (mod(p,2)==0);
            
            [r c] = size (odd_extra);
            if r >= 1 && c >= 1
                vec_grow(odd_extra) += 1;
            endif
            
            [r c] = size (even_extra);
            if r >= 1 && c >= 1
                vec_grow(even_extra) += 1;
            endif
        endif
        
        v = vec_grow
               
    endfunction % end calc_grow_vectors
    
endfunction % end script