package com.zipo.interviewproject.automation.listeners;

import com.zipo.interviewproject.automation.diagnostics.FailureDiagnostics;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;

/** Evaluates the outcome while the device is still in the test's final state. */
public final class TestExecutionListener implements IInvokedMethodListener {

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult result) {
        if (method.isTestMethod()) {
            FailureDiagnostics.finish(result);
        }
    }
}
