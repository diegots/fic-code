%%%%%%%%%%%%%% histShrink %%%%%%%%%%%%%%
%i = "image_samples/lena512.bmp";
%[orig, orig_map] = imread (i);
%
%out = histShrink (i, 10, 233);
%whos
%
%figure("name", "Histogram Shrink");
%subplot (1,2,1); imshow(orig);
%title("Original image", "fontsize",14);
%
%subplot (1,2,2); imshow(out, orig_map);
%title("Shrinked image", "fontsize",14);
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


%%%%%%%%%%%%%% ZoomIn2 %%%%%%%%%%%%%%
%i = "image_samples/lena512.bmp";
%[orig, orig_map] = imread (i);
%
%% Test neighbor interpolation
%out = zoomIn2(i ,"neighbor");
%whos
%
%figure("name", "Neighbor Zoom");
%subplot (1,2,1); imshow(orig);
%title("Original image", "fontsize",14);
%
%subplot (1,2,2); imshow(out, orig_map);
%title("Zoomed-in image", "fontsize",14);
%
%% Test bilinear interpolation
%out = zoomIn2(i ,"bilinear");
%whos
%
%figure("name", "Bilinear Zoom");
%subplot (1,2,1); imshow(orig);
%title("Original image", "fontsize",14);
%
%subplot (1,2,2); imshow(out, orig_map);
%title("Zoomed-in image", "fontsize",14);
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


%%%%%%% Smooth: median filter %%%%%%%
%i = "image_samples/lena512.bmp";
%[orig, orig_map] = imread (i);
%
%%out = medianFilter(i , 3*3); % 3x3 filter
%out = medianFilter(i , 5*5); % 5x5 filter
%whos
%
%% Image
%figure("name", "Smooth: Median Filter Result");
%subplot (1,2,1); imshow(orig);
%title("Original image", "fontsize",14);
%
%subplot (1,2,2); imshow(out, orig_map);
%title("Smoothed image", "fontsize",14);
%
%% Histogram
%figure("name", "Smooth: Median Filter Histogram");
%subplot (1,2,1); imhist(orig);
%title("Original image", "fontsize",14);
%
%subplot (1,2,2); imhist(out);
%title("Smoothed image", "fontsize",14);
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


%% Morphological Operators: erode  %%
%i = "image_samples/test-white-column.bmp";
%i = "image_samples/lena512.bmp";
%[orig, orig_map] = imread (i);
%
%EeType = "linev" % possibilities: square, cross, linev, lineh
%EeSize = "5" % "n" kernel actual size is n*n
%out = erode(i, EeType, EeSize); 
%whos
%
%figure("name", "Morphological Operators: erode");
%subplot (1,2,1); imshow(orig);
%title("Original image", "fontsize",14);
%
%subplot (1,2,2); imshow(out, orig_map);
%title("Eroded image", "fontsize",14);
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


%% Morphological Operators: dilate %%
%i = "image_samples/test-white-column.bmp";
%[orig, orig_map] = imread (i);
%
%EeType = "square" % possibilities: square, cross, linev, lineh
%EeSize = "9" % "n" kernel actual size is n*n
%out = dilate(i, EeType, EeSize); 
%whos
%
%figure("name", "Morphological Operators: erode");
%subplot (1,2,1); imshow(orig);
%title("Original image", "fontsize",14);
%
%subplot (1,2,2); imshow(out, orig_map);
%title("Eroded image", "fontsize",14);
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%% Morphological Operators: opening %%
%i = "image_samples/test-white-column.bmp";
%i = "image_samples/lena512.bmp";
%[orig, orig_map] = imread (i);
%
%EeType = "square" % possibilities: square, cross, linev, lineh
%EeSize = "5" % "n" kernel actual size is n*n
%out = opening(i, EeType, EeSize); 
%whos
%
%figure("name", "Morphological Operators: opening");
%subplot (1,2,1); imshow(orig);
%title("Original image", "fontsize",14);
%
%subplot (1,2,2); imshow(out, orig_map);
%title("Opened image", "fontsize",14);
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%% Morphological Operators: closing %%
%i = "image_samples/lena512.bmp";
%%i = "image_samples/test-white-column.bmp";
%[orig, orig_map] = imread (i);
%
%EeType = "square" % possibilities: square, cross, linev, lineh
%EeSize = "9" % "n" kernel actual size is n*n
%out = closing(i, EeType, EeSize); 
%whos
%
%figure("name", "Morphological Operators: closing");
%subplot (1,2,1); imshow(orig);
%title("Original image", "fontsize",14);
%
%subplot (1,2,2); imshow(out, orig_map);
%title("Closed image", "fontsize",14);
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%% Morphological Operators: tophat %%
%i = "image_samples/lena512.bmp";
%i = "image_samples/test-white-column.bmp";
%[orig, orig_map] = imread (i);
%
%EeType = "square"; % possibilities: square, cross, linev, lineh
%mode = "black"; % white, black
%out = tophatFilter (i, EeType, mode); 
%whos
%
%figure("name", "Morphological Operators: tophat");
%subplot (1,2,1); imshow(orig);
%title("Original image", "fontsize",14);
%
%subplot (1,2,2); imshow(out, orig_map);
%title("Tophated image", "fontsize",14);
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


%%%%% Convolution w/Gauss filter %%%%
%i = "image_samples/lena512.bmp";
%i = "image_samples/test-1.bmp";
%
%[orig, orig_map] = imread (i);
%orig = n2onedim (orig);
%
%% Choose Gauss parameters
%%kernel = fspecial ("gaussian", 7, 2);
%kernel = gaussKernel (7, 2);
%
%out = convolution(orig, kernel);
%
%figure("name", "Gauss smooth");
%subplot (1,2,1); imshow(orig);
%title("Original image", "fontsize",14);
%
%subplot (1,2,2); imshow(out, orig_map);
%title("Smoothed image", "fontsize",14);
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
