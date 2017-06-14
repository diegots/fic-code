%
% Performs convolution over a matrix 'mat' with desired kernel
%
function res = uConvolution (mat, kernel, operation)

    [c r] = size (kernel); % kernel cols and rows number

    % distante from kernel central point to the border
    output = idivide ([c r], 2, 'floor');
    colKer = output(1); % 
    rowKer = output(2); % 

    % matrix of indexes for the whole mat. From 1 : colMat*rowMat
    [colMat rowMat] = size (mat);
    p = reshape (1:colMat*rowMat, colMat, rowMat);

    actualOnes = find (kernel);

    cross=uCreateKernel('cross',c);
    actualCrossOnes = find (cross);

    if (all (actualOnes == actualCrossOnes)) % cross kernel
        printf('Cross kernel detected\n')

        points1 = p(1+colKer:colMat-colKer, 1+rowKer:rowMat-rowKer)(:); % central
        a1 = repmat(points1, 1,r) + [-colKer:1:colKer];
        points2 = p(1+colKer:colMat-colKer, 1+rowKer:rowMat-rowKer)(:); % central
        a2 = repmat(points2, 1,c) + [-colMat*colKer:colMat:colMat*colKer];
        points = cat (2,a1,a2)';
        
        setTo = all (mat(points));
        mat(points1) = setTo;
    endif

    res = mat;

endfunction % end script

