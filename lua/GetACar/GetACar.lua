function tryToGetACar()

    --math.randomseed(os.time())
    
    local doors = {}
    local car = math.random()
    
    print(car)
    
    for i = 1, 3 do
        doors[i] = (i == car)
    end
    
    print(doors[1], doors[2], doors[3])
    
    local choose = math.random(1,3)
    
    if doors[choose] then
        local opendoor = math.random(1,3)
    end
        
    return true
    
end


i = 1
wins = 0
attempts = 1
while i <= attempts do

    if (tryToGetACar()) then
        wins = wins + 1
    end
    
    i = i + 1

end

print(string.format("%d wins out of %d attemps!", wins, attempts))