-- The index starts at 1
-- Limit Key
local key = KEYS[1]
-- Limit size
local limit = tonumber(ARGV[1])

-- Gets current limit
local currentLimit = tonumber(redis.call('get', key) or "0")

if currentLimit + 1 > limit then
    return 0;
else
    redis.call("INCRBY", key, 1)
    redis.call("EXPIRE", key, 2)
    return curentLimit + 1
end

