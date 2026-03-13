local current = tonumber(redis.call('GET', KEYS[1]))
local quantity = tonumber(ARGV[1])

if current == nil then
    return -1
end

return redis.call('INCRBY', KEYS[1], quantity)
