-- Atomic token deduction script
-- KEYS[1] = user quota key (e.g., "quota:{userId}")
-- ARGV[1] = amount to deduct
-- Returns: remaining balance if successful, -1 if insufficient balance, -2 if key not exists

local key = KEYS[1]
local deductAmount = tonumber(ARGV[1])

local currentBalance = redis.call('GET', key)

if currentBalance == false then
    return -2
end

currentBalance = tonumber(currentBalance)

if currentBalance < deductAmount then
    return -1
end

local newBalance = currentBalance - deductAmount
redis.call('SET', key, tostring(newBalance))

return newBalance
