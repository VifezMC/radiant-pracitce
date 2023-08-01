package neptune.dev.utils.terminable.module;

import neptune.dev.utils.terminable.TerminableConsumer;
import org.jetbrains.annotations.NotNull;

/**
 * A terminable module is a class which manipulates and constructs a number of {@link Terminable}s.
 */
public interface TerminableModule {

  /**
   * Performs the tasks to setup this module
   *
   * @param consumer the terminable consumer
   */
  void setup(@NotNull TerminableConsumer consumer);

  /**
   * Registers this terminable with a terminable consumer
   *
   * @param consumer the terminable consumer
   */
  default void bindModuleWith(@NotNull final TerminableConsumer consumer) {
    consumer.bindModule(this);
  }

}
