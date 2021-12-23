function [v0,v1] = drawCircle (x, y, r)
    t = linspace(0,2*pi,30)';
    cos_x = r * cos(t);
    sin_y = r * sin(t);

    e = numel (x); % let's repeat t
    cos_x_rep = repmat (cos_x,1,e);
    sin_y_rep = repmat (sin_y,1,e);

    v0 = cos_x_rep + x';
    v1 = sin_y_rep + y';
end

