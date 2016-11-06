function outputImage = edgeCanny (inputImage, sigma, tlow, thigh)

    orig = imread (inputImage);
    orig = double (n2onedim (orig)) / 255;
    
    % Choose Gauss parameters
    %kernel = fspecial ("gaussian", 3, sigma)(:);
    kernel = gaussKernel (3, sigma);
    out = convolution(orig, kernel);

    source globals.m;
    global sobelKerH;
    global sobelKerV;
    hg = convolution(out, sobelKerH);
    vg = convolution(out, sobelKerV); 

    m = sqrt (hg.^2 + vg.^2);
    o = atan (hg./vg) .* 180 ./ pi .+ 180;
    range = isnan(o);
    [r,c] = size (range);
    range = find (bitxor(range, ones(r,c)));
    angles = arrayfun ("edgeCannyAngle", o(range), "UniformOutput", false);
    o (range) = cell2mat (angles);

end
