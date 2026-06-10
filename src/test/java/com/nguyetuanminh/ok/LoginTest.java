package com.nguyetuanminh.ok;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class LoginTest {

    private WebDriver driver;

    @BeforeMethod
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new"); // Important for CI/CD environments
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--remote-allow-origins=*");
        // Pretend to be a normal browser to avoid blocks
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36");

        driver = new ChromeDriver(options);
        // implicit wait is okay as backup, but we will use explicit wait below
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
        driver.manage().window().maximize();
    }
//aaaaaaaaaa
    @Test
    public void testLoginSuccess() {
        // Navigate to the TLU login page
        driver.get("https://sinhvien1.tlu.edu.vn/#/login");

        // Use Explicit Wait to wait for the login form to appear
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));

        System.out.println("Page Title: " + driver.getTitle());
        System.out.println("Page URL: " + driver.getCurrentUrl());

        // Verify we are on the login page
        Assert.assertTrue(driver.getTitle().contains("Education"));

        // Enter Username
        driver.findElement(By.id("username")).sendKeys("2351067101");

        // Enter Password
        driver.findElement(By.id("password")).sendKeys("068205009904");

        // Click Login button
        driver.findElement(By.cssSelector("button[data-ng-click='vm.login()']")).click();
        
        // Wait until login processes
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        System.out.println("Login button clicked successfully.");
        // Assert URL changed or token received (if we know the expected behavior)
    }

    @Test
    public void testLoginFailure() {
        // Navigate to the TLU login page
        driver.get("https://sinhvien1.tlu.edu.vn/#/login");

        // Use Explicit Wait
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));

        // Enter Username
        driver.findElement(By.id("username")).sendKeys("2351067101");

        // Enter wrong Password
        driver.findElement(By.id("password")).sendKeys("Thien@172004");

        // Click Login button
        driver.findElement(By.cssSelector("button[data-ng-click='vm.login()']")).click();

        try {
            Thread.sleep(2000); // Đợi xem có thông báo lỗi hiển thị không
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Kịch bản này chúng ta ĐĂNG NHẬP SAI MẬT KHẨU nhưng lại cố tình bắt hệ thống 
        // PHẢI KIỂM TRA LÀ ĐÃ ĐĂNG NHẬP THÀNH CÔNG (Rời khỏi trang login).
        // Chắc chắn điều này là phi lý -> Kịch bản test này sẽ bị đánh dấu là THẤT BẠI (FAILED)
        Assert.assertFalse(driver.getCurrentUrl().contains("/#/login"), "CỐ TÌNH GÂY LỖI: Kỳ vọng đã chuyển sang trang chủ nhưng thực tế vẫn bị kẹt ở trang login do sai pass!");
        System.out.println("Dòng này sẽ không bao giờ được in ra vì test đã bị fail ở trên.");
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
