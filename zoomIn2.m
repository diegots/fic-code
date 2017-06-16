% zoomIn2 applies Interpolation to an image. Implements Nearest Neighbor and
% Bilinear algorithm
% inputImage: greyscale image
% mode: either "neighbor" or bilinear
% scale: factor applied to inputImage
function outputImage = zoomIn2 (inputImage , mode, scale)
    
    image = inputImage;
    
    [rows cols] = size (image);
    printf('Original image size: %s x %s px\n', int2str(cols), int2str(rows));
    
    %
    % Bilinear Interpolation
    %
    if strcmp (mode, 'bilinear')
        printf('Interpolating image by bilinear method\n');
        
        new_cols = floor( scale*cols );
        new_rows = floor( scale*rows );

        col_repetition = calc_grow_vectors (cols);

%        p_rows = ( (1 : floor(new_rows/rows) : new_rows) ...
%            + (floor(new_rows/rows/2)) ) (1:rows);
%        p_cols = ( (1 : floor(new_cols/cols) : new_cols) ...
%            + (floor(new_cols/cols/2)) ) (1:cols);
%        outputImage = zeros (new_rows, new_cols);
%        outputImage(p_rows, p_cols) = image (1:rows, 1:cols);
%        outputImage (2:new_rows-1, 2:new_cols-1) ...
%            = uint8 (floor(
%                  outputImage (1:new_rows-2, 1:new_cols-2)   ...
%                + outputImage (1:new_rows-2,   3:new_cols)   ...
%                + outputImage (3:new_rows,     1:new_cols-2) ...
%                + outputImage (3:new_rows,     3:new_cols  )  )/4 );
%
%        show_results (outputImage) 
%        outputImage = uint8 (outputImage);

    %
    % Neighbor Interpolation
    %
    elseif strcmp (mode, 'neighbor')
        printf('Interpolating image by neighbor method\n');
        
        [rows cols] = size (image);
        col_repetitions = calc_grow_vectors(cols); % get cols
        positions = 1 : length ( image'(:) );
        repetitions = repmat (col_repetitions(:), 1, rows)(:)';
        s = repelems (image', [positions; repetitions]);
        s = reshape (s, sum(col_repetitions), rows);
        
        row_repetitions = calc_grow_vectors (rows); % get rows
        [rows, cols] = size (s);
        positions = 1 : length ( s(:) );
        repetitions = repmat (row_repetitions(:), 1, rows)(:)';
        s = repelems (s', [positions; repetitions]);
        s = reshape (s, sum(row_repetitions), sum(col_repetitions));
        
        outputImage = s;
        
    else
        printf('Bad mode requested. Try again with "bilinear" or "neighbor"\n\n');
        return
    endif
    
    [rows cols] = size (outputImage);
    printf('New image size: %s x %s px\n', int2str(cols), int2str(rows));
   
    % Helper function: calculates how many points should be put between two 
    % points in the original matrix. Example: with 10 px in the original
    % image and a scale of 1.5 in between each point must be,
    % 2 1 1 2 1 1 2 1 1 2 points
    function v = calc_grow_vectors (vector)
        new_vector = floor ( scale * vector );
        vec_grow = zeros (1, vector);
        
        div = floor (new_vector / vector );
        modulo = mod (new_vector, vector );
        
        odd_range = 1 : 2 : vector;
        even_range = 2 : 2 : vector;
        
        % Base values. This is what can be distributed equaly
        vec_grow(odd_range) = 1 * div;
        vec_grow(even_range) = 1 * div;

        % add modulus part if modulus is not 0
        if modulo >= 1
            
            if scale >= 1 % Increasing image
                p = linspace (1, vector, modulo);
                p = floor(p);
            else % Reducing image
                p = 1 : (1 / scale) : vector;
                p = floor(p);
            endif
            
            odd_extra = p ( mod(p,2) != 0 ); % Positions that will host extra item
            even_extra = p ( mod(p,2) == 0 );
            
            [c r] = size (odd_extra);
            if c >= 1 && r >= 1
                vec_grow(odd_extra) += 1;
            endif
            
            [c r] = size (even_extra);
            if c >= 1 && r >= 1
                vec_grow(even_extra) += 1;
            endif
        endif
        
        v = vec_grow;
               
    endfunction % end calc_grow_vectors
    
endfunction % end script
