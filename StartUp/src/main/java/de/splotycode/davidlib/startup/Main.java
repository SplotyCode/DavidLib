package de.splotycode.davidlib.startup;

import de.splotycode.davidlib.startup.application.ApplicationManager;
import de.splotycode.davidlib.startup.envirementchanger.StartUpInvirementChangerImpl;
import de.splotycode.davidlib.startup.manager.StartUpManager;
import de.splotycode.davidlib.startup.processbar.StartUpProcessHandler;
import de.splotycode.davidlib.startup.starttask.StartTaskExecutor;
import lombok.Getter;
import me.david.davidlib.runtime.LinkBase;
import me.david.davidlib.runtime.Links;
import me.david.davidlib.runtime.application.IApplication;
import me.david.davidlib.runtime.logging.DavidLibLoggerFactory;
import me.david.davidlib.runtime.logging.LoggingHelper;
import me.david.davidlib.runtime.startup.BootContext;
import me.david.davidlib.runtime.startup.envirement.StartUpEnvironmentChanger;
import me.david.davidlib.util.StringUtil;
import me.david.davidlib.util.collection.ArrayUtil;
import me.david.davidlib.util.init.AlreadyInitailizedException;
import me.david.davidlib.util.logger.Logger;
import me.david.davidlib.util.reflection.ReflectionUtil;
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

        /* Register Links */
        LinkBase.getInstance().registerLink(Links.BOOT_DATA, bootData);
        LinkBase.getInstance().registerLink(Links.APPLICATION_MANAGER, new ApplicationManager());
        LinkBase.getInstance().registerLink(Links.STARTUP_MANAGER, new StartUpManager());

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
    }

    private static void setUpLogging() {
        Logger.setFactory(DavidLibLoggerFactory.class);
        System.setProperty("log4j.defaultInitOverride", "true");
        try {
            org.apache.log4j.Logger root = org.apache.log4j.Logger.getRootLogger();
            if (!root.getAllAppenders().hasMoreElements()) {
                root.setLevel(Level.WARN);
                root.addAppender(new ConsoleAppender(new PatternLayout(PatternLayout.DEFAULT_CONVERSION_PATTERN)));
            }
        } catch (Throwable e) {
            e.printStackTrace();
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
