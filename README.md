### `README.md` for `rakshalokam-automation`

Copy and paste this into the root `README.md` of your **`rakshalokam-automation`** repository. It matches your exact `pom.xml` dependencies (Java 17, Selenium 4.21, Playwright 1.44, TestNG 7.10, REST Assured 5.4, Jackson, JavaFaker, and ExtentReports).

```markdown
# Rakshalokam E-Commerce Test Automation Framework

An end-to-end hybrid test automation framework built with **Java 17**, **Selenium WebDriver**, **REST Assured**, and **TestNG** to validate storefront UI journeys and backend REST APIs for the Rakshalokam e-commerce platform.

---

## 🛠️ Tech Stack & Versions

| Layer | Technology / Library | Version |
| :--- | :--- | :--- |
| **Language** | Java (JDK) | `17` |
| **UI Automation** | Selenium WebDriver / Playwright | `4.21.0` / `1.44.0` |
| **API Testing** | REST Assured & JSON Schema Validator | `5.4.0` |
| **Serialization** | Jackson Databind (POJO) | `2.17.1` |
| **Test Runner** | TestNG & Maven Surefire | `7.10.2` / `3.3.0` |
| **Test Data** | JavaFaker | `1.0.2` |
| **Reporting & Logging** | ExtentReports & SLF4J | `5.1.1` / `2.0.13` |
| **CI/CD** | Jenkins / GitHub Actions | — |

---

## 🏗️ Framework Architecture

```text
rakshalokam-automation/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── base/          # WebDriver initialization (ThreadLocal) & BaseTest setup
│   │   │   ├── pages/         # Page Object Model (POM) classes for Storefront & Checkout
│   │   │   ├── pojo/          # Request/Response POJOs for Jackson serialization
│   │   │   └── utils/         # ConfigReader, Screenshot utilities, ExtentReport manager
│   │   └── resources/
│   │       └── config.properties
│   └── test/
│       └── java/
│           ├── api/           # REST Assured contract, payload, and status assertions
│           ├── listeners/     # TestNG ITestListener & IRetryAnalyzer implementations
│           └── ui/            # End-to-end storefront, cart, coupon, and checkout tests
├── testng.xml                 # TestNG suite execution configuration
└── pom.xml                    # Maven dependencies & Surefire build plugins

```

---

## ✨ Key Framework Capabilities

* **Hybrid UI + API Coverage:** Combines browser-level storefront validation with fast API contract and schema assertions.
* **Thread-Safe Parallel Execution:** Uses `ThreadLocal<WebDriver>` to isolate browser sessions during concurrent TestNG suite runs.
* **Dynamic Test Data Generation:** Integrates `JavaFaker` to generate unique guest user profiles, shipping addresses, and contact details on every run.
* **Automatic Retry Mechanism:** Configured with Maven Surefire (`rerunFailingTestsCount=1`) and TestNG retry listeners to handle transient network/UI flakiness.
* **Rich HTML Execution Reports:** Generates interactive **ExtentReports** with failure screenshots attached automatically via `Commons IO`.

---

## 🚀 Prerequisites & Local Execution

### Prerequisites

* **JDK 17** installed and configured in `JAVA_HOME`
* **Apache Maven 3.8+**
* **Chrome / Edge** browser installed (managed automatically via `WebDriverManager 5.8.0`)

### Run Tests via CLI

1. **Clone the repository:**
```bash
git clone [https://github.com/DarshanVishu/rakshalokam-automation.git](https://github.com/DarshanVishu/rakshalokam-automation.git)
cd rakshalokam-automation

```


2. **Execute the default TestNG suite:**
```bash
mvn clean test

```


3. **Execute a specific TestNG suite file:**
```bash
mvn clean test -DsuiteXmlFile=testng.xml

```



---

## 📊 Test Execution Reports

After execution completes, open the generated ExtentReport HTML file in your browser:

```text
/test-output/ExtentReport.html

```

```

---

### Quick Setup Steps on GitHub
1. Open your **`rakshalokam-automation`** repository on GitHub.
2. Click **Add a README** (or edit your existing `README.md`).
3. Paste the markdown above and adjust the folder names under `src/` if your actual package names differ slightly.
4. Commit with message: `docs: add comprehensive framework architecture README`.

Would you like to create a Jenkins Pipeline (`Jenkinsfile`) for this project next so you can run `mvn clean test` directly from the Jenkins server you just installed?

```
