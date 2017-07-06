% edgeCanny
function outputImage = edgeCanny (inputImage, sgm, tlow, thigh)

    % http://www.peterkovesi.com/matlabfns/Spatial/hysthresh.m

    % Gaussian filter
    mu = 0;
    size_filtro = [3, 3]; % filter 3 x 3
    s1 = size_filtro (1)-1;
    s2 = size_filtro (2)-1;
    [r, c] = meshgrid(0:s2, 0:s1);
    r = r-s2/2;
    c = c-s1/2;
    gauss = exp( -( r.^2 + c.^2 ) / (2*sgm^2) ); % el centro es 1
    filter = gauss / sum (gauss (:)); % la matrix suma 1

    filteredImg = uConvolutionGeneral (inputImage, filter);

    %filter = fspecial ('gaussian', 3, 0.5);
    %outputImage = conv2 (inputImage, filter, 'same');

    % Use sobel as border detector
    border_detector_size = 3;
    gy = fspecial ('sobel', border_detector_size);
    gx = fspecial ('sobel', border_detector_size)';

    ix = uConvolutionGeneral (filteredImg, gx);
    iy = uConvolutionGeneral (filteredImg, gy);

%    figure(1)
%    imshow(iy)
%    figure(2)
%    imshow(ix)

    [rows, cols] = size (inputImage);
    indexes = reshape(1:rows*cols,rows,cols);
    bds = border_detector_size / 2; 
    centers = indexes (ceil(bds/2):rows-(floor(bds/2)), ceil(bds/2):cols-(floor(bds/2)));

%    coners_ = find (ix);
%    ix_borders = centers (coners_); % all X borders

    % Non-max supression
    a = atan (iy./ix);
    nan_pos = isnan(a); % atan calculus produces some NaN values on the matrix borders
    a(nan_pos) = 0; % removes those NaN values
    Eo = mod((180 * a / pi) + 360, 180); % orientation matrix
    Em = sqrt (ix.^2 .+ iy.^2); % magnitude matrix

    d1 = (Eo>0 & Eo<45);    % 0º to 45º 
    d2 = (Eo>=45 & Eo<90);  % 45º to 90º 
    d3 = (Eo>=90 & Eo<135); % 90º to 135º 
    d4 = (Eo>=135);         % 135º to 180º

    cd1 = intersect (indexes, find(d1));
    cd1( Em(cd1)<Em(cd1+1) ) = [];
    cd1( Em(cd1)<Em(cd1-1) ) = [];

    cd2 = intersect (indexes, find(d2));
    cd2( Em(cd2)<Em(cd2-rows-1) ) = [];
    cd2( Em(cd2)<Em(cd2+rows+1) ) = [];

    cd3 = intersect (indexes, find(d3));
    cd3( Em(cd3)<Em(cd3-rows) ) = [];
    cd3( Em(cd3)<Em(cd3+rows) ) = [];

    cd4 = intersect (indexes, find(d4));
    cd4( Em(cd4)<Em(cd4-rows+1) ) = [];
    cd4( Em(cd4)<Em(cd4+rows-1) ) = [];

    supressedIdx = union (cd1, cd2);
    supressedIdx = union (supressedIdx, cd3);
    supressedIdx = union (supressedIdx, cd4);

    t = setdiff (indexes, supressedIdx);
    iy(t) = 0;
    ix(t) = 0;

%    figure(3)
%    imshow (iy);
%
%    figure(4)
%    imshow (ix);

    % normalize tlow & thigh    
    max_iy = max (iy(:));
    min_iy = min (iy(:));
    r2 = max_iy - min_iy; % 
    tlow = (tlow * r2) + min_iy; % a * r2 + min
    thigh = (thigh * r2) + min_iy; % a * r2 + min

    points_d1 = find(d1)
    while(numel(points_d1) > 0)
        if (ismember (points_d1(1) + rows))
            tmp = tmp (points_d1(1) + rows))
        endif
    endwhile

%    % Show results
%    [y,x] = get_relative_indexes (rows,cols,ix_borders);
%    printf('Detected points x gradient: %s\n', num2str(numel(ix_borders)));
%    %figure (9,'visible','off')
%    figure (8);
%    imshow(filteredImg)
%    hold on
%    %imshow(255*ones(rows,cols))
%    plot(x,y,"color","r",".");
%    hold off
%    %print (gcf(), "outputImage.png", '-dpng')
%
%    outputImage = inputImage;

endfunction
% END edgeCanny


%
%
%
function [v0,v1] = drawCircle (x, y, r)
    
    t = linspace(0,2*pi,30)';
    cos_x = r * cos(t);
    sin_y = r * sin(t);

    e = numel (x); % let's repeat t
    cos_x_rep = repmat (cos_x,1,e);
    sin_y_rep = repmat (sin_y,1,e);

    v0 = cos_x_rep + x';
    v1 = sin_y_rep + y';
    
endfunction


%
%
%
function [y,x] = get_relative_indexes (rows,cols,points)
    % Get corners coordinates (x,y)
    p = zeros (rows,cols);
    p(points) = 1;
    [y,x] = find(p);
endfunction
