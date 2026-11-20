package com.zipo.interviewproject.automation.listeners;

import com.zipo.interviewproject.automation.diagnostics.FailureDiagnostics;
import com.zipo.interviewproject.automation.reporting.AllureReporter;
import org.testng.IExecutionListener;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;

/** Evaluates the outcome while the device is still in the test's final state. */
public final class TestExecutionListener implements IInvokedMethodListener, IExecutionListener {

    private final AllureReporter reporter = new AllureReporter();

    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult result) {
        if (method.isTestMethod()) {
            // Allure registers the test in TestNG's preceding onTestStart callback.
            reporter.bindTest(result);
        }
    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult result) {
        if (method.isTestMethod()) {
            FailureDiagnostics.finish(result);
            reporter.completeTest(result);
        }
    }

    @Override
    public void onExecutionFinish() {
        reporter.close();
    }
}
