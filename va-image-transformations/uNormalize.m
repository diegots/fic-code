function out =  uNormalize (x, minVal, maxVal)
  % normalize Normaliza un vector o matriz entre 0 y 1
  [a,b] = size (x);
  out = (x(1:end)/(maxVal-minVal)) - (minVal/(maxVal-minVal));
  out = reshape (out,a,b);
end
