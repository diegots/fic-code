% http://www.giassa.net/?page_id=207

function outputImage = zoomIn2 (inputImage, mode)


    %%%%%%%%%%%%% Neighbor interpolation %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    function outputImage = neighbor(outputImage)
        % Vectorized version
        c1 = 0:new_x-1;
        c2 = 0:new_y-1;

        s1 = new_x / (x-1);
        s2 = new_y / (y-1);

        outputImage(c1+1,c2+1) = image(1+round(c1/s1), 1+round(c2/s2));
        
        % Iterative version (really slow)
    %    for c1 = 0:new_x-1
    %    	for c2 = 0:new_y-1
    %    		outputImage(c1+1,c2+1) = image(1+round(c1./s1), 1+round(c2./s2));
    %    	endfor
    %    endfor

    end
    %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


    %%%%%%%%%%%%% Bilinear interpolation %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    function outputImage = bilinear(image, x, y, outputImage)

        b = (y-1) / (2*y - 1) * [0:2*y-1]; % cols
        cu_q = 1 + floor(b); % current
        ne_q = 1 + ceil(b); % next
        b = b - fix(b);

        a = (x-1) / (2*x - 1) * [0:2*x-1]; % rows
        cu_p = 1 + floor(a); % current
        ne_p = 1 + ceil(a); % next
        a = [a - fix(a)](:);

        outputImage(1:2*x, 1:2*y) = (1-a) .* ( (1-b) .* image(cu_p, cu_q) + b .* image(cu_p, ne_q) ) + ...
                                       a  .* ( (1-b) .* image(ne_p, cu_q) + b .* image(ne_p, ne_q) );

    end
    %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


    % Check that mode argument is valid %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    if not (strcmp (mode, "bilinear") || strcmp (mode, "neighbor"))
        disp("Not valid interpolation mode. Stopping.")
    endif

    % Read input image, initialize output image
    image = imread (inputImage);
    image = n2onedim (image); % transform RGB intensity levels into one
    [x y] = size (image); % number of (rows, cols) on the input image
    
    new_x = 2 * x; % new image pixels
    new_y = 2 * y;

    outputImage = zeros (new_x, new_y); % reserve new image memory

    if strcmp (mode, "bilinear") % Do bilinear interpolation 
        disp("Doing bilinear interpolation.")    
        outputImage = bilinear(image, x, y, outputImage);

    elseif strcmp (mode, "neighbor") % Do neighbor interpolation
        disp("Doing Neighbor interpolation.")    
        outputImage = neighbor(outputImage);
    endif
end
