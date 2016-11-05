% Global variables used by morphology operations
global umbral = 0.69; % Threshold used to convert RGB or gray images to logical
global strelsize = "5"; % Size used in tophatFilter

% Prewitt kernel values
global prewittKerH = [-1 -1 -1 0 0 0 1 1 1]'; 
global prewittKerV = [-1 0 1 -1 0 1 -1 0 1]';

% Sobel kernel values
global sobelKerV = [-1 -2 -1 0 0 0 1 2 1]';
global sobelKerH = [1 0 -1 2 0 -2 1 0 -1]';

