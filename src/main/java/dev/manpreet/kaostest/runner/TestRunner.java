package dev.manpreet.kaostest.runner;

import dev.manpreet.kaostest.stores.Store;
import dev.manpreet.kaostest.util.TestNGUtils;
import lombok.extern.slf4j.Slf4j;
import org.testng.TestNG;

import java.util.List;

/**
 * Test runner thread which picks one test class at a time and runs it using the TestNG API.
 */
@Slf4j
public class TestRunner implements Runnable {

    private final Store runnerStore;
    private final List<Class<?>> inputListeners;
    private boolean isRun;
    private boolean isFinished;

    /**
     * Init the test runner
     * @param inputListeners - List of listener classes to add to the test run
     */
    public TestRunner(List<Class<?>> inputListeners) {
        this.inputListeners = inputListeners;
        this.runnerStore = Store.getInstance();
        isRun = true;
        isFinished = false;
    }

    @Override
    public void run() {
        TestNG testNG;
        try {
            while (isRun) {
                testNG = TestNGUtils.getTestNGInstance(inputListeners);
                String nextFullTestClassName = runnerStore.getRandomTestClass();
                Class<?>[] testClasses = new Class[1];
                testClasses[0] = Class.forName(nextFullTestClassName);
                testNG.setTestClasses(testClasses);
                testNG.run();
            }
            isFinished = true;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

        public void stop() {
        log.info("Stop received");
        isRun = false;
    }

    public boolean isFinished() {
        return isFinished;
    }

}
