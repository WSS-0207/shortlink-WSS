-- 设置用户访问频率限制的参数
local username = KEYS[1]
local timeWindow = tonumber(ARGV[1]) -- 时间窗口，单位:秒


-- 构造Redis中存储用户访问次数的键名
local accessKey = "short-link:user-flow-risk-control:" .. username

-- 原子递增访问次数，并获取递增后的值
local currentAccessCount = redis.call("INCR", accessKey)

-- 如果 currentAccessCount 为 nil，说明 INCR 操作失败
if not currentAccessCount then
    return {err = "Failed to increment access count"}
end

-- 设置键的过期时间，确保访问计数仅在指定时间窗口内有效
redis.call("EXPIRE", accessKey, timeWindow)

-- 返回当前访问次数
return currentAccessCount
