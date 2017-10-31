function outputImage = uThresholding (image, threshold)

    WHITE = 1;

	disp(sprintf('[uThresholding] Aplicando umbralización con parámetro: %d', ...
		threshold))
	
    image(image < threshold) = 0;
    image(image >= threshold) = WHITE;
    outputImage = image;

end % end script
