package com.xkenmon.nox.common;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.xkenmon.nox.common.configuration.LoggingConfiguration;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Slf4JLoggerFactory;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.api.LayoutComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.RootLoggerComponentBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;

public abstract class BaseApp<T> {

  protected abstract T createCommandObject();

  protected abstract void run(T commandArgs);

  public void start(String[] args) {
    T commandArgs = parseCommand(args);

    //netty logging
    InternalLoggerFactory.setDefaultFactory(Slf4JLoggerFactory.INSTANCE);

    initLog4j();

    run(commandArgs);
  }

  private T parseCommand(String[] argv) {
    T commandArgs = createCommandObject();
    try {
      JCommander.newBuilder()
          .addObject(commandArgs)
          .build()
          .parse(argv);
    } catch (ParameterException e) {
      e.usage();
      System.exit(1);
    }
    return commandArgs;
  }

  protected final void setLogLevel(Level level) {
    Configurator.setRootLevel(level);
  }

  protected final Level getDefaultLogLevel() {
    return Level.INFO;
  }

  protected final Level convertLogLevel(int debug) {
    switch (debug) {
      case LoggingConfiguration.LOG_LEVEL_DEBUG:
        return Level.DEBUG;
      case LoggingConfiguration.LOG_LEVEL_INFO:
        return Level.INFO;
      case LoggingConfiguration.LOG_LEVEL_WARN:
        return Level.WARN;
      case LoggingConfiguration.LOG_LEVEL_ERROR:
        return Level.ERROR;
      default:
        return Level.INFO;
    }
  }


  private void initLog4j() {
    ConfigurationBuilder<BuiltConfiguration> builder
        = ConfigurationBuilderFactory.newConfigurationBuilder();

    AppenderComponentBuilder console =
        builder.newAppender("stdout", "Console");

    builder.add(console);

    LayoutComponentBuilder layout = builder.newLayout("PatternLayout");
    layout.addAttribute("pattern", "%d{yyyy-MM-dd HH:mm:ss} [%-25t] %-5level %logger{4} - %msg%n");

    AppenderComponentBuilder appender;
    appender = builder.newAppender("default", "Console");
    appender.add(layout);
    builder.add(appender);

    Level level = getDefaultLogLevel();

    RootLoggerComponentBuilder logger = builder.newRootLogger(level);
    logger.add(builder.newAppenderRef("default"));
    builder.add(logger);

    Configurator.initialize(builder.build());
  }
}
