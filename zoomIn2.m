% zoomIn2 applies Interpolation to an image. Implements Nearest Neighbor and
% Bilinear algorithm
% inputImage: greyscale image
% mode: either "neighbor" or bilinear
% scale: factor applied to inputImage
function outputImage = zoomIn2 (inputImage , mode, scale)
    
    img = inputImage;
    
    [rows cols] = size (img);
    printf('Original image size: %s x %s px\n', int2str(cols), int2str(rows));
    
    %
    % Bilinear Interpolation
    %
    % https://stackoverflow.com/questions/26142288/resize-an-image-with-bilinear-interpolation-without-imresize
    % https://www.youtube.com/watch?v=z4S5kBl6P84
    % https://archive.org/download/Lectures_on_Image_Processing/EECE_4353_15_Resampling.pdf
    if strcmp (mode, 'bilinear')
        printf('Interpolating image by bilinear method\n');

        new_row = round (rows * scale); % new rows 
        new_col = round (cols * scale); % new cols 
        
        Sr = rows / new_row;
        Sc = cols / new_col;

        [cf, rf] = meshgrid(1:new_row, 1:new_col);

        rf = rf * Sr;
        cf = cf * Sc;

        r = floor(rf); % r0
        c = floor(cf); % c0
        
        r(r < 1) = 1;
        c(c < 1) = 1;
        r(r > rows - 1) = rows - 1;
        c(c > cols - 1) = cols - 1;

        delta_r = rf - r;
        delta_c = cf - c;

        c1 = sub2ind([rows, cols], r, c);
        c2 = sub2ind([rows, cols], r+1 ,c);
        c3 = sub2ind([rows, cols], r, c+1);
        c4 = sub2ind([rows, cols], r+1, c+1);

        outputImage = zeros(rows, cols);

        outputImage = img(c1) .* (1 - delta_r) .* (1 - delta_c) + ...
            img (c2) .* delta_r .* (1 - delta_c) + ...
            img (c3) .* (1 - delta_r) .* delta_c + ...
            img (c4) .* delta_r .* delta_c;

    %
    % Neighbor Interpolation
    %
    elseif strcmp (mode, 'neighbor')
        printf('Interpolating image by neighbor method\n');
        
        [rows cols] = size (img);
        col_repetitions = calc_grow_vectors(cols); % get cols
        positions = 1 : length ( img'(:) );
        repetitions = repmat (col_repetitions(:), 1, rows)(:)';
        s = repelems (img', [positions; repetitions]);
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

