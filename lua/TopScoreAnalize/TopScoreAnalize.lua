dofile("d:\\Hry\\World of Warcraft\\WTF\\Account\\ETERNITYJM\\Wildhammer\\Mexbik\\SavedVariables\\FuBar_TopScoreFu.lua")

print("")

local results = {}
local index = 0

for key, value in pairs(TopScoreFuPCDB.global.hits) do
	if (value) then
		local hitInfo = value.crit
		
		if (hitInfo.class and hitInfo.amount) then
			index = index + 1
			results[index] = {
				["spell"] = key,
				["amount"] = hitInfo.amount,
				["class"] = hitInfo.class
			}
		end

	end
end

local sortFunction = function(a, b) -- sort me descendingly
	if (a.amount > b.amount) then
		return true
	end
end

table.sort(results, sortFunction)

for key, value in ipairs(results) do
	print(string.format("%16s %6d %15s", value.spell, value.amount, value.class))
end
