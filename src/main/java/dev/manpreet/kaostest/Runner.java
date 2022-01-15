package dev.manpreet.kaostest;

import dev.manpreet.kaostest.dto.MonitoringConfig;
import dev.manpreet.kaostest.dto.internal.RunnerStore;
import dev.manpreet.kaostest.dto.internal.TestStore;
import dev.manpreet.kaostest.dto.xml.Suite;
import dev.manpreet.kaostest.providers.DurationProvider;
import dev.manpreet.kaostest.providers.ThreadCountProvider;
import dev.manpreet.kaostest.providers.duration.FixedDurationProvider;
import dev.manpreet.kaostest.providers.threadcount.FixedThreadCountProvider;
import dev.manpreet.kaostest.runner.TestRunnersManager;
import dev.manpreet.kaostest.util.CommonUtils;
import dev.manpreet.kaostest.util.SuiteXMLUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Our main API class that should be used.
 */
@Slf4j
public class Runner {

    private MonitoringConfig monitoringConfig;

    public Runner() {
        monitoringConfig = CommonUtils.getMonitoringConfigFromEnvironment();
    }

    public Runner(String monitoringConfigYAML) {
        monitoringConfig = CommonUtils.getMonitoringConfigFromYAMLFile(monitoringConfigYAML);
    }

    public Runner(MonitoringConfig monitoringConfig) {
        this.monitoringConfig = monitoringConfig;
    }

    /**
     * Run the tests in the defined suite XML for 5 minutes in 10 threads
     * @param suiteXmlPath - TestNG XML suite file
     */
    public void runTests(String suiteXmlPath) {
        ThreadCountProvider threadCountProvider = new FixedThreadCountProvider(10);
        DurationProvider durationProvider = new FixedDurationProvider(5, TimeUnit.MINUTES);
        runTests(suiteXmlPath, threadCountProvider, durationProvider);
    }

    /**
     * Run the tests defined in the suite XML as per the provider configurations.
     * @param suiteXmlPath - TestNG XML suite file
     * @param threadCountProvider - Instance of thread count provider
     * @param durationProvider - Instance of duration provider
     * @return RunnerStore - Holds statistics about the complete execution
     */
    public RunnerStore runTests(String suiteXmlPath, ThreadCountProvider threadCountProvider, DurationProvider durationProvider) {
        monitoringConfig.sanitizeConfig(threadCountProvider);
        Suite suite = SuiteXMLUtils.deserializeSuiteXML(suiteXmlPath);
        if (!SuiteXMLUtils.isSuiteValid(suite)) {
            throw new KaosException("Validation of suite XML failed. Please check logs");
        }
        //Initialize runner store
        RunnerStore.getRunnerStore(SuiteXMLUtils.getAllTestClasses(suite));
        TestRunnersManager testRunnersManager = new TestRunnersManager(threadCountProvider, durationProvider,
                SuiteXMLUtils.getAllListenerClasses(suite));
        testRunnersManager.runTests();
        return getPrintResults();
    }

    private RunnerStore getPrintResults() {
        RunnerStore runnerStore = RunnerStore.getRunnerStore();
        StringBuilder testResult = new StringBuilder("\n");
        for (TestStore eachTest: runnerStore.getTestStoreMap().values()) {
            testResult.append(eachTest.getTestName());
            testResult.append("\n");
            testResult.append(eachTest.toString());
            testResult.append("\n");
        }
        testResult.append("\nOverall Summary:\n");
        testResult.append(runnerStore.toString());
        log.info(testResult.toString());
        return runnerStore;
    }
}
