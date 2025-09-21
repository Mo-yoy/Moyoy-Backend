package com.moyoy.infra.redis.support;

import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RedissonLockAop {

	private static final String LOCK_KEY_PREFIX = "lock:";

	private final RedissonClient redissonClient;

	@Around("@annotation(com.moyoy.infra.redis.support.RedissonLock)")
	public Object lock(final ProceedingJoinPoint joinPoint) throws Throwable {
		MethodSignature signature = (MethodSignature)joinPoint.getSignature();
		Method method = signature.getMethod();
		RedissonLock redissonLock = method.getAnnotation(RedissonLock.class);

		String key = parseKey(signature.getParameterNames(), joinPoint.getArgs(), redissonLock.key());
		RLock rLock = redissonClient.getLock(key);

		boolean acquired = false;

		if (rLock.isHeldByCurrentThread()) {
			log.warn("Redisson Lock 재진입 차단 | Key: {}", key);
		} else {
			acquired = rLock.tryLock(
				redissonLock.waitTime(),
				redissonLock.leaseTime(),
				redissonLock.timeUnit());
		}

		if (!acquired) {
			log.info("Redisson Lock 획득 실패 | Key: {}", key);
			return null;
		}

		log.info("Redisson Lock 획득 성공 | Key: {}", key);

		/// TODO : 트랜잭션이 필요한 곳에서는 트랜잭션과 관련된 제어가 추가적으로 필요함, ex) 트랜잭션 커밋 후 락을 해제해야 하는 등의 상황
		Object result = null;
		try {
			result = joinPoint.proceed();
			return handleResultWithLock(rLock, key, result);
		} finally {

			if (!isAsyncResult(result)) {
				unlock(rLock, key);
			}
		}
	}

	private boolean isAsyncResult(Object result) {
		return (result instanceof CompletableFuture<?>);
	}

	private Object handleResultWithLock(RLock rLock, String key, Object result) {

		if (result instanceof CompletableFuture<?>) {

			return ((CompletableFuture<?>)result).whenComplete((r, ex) -> {
				forceUnlock(rLock, key);
			});
		}

		return result;
	}

	private void unlock(RLock rLock, String key) {
		try {
			if (rLock.isHeldByCurrentThread()) {
				rLock.unlock();
				log.info("락 해제 | Key: {}", key);
			}
		} catch (IllegalMonitorStateException e) {
			log.warn("이미 해제된 락 | Key: {}", key);
		}
	}

	private void forceUnlock(RLock rLock, String key) {
		try {
			rLock.forceUnlock();
			log.info("비동기 락 강제 해제 | Key: {}", key);
		} catch (Exception e) {
			log.warn("강제 해제 실패 | Key: {}", key, e);
		}
	}

	private String parseKey(String[] parameterNames, Object[] args, String keyExpression) {
		ExpressionParser parser = new SpelExpressionParser();
		EvaluationContext context = new StandardEvaluationContext();

		for (int i = 0; i < parameterNames.length; i++) {
			context.setVariable(parameterNames[i], args[i]);
		}

		return LOCK_KEY_PREFIX + parser.parseExpression(keyExpression).getValue(context, String.class);
	}
}
