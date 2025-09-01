package tests;

import org.testng.Assert;
import org.testng.annotations.*;

import base.ConfigLoader;
import base.DriverSetup;
import pages.LoginPage;
import org.openqa.selenium.WebDriver;

public class LoginTest {
    private LoginPage loginPage;
    private WebDriver driver;
    private String baseUrl = ConfigLoader.getProperty("baseUrl");

    @Parameters("browser")
    @BeforeClass
    public void setUp(String browser) {
        DriverSetup.setDriver(browser);         // initialize driver for given browser
        driver = DriverSetup.getDriver();
        
        driver.get(baseUrl); // open base URL
        loginPage = new LoginPage(driver);
    }

    @AfterClass
    public void tearDown() {
        DriverSetup.quitDriver();  // quit & cleanup driver
    }

    @BeforeMethod
    public void resetState() throws InterruptedException {
        // loginPage.logout();
        Thread.sleep(5000);
        loginPage.closePopupIfPresent();
    }

    @Test(priority = 1, description = "Should fail if phone number is invalid")
    public void invalidPhoneShouldFail() {
        loginPage.openLogin();
        boolean result = loginPage.loginWithOtp(ConfigLoader.getProperty("invalidPhoneNo"));
        Assert.assertFalse(result, "Login should fail for invalid phone number");
    }

    @Test(priority = 2, description = "Should fail if OTP entered is invalid")
    public void invalidOtpShouldFail() {
        loginPage.openLogin();
        boolean result = loginPage.loginWithOtp(ConfigLoader.getProperty("validPhoneNoForIncorrectOTP")); // valid phone, wrong OTP
        Assert.assertFalse(result, "Login should fail for invalid OTP");
        System.out.println("End of invalid otp");
    }

    @Test(priority = 3, description = "Should pass if OTP is valid")
    public void validOtpShouldPass() {
        loginPage.openLogin();
        boolean result = loginPage.loginWithOtp(ConfigLoader.getProperty("validPhoneNoForCorrectOTP")); // enter OTP manually
        Assert.assertTrue(result, "Login should succeed for valid OTP");
    }
}
