local current = tonumber(redis.call('GET', KEYS[1]))
local requested = tonumber(ARGV[1])

if current == nil then
    return -1
end

if current < requested then
    return -2
end

return redis.call('DECRBY', KEYS[1], requested)
