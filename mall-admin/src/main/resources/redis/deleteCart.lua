local cartKey = ARGV[1] .. KEYS[1]
local productId = ARGV[2]
local versionKey = ARGV[3]
local changedSetKey = ARGV[4]
local expireTime = tonumber(ARGV[5])
local delResult = redis.call('HDEL', cartKey, ARGV[1])
if delResult > 0 then
    redis.call('HINCRBY', ARGV[3], KEYS[1], 1)
    redis.call('SADD', ARGV[4], KEYS[1])
end
redis.call('EXPIRE', cartKey, expireTime)
return delResult