package blockchain.utility;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;

public class Log {
    static {
        ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();
        builder.setStatusLevel(Level.ERROR);
        builder.setConfigurationName("BuilderTest");
        builder.add(builder.newFilter("ThresholdFilter", Filter.Result.ACCEPT, Filter.Result.NEUTRAL)
                .addAttribute("level", Level.DEBUG));
        AppenderComponentBuilder appenderBuilder =
                builder.newAppender("Stdout", "CONSOLE")
                        .addAttribute("target", ConsoleAppender.Target.SYSTEM_OUT);
        appenderBuilder.add(builder.newLayout("PatternLayout")
                .addAttribute("pattern", "%d{yyyy-MM-dd HH:mm:ss.SSS} [%t] [%-1level]: %msg%n%throwable"));
        appenderBuilder.add(builder.newFilter("MarkerFilter", Filter.Result.DENY, Filter.Result.NEUTRAL)
                .addAttribute("marker", "FLOW"));
        builder.add(appenderBuilder);
        builder.add(builder.newLogger("org.apache.logging.log4j", Level.DEBUG)
                .add(builder.newAppenderRef("Stdout")).addAttribute("additivity", false));
        builder.add(builder.newRootLogger(Level.ERROR).add(builder.newAppenderRef("Stdout")));
        LoggerContext ignored = Configurator.initialize(builder.build());
    }

    private final org.apache.logging.log4j.Logger logger;

    private Log(String name) {
        logger = LogManager.getLogger(name);
    }

    public void debug(Object message) {
        logger.debug(message);
    }

    public void debug(String message, Object... params) {
        logger.debug(message, params);
    }

    public void info(Object message) {
        logger.info(message);
    }

    public void info(String message, Object... params) {
        logger.info(message, params);
    }

    public void warn(Object message) {
        logger.warn(message);
    }

    public void warn(String message, Object... params) {
        logger.warn(message, params);
    }

    public void error(Object message) {
        logger.error(message);
    }

    public void error(String message, Object... params) {
        logger.error(message, params);
    }

    public static Log get(String name) {
        return new Log(name);
    }

    public static Log get(Class<?> cls) {
        return new Log(cls.getSimpleName());
    }

    public static Log get(Object obj) {
        return new Log(obj.getClass().getSimpleName());
    }
}
