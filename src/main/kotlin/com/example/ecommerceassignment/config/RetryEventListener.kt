package com.example.ecommerceassignment.config

import io.github.resilience4j.core.registry.EntryAddedEvent
import io.github.resilience4j.core.registry.EntryRemovedEvent
import io.github.resilience4j.core.registry.EntryReplacedEvent
import io.github.resilience4j.core.registry.RegistryEventConsumer
import io.github.resilience4j.retry.Retry
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class RetryEventListener : RegistryEventConsumer<Retry> {
    private val log = LoggerFactory.getLogger(this::class.java)

    override fun onEntryAddedEvent(entryAddedEvent: EntryAddedEvent<Retry>) {
        entryAddedEvent.addedEntry.eventPublisher
            .onRetry {
                log.warn(
                    "🔄 [Retry] 재시도 중 " +
                        "name=${it.name} " +
                        "attempt=${it.numberOfRetryAttempts} " +
                        "cause=${it.lastThrowable?.message}",
                )
            }.onSuccess {
                log.info("✅ [Retry] 성공 name=${it.name} totalAttempts=${it.numberOfRetryAttempts}")
            }.onError {
                log.error(
                    "❌ [Retry] 최종 실패 " +
                        "name=${it.name} " +
                        "totalAttempts=${it.numberOfRetryAttempts} " +
                        "cause=${it.lastThrowable?.message}",
                )
            }.onIgnoredError {
                log.warn("⏭️ [Retry] 무시된 예외 name=${it.name} cause=${it.lastThrowable?.message}")
            }
    }

    override fun onEntryReplacedEvent(entryReplacedEvent: EntryReplacedEvent<Retry>) {}

    override fun onEntryRemovedEvent(entryRemovedEvent: EntryRemovedEvent<Retry>) {}
}
