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
        a1 = repmat(points1, 1,r) + [-colKer:1:colKer]; % extension
        points2 = p(1+colKer:colMat-colKer, 1+rowKer:rowMat-rowKer)(:); % central
        a2 = repmat(points2, 1,c) + [-colMat*colKer:colMat:colMat*colKer]; % extension
        points = cat (2,a1,a2)'; % all points por central
        
        if (strcmp (operation, 'erode'))
            setTo = all (mat(points)); % if all points are 1
            mat(points1) = setTo;
        elseif (strcmp (operation, 'dilate'))
            points_ = cat (points1,points2)';
            centerOnes = find ((mat(points_)));
            allOnes = points(:,centerOnes);
            mat (allOnes) = 1;
        endif
    endif

    res = mat;

endfunction % end script

