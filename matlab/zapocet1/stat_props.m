function [] = stat_props( filename )

%   This function takes a name of csv file.
%   For every row in the given csv file the following values are
%   calculated:
%       maximum
%       minimum
%       median
%       mean
%       variance
%       mode
%
%   The result is stored into matrix (with rows corresponding to the input
%   rows and columns corresponding to the values in the above mentioned
%   order)
%

    data = csvread(filename);
    
    result = zeros(size(data,1),6);
    
    for i = 1:size(data,1)
        row = data(i,:);
        result(i,1) = min(row);
        result(i,2) = max(row);
        result(i,3) = median(row);
        result(i,4) = mean(row);
        result(i,5) = var(row);
        result(i,6) = mode(row);
    end
    
    disp(result);
        
end

