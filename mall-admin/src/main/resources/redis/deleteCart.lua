local cartKey = ARGV[1] .. KEYS[1]
local productId = ARGV[2]
local versionKey = ARGV[3]
local changedSetKey = ARGV[4]
local expireTime = tonumber(ARGV[5])
local delResult = redis.call('HDEL', cartKey, productId)
if delResult > 0 then
    redis.call('HINCRBY', versionKey, KEYS[1], 1)
    redis.call('SADD', changedSetKey, KEYS[1])
end
redis.call('EXPIRE', cartKey, expireTime)
return delResult