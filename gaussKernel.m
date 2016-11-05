function kernel = gaussKernel (kerSize, sigma)

    len = (kerSize-1) / 2;
    col = [-len:len] .* ones(kerSize, 1);
    row = col';

    kernel = e .^ (-(row.^2 + col.^2) / (2 * sigma^2));
    kernel = kernel / sum (kernel(:));
end
