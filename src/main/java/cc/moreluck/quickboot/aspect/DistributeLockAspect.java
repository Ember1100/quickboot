package cc.moreluck.quickboot.aspect;

import cc.moreluck.quickboot.annotation.DistributeLock;
import cc.moreluck.quickboot.constant.Constants;
import cc.moreluck.quickboot.exception.DistributeLockException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.StandardReflectionParameterNameDiscoverer;
import org.springframework.core.annotation.Order;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import static cc.moreluck.quickboot.constant.Constants.PREFIX;

/**
 * @Author: huangqi
 * @CreateTime: 2025-04-17
 * @Description:
 */
@Aspect
@Component
@Order(Integer.MIN_VALUE)
public class DistributeLockAspect {

    private final RedissonClient redissonClient;

    public DistributeLockAspect(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    private static final Logger LOG = LoggerFactory.getLogger(DistributeLockAspect.class);

    @Around("@annotation(cc.moreluck.quickboot.annotation.DistributeLock)")
    public Object process(ProceedingJoinPoint pjp) {
        Object response = null;
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        DistributeLock distributeLock = method.getAnnotation(DistributeLock.class);

        String key = distributeLock.key();
        if (Constants.NONE_KEY.equals(key)) {
            if (Constants.NONE_KEY.equals(distributeLock.keyExpression())) {
                throw new DistributeLockException("no lock key found...");
            }
            SpelExpressionParser parser = new SpelExpressionParser();
            Expression expression = parser.parseExpression(distributeLock.keyExpression());

            EvaluationContext context = new StandardEvaluationContext();
            // 获取参数值
            Object[] args = pjp.getArgs();

            // 获取运行时参数的名称 java 17
            StandardReflectionParameterNameDiscoverer discoverer = new StandardReflectionParameterNameDiscoverer();
            //java8
            //LocalVariableTableParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();
            String[] parameterNames = discoverer.getParameterNames(method);

            // 将参数绑定到context中
            if (parameterNames != null) {
                for (int i = 0; i < parameterNames.length; i++) {
                    context.setVariable(parameterNames[i], args[i]);
                }
            }

            // 解析表达式，获取结果
            key = String.valueOf(expression.getValue(context));
        }

        String scene = distributeLock.scene();

        String lockKey = PREFIX + scene + "#" + key;

        int expireTime = distributeLock.expireTime();
        int waitTime = distributeLock.waitTime();
        RLock rLock = redissonClient.getLock(lockKey);
        try {
            boolean lockResult = false;
            if (waitTime == Constants.DEFAULT_WAIT_TIME) {
                if (expireTime == Constants.DEFAULT_EXPIRE_TIME) {
                    LOG.info("lock for key : {}", lockKey);
                    rLock.lock();
                } else {
                    LOG.info("lock for key : {} , expire : {}", lockKey, expireTime);
                    rLock.lock(expireTime, TimeUnit.MILLISECONDS);
                }
                lockResult = true;
            } else {
                if (expireTime == Constants.DEFAULT_EXPIRE_TIME) {
                    LOG.info("try lock for key : {} , wait : {}", lockKey, waitTime);
                    lockResult = rLock.tryLock(waitTime, TimeUnit.MILLISECONDS);
                } else {
                    LOG.info("try lock for key : {} , expire : {} , wait : {}", lockKey, expireTime, waitTime);
                    lockResult = rLock.tryLock(waitTime, expireTime, TimeUnit.MILLISECONDS);
                }
            }

            if (!lockResult) {
                LOG.warn("lock failed for key : {} , expire : {}", lockKey, expireTime);
                throw new DistributeLockException("acquire lock failed... key : " + lockKey);
            }


            LOG.info("lock success for key : {} , expire : {}", lockKey, expireTime);
            response = pjp.proceed();
        } catch (Throwable e) {
            throw new DistributeLockException(e.getMessage());
        } finally {
            if (rLock.isHeldByCurrentThread()) {
                rLock.unlock();
                LOG.info("unlock for key : {} , expire : {}", lockKey, expireTime);
            }
        }
        return response;
    }
}