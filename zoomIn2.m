% zoomIn2 applies Interpolation to an image. Implements Nearest Neighbor and
% Bilinear algorithm
% inputImage: greyescale image
% mode: either "neighbor" or bilinear
% escale: factor applied to inputImage
function outputImage = zoomIn2 ( inputImage , mode, escale )
    
    if (exist(inputImage) != 2)
        printf('A path to a image is needed!\n\n');
        return
    endif
    if (exist('mode') != 1)
        printf("What 'mode' should be used? Both neighbor and bilinear are supported\n\n");
        return
    endif
    if (exist('escale') != 1)
        printf('Please, provide desired scale factor\n\n');
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
        
        new_rows = escale*rows;
        new_cols = escale*cols;
        p_rows = ( (1 : floor(new_rows/rows) : new_rows) ...
            + (floor(new_rows/rows/2)) ) (1:rows);
        p_cols = ( (1 : floor(new_cols/cols) : new_cols) ...
            + (floor(new_cols/cols/2)) ) (1:cols);
        outputImage = zeros (new_rows, new_cols);
        outputImage(p_rows, p_cols) = image (1:rows, 1:cols);
        outputImage (2:new_rows-1, 2:new_cols-1) ...
            = uint8 (floor(
                  outputImage (1:new_rows-2, 1:new_cols-2)   ...
                + outputImage (1:new_rows-2,   3:new_cols)   ...
                + outputImage (3:new_rows,     1:new_cols-2) ...
                + outputImage (3:new_rows,     3:new_cols  )  )/4 );

        show_results (outputImage) 
        outputImage = uint8 (outputImage);

    %
    % Neighbor Interpolation
    %
    elseif strcmp (mode, 'neighbor')
        printf('Interpolating image by neighbor method\n');
        
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
        
        show_results (s) 
        outputImage = s;
        
    else
        printf('Bad mode requested. Try again with "bilinear" or "neighbor"\n\n');
        return
    endif
    
    % Show results
    function res = show_results (res_image)
        outputImageStr = "new-image.png";

        [rows, cols] = size (res_image);
        printf("New image size: %dx%d px\n", rows, cols);
        imshow (res_image)
        msg = strcat ("New size: ", int2str(rows), " x ", int2str(cols), " px");
        text (30, cols+30, msg, "fontsize", 12)
        imwrite (res_image, outputImageStr)
        printf("Written output image to '%s'\n", outputImageStr);
        
    endfunction % end show_results
    
    % Helper function: calculates grow vectors
    function v = calc_grow_vectors (vector)
        new_vector = floor ( escale * vector );
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
        
        v = vec_grow;
               
    endfunction % end calc_grow_vectors
    
endfunction % end script
