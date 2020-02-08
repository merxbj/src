function LogSpells_OnLoad()
  this:RegisterEvent("COMBAT_LOG_EVENT_UNFILTERED");
  DEFAULT_CHAT_FRAME:AddMessage("LogSpells addon loaded!");
end

allowedSpells = {
	["Frostbolt"] = true,
	["Frostfire Bolt"] = false,
	["Fireball"] = true,
	["Ice Lance"] = true,
	["Cone of Cold"] = true,
	["Fire Blast"] = true,
	["Living Bomb"] = false,
	["Scorch"] = false,
	["Frost Nova"] = true,
	["Arcane Explosion"] = true,
	["Blizzard"] = true,
	["Arcane Missiles"] = true,
	["Shoot"] = false,
	["Molten Armor"] = false,
	["Attack"] = false
}

function IsAllowedSpell(spell)
	return allowedSpells[spell]
end

function LogSpells_OnEvent(self, event, ...)

	local timestamp, type, sourceGUID, sourceName, sourceFlags, destGUID, destName, destFlags = select(1, ...)

	if (event == "COMBAT_LOG_EVENT_UNFILTERED") then
		if (type == "SPELL_DAMAGE") then

			local spellName = select(10, ...)
			local amount = select(12, ...)

			if (IsAllowedSpell(spellName) or true) then
				DEFAULT_CHAT_FRAME:AddMessage(spellName.." caused "..amount.." damege!");
			end
		
		elseif (event == "SWING_DAMAGE") then
		
			local amount = select(12, ...)
			
			DEFAULT_CHAT_FRAME:AddMessage("Melee swing caused "..amount.." damege!");
		end
	end
end
