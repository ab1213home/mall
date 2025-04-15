local cartKey = ARGV[1] .. KEYS[1]
local productId = ARGV[2]
local versionKey = ARGV[3]
local changedSetKey = ARGV[4]
local expireTime = tonumber(ARGV[5])
local delResult = redis.call('HDEL', cartKey, productId)
if delResult > 0 then
    -- 检查并添加用户ID到变更集合，仅在新增时更新版本号
    local added = redis.call('SADD', changedSetKey, KEYS[1])
    if added == 1 then
        redis.call('HINCRBY', versionKey, KEYS[1], 1)
    end
end
redis.call('EXPIRE', cartKey, expireTime)
return delResult