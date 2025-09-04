package pages;

import java.time.Duration;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LoginPage {
    private WebDriver driver;
    private WebDriverWait wait;

    // ------------------- Locators -------------------
    @FindBy(xpath = "//span[@role='button' and text()='X']")
	private WebElement closeLoginPopup;
	
	@FindBy(xpath = "//a[@title='Login' and contains(@href, 'login?ret')]")
	private WebElement loginButton;
	
	@FindBy(xpath = "//input[@type='text' and @autocomplete='off' and contains(@class, 'BV+Dqf')]")
	private WebElement phoneInput;
	
	@FindBy(xpath = "//button[contains(text(),'Request OTP')]")
	private WebElement continueButton;
	
	@FindBy(xpath = "//button[@type='submit' and contains(@class, 'QqFHMw') and contains(text(),'Verify')]")
	private WebElement submitBtn;
	
	@FindBy(xpath = "//div[text()='OTP is incorrect' and @class='eIDgeN']")
	private WebElement otpIncorrectElement;
	
	// Defensive locators
	private By phoneBy = By.xpath("//input[@type='text' and @autocomplete='off' and contains(@class, 'BV+Dqf')]");
	private By closePopupBy = By.xpath("/html/body/div[4]/div/span");
	private By loggedInAccount = By.xpath("//*[@id='container']/div/div[1]/div/div/div/div/div/div/div/div/div/div[1]/div/div/header/div[2]/div[2]/div/div/div/div/a[not(@title='Login')]");
	private By notLoggedInAccount = By.xpath("//a[contains(text(), 'Login')]");
//	private By logOutBtn = By.xpath("//div[text()='Logout']");
	private By invalidMobileNo = By.xpath("//span[@class='llBOFA']/span[text()='Please enter valid Email ID/Mobile number']");
	private By unRegisteredMobileNoBy = By.xpath("//div[@class='eIDgeN' and text()='You are not registered with us. Please sign up.']");
	
    // ------------------- Constructor -------------------
    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    // ------------------- Utility Methods -------------------
    private boolean isElementPresent(By locator) {
        return driver.findElements(locator).size() > 0;
    }

    private void clickIfVisible(By locator, WebElement element) {
        if (driver.findElements(By.xpath("/html/body/div[4]/div/span")).size() > 0) {
        	System.out.println("Successfully popup closed");
            driver.findElement(locator).click();
        }
    }

    // ------------------- Page Actions -------------------
    // Close the initial login popup if visible 
    public void closePopupIfPresent() {
        clickIfVisible(closePopupBy, closeLoginPopup);
    }

    // Open login popup robustly // !TODO: problem
    public void openLogin() {

        if (isElementPresent(phoneBy)) {
            System.out.println("Login popup already open.");
            return;
        }

        try {
            wait.until(ExpectedConditions.elementToBeClickable(loginButton)).click();
            System.out.println("Login popup opened.");
        } catch (TimeoutException e) {
            System.out.println("Could not find Login button.");
        }
    }

    public boolean loginWithOtp(String phoneNo) {
        phoneInput.clear();
        phoneInput.sendKeys(phoneNo);
        continueButton.click();
        
        // Step 1: Invalid phone number
        if (driver.findElements(invalidMobileNo).size() > 0) {
            System.out.println("Login failed – invalid phone number: " + phoneNo);
            return false;
        }	
        if (driver.findElements(unRegisteredMobileNoBy).size() > 0) {
			System.out.println("Login failed - unregister phone number: " + phoneNo);
			return false;
		}

        try {
            // Step 2: Wait for user to manually enter OTP and then click Verify
            wait.until(ExpectedConditions.elementToBeClickable(submitBtn)).click();

            // Step 3a: Invalid OTP
            try {
                WebElement otpError = new WebDriverWait(driver, Duration.ofSeconds(5))
                        .until(ExpectedConditions.visibilityOf(otpIncorrectElement));
                System.out.println("Login failed – invalid OTP: " + otpError.getText());
                driver.navigate().back();
                return false;
            } catch (TimeoutException e) {
                // no OTP error → continue
            }

            // Step 3b: Successful login
            wait.until(ExpectedConditions.visibilityOfElementLocated(loggedInAccount));

            if (driver.findElements(notLoggedInAccount).size() == 0)
            	System.out.println("Successful login – account menu visible.");
            
            return true;

        } catch (TimeoutException e) {
            System.out.println("OTP not submitted in time – login failed.");
            return false;
        }
    }

//    // Logout if logged in
//    public void logout() {
//        try {
//            WebElement logout = new WebDriverWait(driver, Duration.ofSeconds(5))
//                    .until(ExpectedConditions.visibilityOfElementLocated(logOutBtn));
//            logout.click();
//            System.out.println("Logged out successfully.");
//        } catch (TimeoutException e) {
////            System.out.println("Logout button not found – maybe already logged out.");
//        	if(driver.getCurrentUrl().contains("login"))
//        		driver.navigate().back();
//        }
//    }

    // Helper: Check if login popup is visible *
    public boolean isLoginPopupVisible() {
        return isElementPresent(phoneBy);
    }
}
