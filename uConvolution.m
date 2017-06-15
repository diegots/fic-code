%
% Performs convolution over a matrix 'mat' with desired kernel
%
function res = uConvolution (mat, kernel, operation)

    [c r] = size (kernel);  % kernel cols and rows number

    % distante from kernel central point to the border
    output = idivide ([c r], 2, 'floor');
    colKer = output(1); % 
    rowKer = output(2); % 

    % matrix of indexes for the whole mat. From 1 : colMat*rowMat
    [colMat rowMat] = size (mat);
    p = reshape (1:colMat*rowMat, colMat, rowMat);

    cross=uCreateKernel('cross',c);
    linev=uCreateKernel('linev',c);
    lineh=uCreateKernel('lineh',c);
    square=uCreateKernel('square',c);

    if (kernel == cross) 
        printf('Doing convolution with cross kernel\n')

        points1 = p(1+colKer:colMat-colKer, 1+rowKer:rowMat-rowKer)(:); % central
        a1 = repmat(points1, 1,r) + [-colKer:1:colKer]; % extension
        points2 = p(1+colKer:colMat-colKer, 1+rowKer:rowMat-rowKer)(:); % central
        a2 = repmat(points2, 1,c) + [-colMat*colKer:colMat:colMat*colKer]; % ext
        points = cat (2,a1,a2)'; % all points por central
        
        applyOperation ()

    elseif (kernel == lineh) 
        printf('Doing convolution with lineh kernel\n')

        points1 = p(1+colKer:colMat-colKer, 1+rowKer:rowMat-rowKer)(:);% central
        a2 = repmat(points1, 1,c) + [-colMat*colKer:colMat:colMat*colKer] % ext
        points = a2';

        applyOperation ()

    elseif (kernel == linev) 
        printf('Doing convolution with linev kernel\n')

        points1 = p(1+colKer:colMat-colKer, 1+rowKer:rowMat-rowKer)(:)  % central
        a1 = repmat(points1, 1,r) + [-colKer:1:colKer]; % extension
        points = a1';

        applyOperation ()

    elseif (kernel == square) 
        printf('Doing convolution with square kernel\n')

        points1 = p(1+colKer:colMat-colKer, 1+rowKer:rowMat-rowKer)(:); % central
        a1 = repmat(points1, 1,r) + [-colKer:1:colKer]; % extension
        a1 = repmat (a1,1,c);
        z = [-colMat*colKer:colMat:colMat*colKer]';
        m = repmat (z,r,1);
        m = reshape (m,r,c)'(:)';
        [a b] = size (a1);
        f = repmat (m, a, 1);
        points = (a1 + f)';
        
        applyOperation ();
    endif

    res = mat; % return this matrix

    % Perform erode or dilate with calculated points
    function applyOperation ()
        if (strcmp (operation, 'erode'))
            setTo = all (mat(points)); % if all points are 1
            mat(points1) = setTo;

            %mat = imerode (mat, kernel); % check result 

        elseif (strcmp (operation, 'dilate'))
            pointsInMat = mat (points1);
            centerOnes = find (pointsInMat);
            allOnes = points(:,centerOnes);
            mat (allOnes) = 255;

            %mat = imdilate (mat, kernel); % check result
        endif
    endfunction

endfunction % end script

