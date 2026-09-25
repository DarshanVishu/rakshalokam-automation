package com.rakshalokam.utils;

import java.io.File;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ExtentManager {
    private static ExtentReports extent;

    public static synchronized ExtentReports getInstance() {
        if (extent == null) {
            String reportsDirPath = System.getProperty("user.dir") + File.separator + "reports";
            File dir = new File(reportsDirPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            ExtentSparkReporter spark = new ExtentSparkReporter(reportsDirPath + File.separator + "ExtentReport.html");
            spark.config().setReportName("Rakshalokam E-Commerce Automation Suite");
            spark.config().setDocumentTitle("Test Execution Report");
            spark.config().setTheme(Theme.STANDARD);

            extent = new ExtentReports();
            extent.attachReporter(spark);
            extent.setSystemInfo("Application", "Vendure Remix Storefront");
            extent.setSystemInfo("Java Version", System.getProperty("java.version"));
            extent.setSystemInfo("OS", System.getProperty("os.name"));
        }
        return extent;
    }
}