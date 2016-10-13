% http://www.giassa.net/?page_id=207

function outputImage = zoomIn2 (inputImage, mode)


    %%%%%%%%%%%%% Neighbor interpolation %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    function outputImage = neighbor()
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
    function outputImage = bilinear()

    end
    %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


    % Check that mode argument is valid %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    if not (strcmp (mode, "bilinear") || strcmp (mode, "neighbor"))
        disp("Not valid interpolation mode. Stopping.")
    endif

    % Read input image, initialize output image
    image = imread (inputImage);
    image = n2onedim (image); % transform RGB intensity levels into one
    [x y] = size (image); % actual image pixels
    
    new_x = 2 * x; % new image pixels
    new_y = 2 * y;

    outputImage = zeros (new_x,new_y); % reserve new image memory

    if strcmp (mode, "bilinear") % Do bilinear interpolation 
        disp("Doing bilinear interpolation.")    

    elseif strcmp (mode, "neighbor") % Do neighbor interpolation
        disp("Doing Neighbor interpolation.")    
        outputImage = neighbor();
    endif
end
