dofile("exclusions.lua")

function get_file_name(path)

    return path:match("[^\\]*$")
end


total_changes = 0
local function process_single_file(path)
    local file = assert(io.open(path, "r"))
    local temp_path = string.format("%s_1", path)
    local temp_file = assert(io.open(temp_path, "w"))
    local changes = 0
    
    for line in file:lines() do

        local new_line = line
        
        -- at first replace leading tabs with spaces
        leading_tabs = line:match("^\t+")
        if not (leading_tabs == nil) then
        
            local replacement = string.rep(" ", leading_tabs:len() * 4)
            new_line, subs = line:gsub("^\t+", replacement)
            changes = changes + 1
        end
        
        -- then trim trailing spaces
        if (not (new_line:match(" +$") == nil)) then
            
            new_line, subs = new_line:gsub(" +$", "")
            changes = changes + 1
        end

        temp_file:write(string.format("%s\n", new_line))
    
    end
    
    total_changes = total_changes + changes
    
    print(string.format("    Changes made: %d", changes))
    io.close(temp_file)
    io.close(file)
    if changes > 0 then
        assert(os.remove(path))
        assert(os.rename(temp_path, path))
    else
        assert(os.remove(temp_path))
    end
end

function exclude(path)

    local exclude_path = false
    local filename = get_file_name(path)

    for key,exclusion in pairs(exclusions) do

        if not (filename:match(exclusion) == nil) then
            exclude_path = true
            break
        end
    end
    
    return exclude_path
end

local command = "status"
local server = "tfs.radiantsystems.com"
local user = "jmerxbauer"
local workspace = "EURJMERXBAUER1"
local output = "pending.txt"

local command_string = string.format("tf %s /s:%s /user:%s /workspace:%s > %s", command, server, user, workspace, output)

os.execute (command_string)

local file = assert(io.open(output, "r"))
local local_paths = {}

for line in file:lines() do -- get all local paths
    
    local path = line:match(".:\\.+$")
    if not (path == nil) then
        table.insert(local_paths, path)
    end
end

io.close(file)

for key, path in pairs(local_paths) do
    if not exclude(path) then
        print(string.format("PROCESSING: %s", get_file_name(path)))
        process_single_file(path)
    else
        print(string.format("EXCLUDING: %s", get_file_name(path)))
    end
end

print(string.format("\n\n\tTotal Changes made: %d", total_changes))