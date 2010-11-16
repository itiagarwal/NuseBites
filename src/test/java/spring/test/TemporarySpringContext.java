package spring.test;

import org.junit.rules.TestWatchman;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * A MethodRule that loads a Spring context and caches it to save on test runtime.
 *
 * @author Iwein Fuld
 */
public class TemporarySpringContext extends TestWatchman {
  /**
   * Cache of Spring application contexts. This needs to be static, as tests
   * are typically destroyed and recreated between running individual test methods.
   */
  static final ContextCache contextCache = new ContextCache();

  private ConfigurableApplicationContext context;
  private final String[] contextLocations;

  public TemporarySpringContext(String... contextLocations) {
    this.contextLocations = contextLocations;
    try {
      context = contextCache.contextForLocations(contextLocations);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Statement apply(Statement base, FrameworkMethod method, Object target) {
    context.getAutowireCapableBeanFactory().autowireBean(target);
    return super.apply(base, method, target);
  }

  public ConfigurableApplicationContext getContext() {
    return context;
  }

  public void dirtyContext() {
    contextCache.markDirty(contextLocations);
  }
}
