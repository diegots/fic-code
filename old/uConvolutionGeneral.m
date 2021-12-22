function res = uConvolutionGeneral (mat, kernel)

    mat = double (mat);
    [r, c] = size (mat);
    [kr, kc] = size (kernel);

    kernel = reshape( (kernel(:))(end:-1:1), kr, kc)

    indexes = reshape(1:r*c,r,c);
    firstMatConv = indexes (1:kr,1:kc)(:);
    centers = indexes (ceil(kr/2):r-(floor(kr/2)), ceil(kc/2):c-(floor(kc/2)));
    numberOfCerters = numel (centers);
    increments = (indexes (1:r-ceil(kr/2), 1:c-ceil(kc/2)) - 1)(:)';
    matConvolutions = repmat (firstMatConv, 1, numberOfCerters)+increments;
    allKernels = repmat (kernel(:),1,numberOfCerters);

    matConvolutions = mat (matConvolutions) .* allKernels;
    matConvolutions = sum (matConvolutions, 1);

    res = double (zeros (r,c));
    res(centers) = matConvolutions;

    % conv2 returns the same matrix as res calculated here but if kernel has 
    % negative values such as a Sobel kernel, values are multiplied by -1
%    if ( eq(sum(kernel(:)), 0) )
%        res = res * (-1);
%    endif
    % This code is not really needed. It's just neccessary to reverse the 
    % filter like was seen in the lecture.
    
endfunction % end script
