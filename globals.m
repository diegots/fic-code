% Global variables used by morphology operations
global umbral = 0.69; % Threshold used to convert RGB or gray images to logical
global strelsize = 5; % Size used in tophatFilter

% Prewitt kernel values
global prewittKerX = [-1 -1 -1 0 0 0 1 1 1]; 
global prewittKerY = [-1 0 1 -1 0 1 -1 0 1];

% Sobel kernel values
global sobelKerX [-1 -2 -1 0 0 0 1 2 1];
global sobelKerY [-1 0 1 -2 0 2 -1 0 1];
