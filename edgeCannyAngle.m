% edgeCannyAngle puts an angle into one of four categories using a proximity 
% criteria. Categories are: 0, 45, 90 and 135 degrees.
function retval = edgeCannyAngle (alfa)
    incr = 45;
    test = 0;
    while (1) 
        if test > 135
            alfa -= 180;
            test = 0;
            continue;
        endif

        d1 = alfa - test;
        if d1 >= incr
            test += incr;
        else
            d2 = abs (alfa - (test + incr));
            if d1 >= d2
                retval = test+incr;
                break;
            else
                retval = test;
                break;
            endif
        endif
    endwhile
end
