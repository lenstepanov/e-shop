package com.eshop.ordering.api.application.infrastructure.integrationevents;

import com.eshop.ordering.api.application.integrationevents.OrderingIntegrationEventService;
import lombok.AllArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.UUID;

/**
 * Add unique name to the current thread and use it to store all events that should be published when executing the
 * thread. After the transaction is committed, publish all events that are stored for the current thread.
 */
@AllArgsConstructor
@Aspect
@Configuration
class PublishIntegrationEventsAspect extends TransactionSynchronizationAdapter {
  private final OrderingIntegrationEventService orderingIntegrationEventService;

  @Before("@annotation(org.springframework.transaction.annotation.Transactional)")
  public void registerTransactionSyncrhonization() {
    Thread.currentThread().setName("tx=" + UUID.randomUUID());
    TransactionSynchronizationManager.registerSynchronization(this);
  }

  @Override
  public void beforeCompletion() {
    super.beforeCompletion();
  }

  @Override
  public void afterCompletion(int status) {
    super.afterCompletion(status);
  }

  @Override
  public void beforeCommit(boolean readOnly) {
    super.beforeCommit(readOnly);
  }

  @Override
  public void afterCommit() {
    super.afterCommit();
    orderingIntegrationEventService.publishEventsThroughEventBus(Thread.currentThread().getName());
  }

}