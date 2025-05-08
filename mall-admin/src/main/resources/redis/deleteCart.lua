local cartKey = ARGV[1] .. KEYS[1]
        local productId = ARGV[2]
        local delta = tonumber(ARGV[3])
        local versionKey = ARGV[4]
        local changedSetKey = ARGV[5]
        local expireTime = tonumber(ARGV[6])
        -- 增加或减少商品数量
        local newVal = redis.call('HINCRBY', cartKey, productId, delta)
        -- 如果新的数量小于等于0，则从购物车中删除该商品
        if newVal <= 0 then
            redis.call('HDEL', cartKey, productId)
            newVal = 0 -- 修改为0以表示商品已被移除
        end
        -- 更新变更集合
        local added = redis.call('SADD', changedSetKey, KEYS[1])
        if added == 1 then
            redis.call('HINCRBY', versionKey, KEYS[1], 1)
        end
        -- 设置过期时间
        redis.call('EXPIRE', cartKey, expireTime)
        return newVal