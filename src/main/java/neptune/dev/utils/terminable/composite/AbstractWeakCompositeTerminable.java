package neptune.dev.utils.terminable.composite;

import neptune.dev.utils.terminable.Terminable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedDeque;

public class AbstractWeakCompositeTerminable implements CompositeTerminable {

  private final Deque<WeakReference<AutoCloseable>> closeables = new ConcurrentLinkedDeque<>();

  protected AbstractWeakCompositeTerminable() {

  }

  @Override
  public CompositeTerminable with(final AutoCloseable autoCloseable) {
    Objects.requireNonNull(autoCloseable, "autoCloseable");
    this.closeables.push(new WeakReference<>(autoCloseable));
    return this;
  }

  @Override
  public void close() throws CompositeClosingException {
    final List<Exception> caught = new ArrayList<>();
    for (WeakReference<AutoCloseable> ref; (ref = this.closeables.poll()) != null; ) {
      final AutoCloseable ac = ref.get();
      if (ac == null) {
        continue;
      }

      try {
        ac.close();
      } catch (final Exception e) {
        caught.add(e);
      }
    }

    if (!caught.isEmpty()) {
      throw new CompositeClosingException(caught);
    }
  }

  @Override
  public void cleanup() {
    this.closeables.removeIf(ref -> {
      final AutoCloseable ac = ref.get();
      return ac == null || (ac instanceof Terminable && ((Terminable) ac).isClosed());
    });
  }
}
