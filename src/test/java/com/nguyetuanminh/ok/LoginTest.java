package com.nguyetuanminh.ok;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;

public class LoginTest {

    private WebDriver driver;
    private WebDriverWait wait;

    private static final String BASE_URL = "https://sinhvien1.tlu.edu.vn/#/login";
    private static final String USERNAME = System.getenv("TLU_USERNAME") != null ? System.getenv("TLU_USERNAME") : "2351067101";
    private static final String CORRECT_PASSWORD = System.getenv("TLU_PASSWORD") != null ? System.getenv("TLU_PASSWORD") : "068205009904";
    private static final String WRONG_PASSWORD = "11111111";

    @BeforeMethod
    public void setUp() {
        // Tự động tải đúng ChromeDriver theo phiên bản Chrome trên máy chạy CI
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");         // Chạy không giao diện (cần cho CI)
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36");

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.manage().window().maximize();

        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    @Test
    public void testLoginSuccess() {
        driver.get(BASE_URL);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));

        driver.findElement(By.id("username")).sendKeys(USERNAME);
        driver.findElement(By.id("password")).sendKeys(CORRECT_PASSWORD);
        driver.findElement(By.cssSelector("button[data-ng-click='vm.login()']")).click();

        // Chờ cho đến khi URL không còn là login (chuyển sang dashboard)
        boolean isRedirected = wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("/#/login")));

        Assert.assertTrue(isRedirected, "Đăng nhập thành công nhưng không chuyển khỏi trang login!");
        System.out.println("✅ Đăng nhập thành công, đã chuyển sang trang chủ.");
    }

    @Test
    public void testLoginFailure() {
        driver.get(BASE_URL);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));

        driver.findElement(By.id("username")).sendKeys(USERNAME);
        driver.findElement(By.id("password")).sendKeys(WRONG_PASSWORD);
        driver.findElement(By.cssSelector("button[data-ng-click='vm.login()']")).click();

        // Kiểm tra thông báo lỗi hiển thị (thông thường là div hoặc span chứa thông báo lỗi)
        // Bạn cần điều chỉnh selector này dựa trên thực tế trang web
        By errorLocator = By.cssSelector(".alert-danger, .error-message, .notification-error");
        boolean errorDisplayed;
        try {
            WebElement errorMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(errorLocator));
            errorDisplayed = errorMsg.isDisplayed();
        } catch (Exception e) {
            errorDisplayed = false;
        }

        // Vẫn còn ở trang login
        boolean stillOnLogin = driver.getCurrentUrl().contains("/#/login");

        Assert.assertTrue(errorDisplayed && stillOnLogin,
                "Sai mật khẩu nhưng không thấy thông báo lỗi hoặc đã chuyển trang!");
        System.out.println("❌ Đăng nhập thất bại đúng như kỳ vọng: thông báo lỗi xuất hiện.");
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}