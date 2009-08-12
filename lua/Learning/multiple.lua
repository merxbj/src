function Inc(value) --value incrementing helper
	
	if (not value) then
		return 1
	else
		return value + 1
	end
end

function Add(value, addition) --value adding helper
	
	local val 
	if (not value) then val = 0 else val = value end
	if (not addition) then add = 0 else add = addition end
	
	return val + add
end

local results = {}

results["KEY"] = {
	["member"] = 10
}

results["KEY"] = {
	["member2"] = 11
}

print(results["KEY"].member)
print(results["KEY"].member2)