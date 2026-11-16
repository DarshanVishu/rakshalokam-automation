# RakshaLokam E-Commerce Test Automation Framework

![Java](https://img.shields.io/badge/Java-17-orange?logo=openjdk)
![Playwright](https://img.shields.io/badge/Playwright-1.50.0-2EAD33?logo=playwright)
![Selenium](https://img.shields.io/badge/Selenium-4.x-43B02A?logo=selenium)
![REST Assured](https://img.shields.io/badge/REST_Assured-5.x-009688)
![TestNG](https://img.shields.io/badge/TestNG-7.9.0-red)
![Allure Report](https://img.shields.io/badge/Allure-2.26.0-yellow)
![CI/CD](https://img.shields.io/badge/GitHub_Actions-Automated-2088FF?logo=githubactions)

An enterprise-grade, modular **Hybrid Test Automation Framework** built for the **RakshaLokam E-Commerce Platform**. Designed for end-to-end quality coverage across **Desktop Web, Mobile Safari (iOS WebKit), and REST APIs**, with parallel execution, automatic failure tracing, and live Allure reporting via GitHub Actions.

---

## Key Highlights

- **Multi-Engine Web & Mobile Architecture:** Supports Chromium/Chrome desktop execution alongside emulated **iOS Safari (WebKit)** mobile viewport testing (`393x852`, touch gestures, subpixel overflow detection) to catch mobile checkout and layout regressions early.
- **End-to-End E-Commerce Coverage:** Validates product discovery, search & filtering, variant selection, cart persistence, guest/authenticated checkout flows, payment gateway callbacks, and order history.
- **API & UI Hybrid Validation:** Combines **REST Assured** API checks (inventory stock, pricing/discount rules, cart totals, order creation payloads) with UI assertions for faster, deterministic test runs.
- **Failure Diagnostics & Observability:** Captures Playwright traces, failure screenshots, API request/response logs, and Surefire reports attached directly to **Allure Reports**.
- **CI/CD Ready:** Pre-configured **GitHub Actions** pipeline supporting scheduled runs, PR checks, suite selection via `workflow_dispatch`, and automated **GitHub Pages** report publishing.

---

## Tech Stack

| Layer | Technology |
| :--- | :--- |
| **Language & Build** | Java 17, Apache Maven |
| **UI & Mobile Web** | Playwright Java (`1.50.0`), Selenium WebDriver (`4.x`) |
| **API Testing** | REST Assured (`5.x`), Jackson Databind |
| **Test Runner** | TestNG (`7.9.0`) with XML Suite Routing & Parallel Threading |
| **Data-Driven Testing** | Apache POI (`.xlsx`/`.xls`), JSON & Property Configs |
| **Reporting** | Allure TestNG & Allure Maven (`2.26.0`) |
| **CI/CD & Hosting** | GitHub Actions (`ubuntu-24.04`), GitHub Pages |

---

## Project Architecture

```text
RakshaLokam-Automation/
├── .github/
│   └── workflows/
│       └── regression.yml                 # CI/CD pipeline (Desktop, iOS WebKit, Allure Pages)
├── src/
│   ├── main/java/com/rakshalokam/automation/
│   │   ├── core/
│   │   │   ├── constants/                 # Timeouts, URLs, environment constants
│   │   │   └── utils/                     # Excel readers, JSON parsers, retry & wait helpers
│   │   ├── drivers/
│   │   │   ├── capabilities/              # Browser & iOS Safari WebKit device profiles
│   │   │   ├── factory/                   # TargetEngine & DriverFactory routing
│   │   │   └── managers/                  # ThreadLocal Playwright & WebDriver lifecycle
│   │   ├── pages/                         # Page Object Model (Home, PLP, PDP, Cart, Checkout)
│   │   └── api/                           # REST Assured specs, endpoints & POJO models
│   └── test/
│       ├── java/com/rakshalokam/automation/tests/
│       │   ├── web/                       # Desktop E2E & smoke regression suites
│       │   ├── mobile/                    # iOS Safari (WebKit) responsive & touch tests
│       │   ├── api/                       # Catalog, Cart, Pricing & Order API tests
│       │   └── e2e/                       # Cross-layer UI + API order lifecycle flows
│       └── resources/
│           ├── testng-web.xml             # Desktop Web regression suite
│           ├── testng-ios.xml             # iOS Safari mobile web suite
│           ├── testng-api.xml             # REST API validation suite
│           ├── testng-e2e.xml             # Full checkout E2E suite
│           └── testng-all.xml             # Master full-regression suite
└── pom.xml

```

---

## Automated Test Coverage

### 1. Storefront & Catalog (`web` & `mobile`)

* Homepage hero banners, category navigation, and predictive search.
* Product Listing Page (PLP) sorting, price-range filters, and pagination.
* Product Detail Page (PDP) size/variant selection, stock badges, and image gallery swipe/zoom.

### 2. Cart & Checkout Flow (`e2e`)

* Add-to-cart, quantity updates, coupon/promo code discount calculation, and tax/shipping totals.
* Guest-to-registered user cart merge across session reloads.
* Address validation, payment method selection, and order confirmation ID verification.

### 3. Mobile Safari / Responsive Web (`mobile`)

* Hamburger drawer navigation, sticky Add-to-Cart bar visibility, and touch dropdown selection on WebKit.
* Viewport integrity checks preventing horizontal overflow and iOS input auto-zoom (`font-size >= 16px`).

### 4. Backend & Pricing Engine (`api`)

* Schema validation, HTTP status checks, and response SLA assertions across catalog, cart, and order endpoints.
* Boundary & negative testing for invalid coupons, out-of-stock SKUs, and unauthorized order access.

---

## Getting Started

### Prerequisites

* **Java JDK 17+**
* **Apache Maven 3.8+**
* **Git**

### 1. Clone the Repository

```bash
git clone [https://github.com/DarshanVishu/RakshaLokam-Automation.git](https://github.com/DarshanVishu/RakshaLokam-Automation.git)
cd RakshaLokam-Automation

```

### 2. Install Playwright Browsers (First-Time Setup)

```bash
mvn test-compile exec:java -e \
  -Dexec.classpathScope=test \
  -Dexec.mainClass=com.microsoft.playwright.CLI \
  -Dexec.args="install webkit chromium"

```

### 3. Run Test Suites Locally

**Run Desktop Web Suite (Headless):**

```bash
mvn clean test -DsuiteXmlFile=src/test/resources/testng-web.xml -Dheadless=true

```

**Run iOS Safari (WebKit) Mobile Suite:**

```bash
mvn test -DsuiteXmlFile=src/test/resources/testng-ios.xml -Dheadless=true

```

**Run API Regression Suite:**

```bash
mvn test -DsuiteXmlFile=src/test/resources/testng-api.xml

```

**Run Full Master Regression:**

```bash
mvn clean test -DsuiteXmlFile=src/test/resources/testng-all.xml -Dheadless=true

```

### 4. Generate & View Allure Report

```bash
mvn allure:serve

```

---

## CI/CD Pipeline (GitHub Actions)

Every push or pull request to `main` triggers `.github/workflows/regression.yml`:

1. **Desktop Regression Job:** Executes `testng-web.xml` in headless mode on Linux.
2. **iOS Safari Mobile Job:** Provisions WebKit on `ubuntu-24.04` and runs `testng-ios.xml` with trace capture enabled on failure.
3. **Combined Allure Deployment:** Merges desktop and mobile execution artifacts and publishes a unified interactive report to **GitHub Pages**.

---

## Author

**Darsh** — QA Automation & Platform Engineer

* GitHub: [@DarshanVishu](https://github.com/DarshanVishu)

```

If you want to customize any badges, live GitHub Pages URL, specific RakshaLokam modules (such as loyalty points, vendor portal, or Razorpay/UPI sandbox flows), or package names to match your exact repo structure, let me know and I will update it.

```
