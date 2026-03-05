package com.example.ecommerceassignment.config

import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.core.registry.EntryAddedEvent
import io.github.resilience4j.core.registry.EntryRemovedEvent
import io.github.resilience4j.core.registry.EntryReplacedEvent
import io.github.resilience4j.core.registry.RegistryEventConsumer
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class CircuitBreakerEventListener : RegistryEventConsumer<CircuitBreaker> {
    private val log = LoggerFactory.getLogger(this::class.java)

    override fun onEntryAddedEvent(entryAddedEvent: EntryAddedEvent<CircuitBreaker>) {
        entryAddedEvent.addedEntry.eventPublisher
            .onStateTransition {
                log.warn(
                    "⚡ [CircuitBreaker] 상태 변경 " +
                        "name=${it.circuitBreakerName} " +
                        "transition=${it.stateTransition}",
                )
            }.onCallNotPermitted {
                log.error("🚫 [CircuitBreaker] OPEN - 호출 차단 name=${it.circuitBreakerName}")
            }.onSuccess {
                log.info(
                    "✅ [CircuitBreaker] 성공 " +
                        "name=${it.circuitBreakerName} " +
                        "duration=${it.elapsedDuration.toMillis()}ms",
                )
            }.onError {
                log.error(
                    "❌ [CircuitBreaker] 실패 " +
                        "name=${it.circuitBreakerName} " +
                        "duration=${it.elapsedDuration.toMillis()}ms " +
                        "cause=${it.throwable.message}",
                )
            }
    }

    override fun onEntryReplacedEvent(entryReplacedEvent: EntryReplacedEvent<CircuitBreaker>) {}

    override fun onEntryRemovedEvent(entryRemovedEvent: EntryRemovedEvent<CircuitBreaker>) {}
}
