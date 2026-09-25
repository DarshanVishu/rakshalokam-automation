package com.rakshalokam.utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.rakshalokam.base.BaseTest;
import com.rakshalokam.base.DriverFactory;

public class TestListener implements ITestListener {
    private static final ExtentReports extent = ExtentManager.getInstance();
    private static final ThreadLocal<ExtentTest> testThread = new ThreadLocal<>();

    @Override
    public void onTestStart(ITestResult result) {
        ExtentTest test = extent.createTest(result.getMethod().getMethodName(), result.getMethod().getDescription());
        testThread.set(test);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        if (testThread.get() != null) {
            testThread.get().log(Status.PASS, "Test passed successfully.");
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {
        if (testThread.get() != null) {
            testThread.get().log(Status.FAIL, result.getThrowable());
        }

        // Safe Driver Extraction: only capture screenshots for Selenium UI tests
        WebDriver driver = null;
        Object testInstance = result.getInstance();

        if (testInstance instanceof BaseTest) {
            driver = DriverFactory.getDriver();
        }

        if (driver != null) {
            try {
                String base64Screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
                if (testThread.get() != null) {
                    testThread.get().fail("Failure Snapshot", 
                        MediaEntityBuilder.createScreenCaptureFromBase64String(base64Screenshot).build());
                }
            } catch (Exception e) {
                System.err.println("Failed to capture screenshot: " + e.getMessage());
            }
        }

        System.err.println("\n=======================================================");
        System.err.println(">>> TEST FAILED: " + result.getTestClass().getName() + "." + result.getName());
        if (result.getThrowable() != null) {
            result.getThrowable().printStackTrace();
        }
        System.err.println("=======================================================\n");
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        if (testThread.get() != null) {
            testThread.get().log(Status.SKIP, "Test skipped: " + result.getThrowable());
        }
    }

    @Override
    public void onFinish(ITestContext context) {
        extent.flush();
    }
}