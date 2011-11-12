function [ fibValues ] = fib_arr( array )

%   This function calculates a fibonacci number for each element in the
%   given array. The result is then returned as a vector.

fibValues = zeros(size(array));
fibBase = [1 1; 1 0];
fibValuesIdx = 1;

for f = array
    
    fibMatrix = fibBase ^ f;
    fibValues(fibValuesIdx) = fibMatrix(1,2);
    fibValuesIdx = fibValuesIdx + 1;
    
end

end

