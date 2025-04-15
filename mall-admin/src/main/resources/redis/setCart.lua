local cartKey = ARGV[1] .. KEYS[1]
local productId = ARGV[2]
local delta = tonumber(ARGV[3])
local versionKey = ARGV[4]
local changedSetKey = ARGV[5]
local expireTime = tonumber(ARGV[6])

local newVal = redis.call('HINCRBY', cartKey, productId, delta)
if newVal <= 0 then
    redis.call('HDEL', cartKey, productId)
    newVal = 1
end

-- 检查并添加用户ID到变更集合，仅在新增时更新版本号
local added = redis.call('SADD', changedSetKey, KEYS[1])
if added == 1 then
    redis.call('HINCRBY', versionKey, KEYS[1], 1)
end

redis.call('EXPIRE', cartKey, expireTime)
return newVal