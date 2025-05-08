local cartKey = ARGV[1] .. KEYS[1]
local productsStr = ARGV[2]
local versionKey = ARGV[3]
local changedSetKey = ARGV[4]
local expireTime = tonumber(ARGV[5])

-- 解析商品列表
for product in string.gmatch(productsStr, "[^,]+") do
    local prodId, num = string.match(product, "([^:]+):([^:]+)")
        if prodId and num then
            local delta = tonumber(num)
            if delta ~= nil then
                local newVal = redis.call('HINCRBY', cartKey, prodId, delta)
            if newVal <= 0 then
                redis.call('HDEL', cartKey, prodId)
            end
        end
    end
end

-- 更新变更集合和版本号
local added = redis.call('SADD', changedSetKey, KEYS[1])
    if added == 1 then
        redis.call('HINCRBY', versionKey, KEYS[1], 1)
end

-- 设置过期时间
redis.call('EXPIRE', cartKey, expireTime)

return 1