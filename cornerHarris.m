% información: https://technicache.wordpress.com/2010/11/24/harris-corner-detector-in-matla/
% Implementación del detector de esquinas Harris
% N : tamaño de la zona de vecindad
% t : valor del umbral para las esquinas
function outputImage = cornerHarris (inputImage, N, t)
    k = 0.04; % entre 0.04 y 0.06

    [r,c] = size (inputImage);

    % derivatives
    %gx = [-1 0; 0 1];
    %gy = [0 -1; 1 0];

    gx = fspecial ('sobel', 3);
    gy = fspecial ('sobel', 3)';

    ix = uConvolutionGeneral (inputImage, gx);
    iy = uConvolutionGeneral (inputImage, gy);

    mat = double (inputImage);
    [r, c] = size (mat);
    kr = N;
    kc = N;

    indexes = reshape(1:r*c,r,c);
    firstMatConv = indexes (1:kr,1:kc)(:);
    centers = indexes (ceil(kr/2):r-(floor(kr/2)), ceil(kc/2):c-(floor(kc/2)));
    numberOfCerters = numel (centers);
    increments = (indexes (1:r-ceil(kr/2), 1:c-ceil(kc/2)) - 1)(:)';
    matConvolutions = repmat (firstMatConv, 1, numberOfCerters)+increments;

    ix2 = sum(ix(matConvolutions).^2, 1);
    iy2 = sum(iy(matConvolutions).^2, 1);
    ixiy = sum(ix(matConvolutions) .* iy(matConvolutions), 1);

    v = 1:numberOfCerters;
    S = reshape ([ix2(v); ixiy(v); ixiy(v); iy2(v)],2,2,numberOfCerters);

    % create objects
    dets = zeros(1,numberOfCerters);
    traces = zeros(1,numberOfCerters);
    M = zeros (1,numberOfCerters);
    
    for i = v
        s_ = S(:,:,i);
        dets(i) = det (s_);
        traces(i) = trace (s_) ^ 2;

        %M(i) = det (S(:,:,i)) - k * (trace (S(:,:,i)) ^ 2);
    endfor
    M(v) = dets(v) - k * traces(v);

    i = sqrt (numel(M));
    M = reshape(M, i, i);
    (M>t);
    coners_ = find (M>t);
    centers;
    corners = centers (coners_);

    p = zeros (r,c);
    p(corners) = 1;
    [y,x] = find(p);

    printf('Detected points %s\n', num2str(numel(x)));

    outputImage = inputImage;
    [v0,v1] = drawCircle (x,y,3);
    figure (1)
    imshow(inputImage)
    hold on
    plot(v0,v1,"color","r","-")
    hold off

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

endfunction

