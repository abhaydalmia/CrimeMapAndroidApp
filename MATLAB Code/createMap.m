function createMap(raw, option)
boxsize = 0.1;
[row, ~] = size(raw);
array=[];
topLat = 33.892103;
leftLong = 84.553532;

currentTop = 33.878039;
currentLeft = -84.529483;
buffer=[];

oneLat = 69;
for i = 2:row
   Lat = str2double(raw{i, 23});
   Long = abs(str2double(raw{i, 22}));
   oneLong = abs(cosd(Lat)*69.172);
   diffLat = (abs(topLat - Lat))*oneLat;
   diffLong = (abs(leftLong - Long))*oneLong;
   yIndex = ceil(diffLat/boxsize);
   xIndex = ceil(diffLong/boxsize);
   [arrR, arrC] = size(array);
   if (xIndex > arrC || yIndex > arrR)
       array(yIndex, xIndex) = 1;
   else
       array(yIndex, xIndex) = array(yIndex, xIndex) + 1;
   end
end

[row, col] = size(array);

currentTop = 33.878039;
finalArray=[];
for i = 1:row
    currentLeft = -84.529483;
    for j = 1:col
        currentBottom = currentTop - 1/oneLat*boxsize;
        oneLong = abs(cosd(currentTop)*69.172);
        currentRight = currentLeft + 1/oneLong*boxsize;
        newRow = [currentTop, currentBottom, currentLeft, currentRight, array(i,j)];
        finalArray = [finalArray; newRow];
        currentLeft = currentRight;
    end
    currentTop = currentBottom;
end
[row, col] = size(finalArray);
[~,sortInd] = sort(finalArray(:,5));
finalArray = finalArray(sortInd,:);
finalArray(:,5) = mat2gray(finalArray(:,5))
for i = 1:row
    if(true)
        if strcmp(option,'avg')
            avgLat = (finalArray(i,1) + finalArray(i,2))/2;
            avgLong = (finalArray(i,3) + finalArray(i,4))/2;
            buffer = [buffer,num2str(avgLat),',',num2str(avgLong),',',num2str(finalArray(i,5)),'\n'];
        else
            buffer = [buffer,num2str(finalArray(i,1)),',',num2str(finalArray(i,2)),',',num2str(finalArray(i,3)),',',num2str(finalArray(i,4)),',',num2str(finalArray(i,5)),'\n'];
        end
    end
end
fh = fopen('finalheatmap2norm.txt','w');
fprintf(fh, buffer);
fclose(fh);
end