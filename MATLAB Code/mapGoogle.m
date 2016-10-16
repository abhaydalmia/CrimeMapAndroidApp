function raw = mapGoogle(raw, addon)


flag = true;
counter = 2;
while (flag)
    currentLocation = raw{counter,11};
    currentLocation = [strrep(currentLocation,' ','+') '+'  strrep(addon,' ','+')];
    wholeJSON = urlread(['https://maps.googleapis.com/maps/api/geocode/json?address=' currentLocation '&key=AIzaSyBSX0PCGxP9uAX7hgM1Hap2wtwVqodL7V8']);
    counter = counter + 1;
    statusIndex = strfind(wholeJSON,'"status"');
    statusIndex = wholeJSON(statusIndex+12:end);
    status = strtok(statusIndex, '"');
    if strcmp(status, 'OVER_QUERY_LIMIT')
        flag = false;
    elseif strcmp(status, 'OK')
            lngIndex = strfind(wholeJSON,'"lng"');
            latIndex = strfind(wholeJSON,'"lat"');
            lat = str2double(strtok(wholeJSON(latIndex(1)+8:lngIndex(1)),','));
            lng = str2double(strtok(wholeJSON(lngIndex(1)+8:lngIndex(1)+100),' }'));
            raw{counter, 24} = lat;
            raw{counter, 25} = lng;
    end 
    counter
end

end