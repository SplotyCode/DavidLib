package io.github.splotycode.mosaik.startup;

import io.github.splotycode.mosaik.runtime.LinkBase;
import io.github.splotycode.mosaik.runtime.Links;
import io.github.splotycode.mosaik.runtime.application.IApplication;
import io.github.splotycode.mosaik.runtime.logging.LoggingHelper;
import io.github.splotycode.mosaik.runtime.logging.MosaikLoggerFactory;
import io.github.splotycode.mosaik.runtime.startup.BootContext;
import io.github.splotycode.mosaik.runtime.startup.envirement.StartUpEnvironmentChanger;
import io.github.splotycode.mosaik.startup.application.ApplicationManager;
import io.github.splotycode.mosaik.startup.envirementchanger.StartUpInvirementChangerImpl;
import io.github.splotycode.mosaik.startup.exception.FrameworkStartException;
import io.github.splotycode.mosaik.startup.manager.StartUpManager;
import io.github.splotycode.mosaik.startup.processbar.StartUpProcessHandler;
import io.github.splotycode.mosaik.startup.starttask.StartTaskExecutor;
import io.github.splotycode.mosaik.util.StringUtil;
import io.github.splotycode.mosaik.util.collection.ArrayUtil;
import io.github.splotycode.mosaik.util.init.AlreadyInitailizedException;
import io.github.splotycode.mosaik.util.logger.Logger;
import io.github.splotycode.mosaik.util.reflection.ReflectionUtil;
import lombok.Getter;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;

public class Main {

    @Getter private static Main instance;

    @Getter private static BootContext bootData;

    @Getter private static boolean initialised = false;

    public static void main() {
        main(ArrayUtil.EMPTY_STRING_ARRAY);
    }

    public static void mainIfNotInitialised() {
        if (!initialised)
            main();
    }

    public static void mainIfNotInitialised(String[] args) {
        if (!initialised)
            main(args);
    }

    private static Logger logger;

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        if (initialised) throw new AlreadyInitailizedException("Main.main() already called");
        initialised = true;

        setUpLogging();
        LoggingHelper.loggingStartUp();

        checkClassLoader();
        if (ReflectionUtil.getCallerClasses().length >= 4) {
            logger.warn("Framework was not invoked by JVM! It was invoked by: " + ReflectionUtil.getCallerClass().getName());
        }
        logger.info("");

        bootData = new BootContext(args, start);
        instance = new Main();
    }

    private Main() {
        ApplicationManager applicationManager = new ApplicationManager();
        StartUpManager startUpManager = new StartUpManager();

        /* Register Links */
        LinkBase.getInstance().registerLink(Links.BOOT_DATA, bootData);
        LinkBase.getInstance().registerLink(Links.APPLICATION_MANAGER, applicationManager);
        LinkBase.getInstance().registerLink(Links.STARTUP_MANAGER, startUpManager);

        /* Register StartUp Environment Changer */
        StartUpEnvironmentChanger environmentChanger = new StartUpInvirementChangerImpl();
        LinkBase.getInstance().registerLink(Links.STARTUP_ENVIRONMENT_CHANGER, environmentChanger);

        /* Running Startup Tasks*/
        LoggingHelper.startSection("StartUp Tasks");
        StartTaskExecutor.getInstance().collectSkippedPaths();
        StartTaskExecutor.getInstance().findAll(false);
        StartTaskExecutor.getInstance().runAll(environmentChanger);
        LoggingHelper.endSection();

        LoggingHelper.startSection("Environment Information");
        LoggingHelper.printInfo();
        LoggingHelper.endSection();

        /* Starting Applications */
        LoggingHelper.startSection("Applications");
        applicationManager.startUp();
        StartUpProcessHandler.getInstance().end();
        logger.info("Started " + applicationManager.getLoadedApplicationsCount() + " Applications: " + StringUtil.join(applicationManager.getLoadedApplications(), IApplication::getName, ", "));
        LoggingHelper.endSection();
        startUpManager.finished();
    }

    private static void setUpLogging() {
        Logger.setFactory(MosaikLoggerFactory.class);
        System.setProperty("log4j.defaultInitOverride", "true");
        try {
            org.apache.log4j.Logger root = org.apache.log4j.Logger.getRootLogger();
            if (!root.getAllAppenders().hasMoreElements()) {
                root.setLevel(Level.WARN);
                root.addAppender(new ConsoleAppender(new PatternLayout(PatternLayout.DEFAULT_CONVERSION_PATTERN)));
            }
        } catch (Throwable e) {
            throw new FrameworkStartException("Could not initialize log4j logging", e);
        }
        logger = Logger.getInstance(Main.class);

        LoggingHelper.registerShutdownLogging();
    }

    private static void checkClassLoader() {
        ClassLoader systemLoader = ClassLoader.getSystemClassLoader();
        ClassLoader threadLoader = Thread.currentThread().getContextClassLoader();
        ClassLoader thisLoader = Main.class.getClassLoader();

        if (thisLoader.getClass() != threadLoader.getClass() || thisLoader.getClass() != systemLoader.getClass()) {
            logger.warn(StringUtil.format("Invalid ClassLoader! ThisLoader: '{1}', SystemLoader: '{2}', ThisLoader: '{3}'",
                    thisLoader.getClass().getName(),
                    threadLoader.getClass().getName(),
                    systemLoader.getClass().getName()));
        }
    }

}
