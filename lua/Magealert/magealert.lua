-- I'd like to encapsulate all the methods and members to one single table (like a class)
-- But this is not necessary - we can simply ged rid off this by removing of following line
-- and all the MageAlert prefixes from both lua file and xml file
local MageAlert = {}

-- Thin wrapper around adding messages to chat frame
function MageAlert:LogMessage(msg)
	DEFAULT_CHAT_FRAME:AddMessage(msg)
end

-- This will only show a message whether the addon is enabled
function MageAlert:ReportAddonStatus()
	
	if (MageAlert.Enabled) then
		MageAlert:LogMessage("Mage alert: Enabled!")
	else
		MageAlert:LogMessage("Mage alert: Disabled!")
	end
end

-- Simple help message
function MageAlert:ShowHelpMessage()
	MageAlert:LogMessage("MageAlert: Usage is: \"enabled\" for enabling / disabling the addon")
end

-- slash commands event handler
function ParseCommandLine(cmd)

	-- command is stored as a string in variable cmd
	-- we can use something like that:
	if (cmd == "enabled") then
		MageAlert.Enabled = not MageAlert.Enabled
		MageAlert:ReportAddonStatus() -- Report whether we are enabled or disabled
	else
		MageAlert:ShowHelpMessage() -- We have used wrong argument! Show us a little help message
	end
end

-- OnLoad event handler
function OnLoad()

	MageAlert:LogMessage("MageAlert Addon loaded!")
	
	-- register slash commands 
	SLASH_MAGEALERT1 = "/magealert"
	SLASH_MAGEALERT2 = "/mg"
	
	-- register slash commands event handler
	SlashCmdList["MAGEALERT"] = ParseCommandLine
	
	-- register for event notification
	this:RegisterEvent("COMBAT_LOG_EVENT_UNFILTERED")

	-- set our addon enabled by default
	MageAlert.Enabled = true
end

-- OnEvent event handler - this function is called everytime when Combat Log fires an event (hit, miss, auras, etc...) from MageAlert.xml
function OnEvent(self, event, ...) 

	local eventType = select(2, ...) -- select 2nd argument from "...", we want to know what kind of event we have caught
	local srcName = select(4, ...) -- select 4nd argument from "...", we want to know who is source of our event
	local auraName = select(10, ...) -- finally select 10nd argument from "...", we want to know what is the aura name

	-- following code speaks for itself :-)
	if (MageAlert.Enabled) then
		if ((eventType == "SPELL_AURA_APPLIED") and (srcName == UnitName("player"))) then
			if (auraName == "Fireball!") then
				PlaySoundFile("Interface\\AddOns\\MageAlert\\Sounds\\Alert.mp3")
			elseif (auraName == "Fingers of Frost") then
				PlaySoundFile("Interface\\AddOns\\MageAlert\\Sounds\\Alarm.mp3")
			end
		end
	end
end
