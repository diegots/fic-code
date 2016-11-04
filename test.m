%%%%%%%%%%%%%% histShrink %%%%%%%%%%%%%%
i = "image_samples/lena512.bmp";
[orig, orig_map] = imread (i);

out = histShrink (i, 10, 233);
whos

figure("name", "Histogram Shrink");
subplot (1,2,1); imshow(orig);
title("Original image", "fontsize",14);

subplot (1,2,2); imshow(out, orig_map);
title("Shrinked image", "fontsize",14);
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


%%%%%%%%%%%%%% ZoomIn2 %%%%%%%%%%%%%%
i = "image_samples/lena512.bmp";
[orig, orig_map] = imread (i);

% Test neighbor interpolation
out = zoomIn2(i ,"neighbor");
whos

figure("name", "Neighbor Zoom");
subplot (1,2,1); imshow(orig);
title("Original image", "fontsize",14);

subplot (1,2,2); imshow(out, orig_map);
title("Zoomed-in image", "fontsize",14);

% Test bilinear interpolation
out = zoomIn2(i ,"bilinear");
whos

figure("name", "Bilinear Zoom");
subplot (1,2,1); imshow(orig);
title("Original image", "fontsize",14);

subplot (1,2,2); imshow(out, orig_map);
title("Zoomed-in image", "fontsize",14);
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


%%%%%%%%%%%%%% Smooth %%%%%%%%%%%%%%

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
