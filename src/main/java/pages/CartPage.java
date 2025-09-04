package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import base.ConfigLoader;

import java.time.Duration;

public class CartPage {
    private WebDriver driver;
    private WebDriverWait wait;

    // ---------- Locators ----------
    @FindBy(xpath = "//button[contains(@class, 'QqFHMw vslbG+ In9uk2')]") // "Add to Cart" button on product details page
    private WebElement addToCartBtn;

    @FindBy(xpath = "//a[@class='_9Wy27C' and contains(@href, 'viewcart?')]") // cart link (header)
    private WebElement cartLink;

    @FindBy(xpath = "//div[@class='JxFEK3 _48O0EI']") // cart container
    private WebElement cartContainer;

    @FindBy(xpath = "//button[@class='LcLcvv' and text()='+']") // increment quantity button
    private WebElement increaseQtyBtn;

    @FindBy(xpath = "//button[@class='LcLcvv'][1]") // decrement quantity button
    private WebElement decreaseQtyBtn;

    @FindBy(xpath = "//div[@class='PWd9A7 xvz6eC']/div[@class='_1Y9Lgu']/span") // total price in cart
    private WebElement totalPrice;

    @FindBy(xpath = "//div[@class='sBxzFz' and text()='Remove']") // remove item button
    private WebElement removeItemBtn;

    @FindBy(xpath = "//div[@class='sBxzFz fF30ZI t9UCZh' and text()='Cancel']") // confirm remove
    private WebElement cancelRemoveBtn;

    @FindBy(xpath = "//div[@class='sBxzFz fF30ZI A0MXnh' and text()='Remove']") // confirm remove
    private WebElement confirmRemoveBtn;

    @FindBy(xpath = "//button[@class='QqFHMw zA2EfJ _7Pd1Fp']/span[text()='Place Order']") // proceed to buy button
    private WebElement placeOrderBtn;

    @FindBy(xpath = "//span[text()='Login'] | //span[text()='Delivery Address']") // address step
    private WebElement addressStep;

    @FindBy(xpath = "//span[text()='Payment Options']") // payment page step
    private WebElement paymentStep;
    
    @FindBy(xpath = "//input[contains(@class, 'Jr-g+f') and @type='text' and @maxlength='auto']")
    private WebElement phoneNoElement;
    
    @FindBy(xpath = "//form/div[@class='aPGMpN']/button[@type='submit' and contains(@class, '_7Pd1Fp')]")
    private WebElement continueButtonElement;
    
//    @FindBy(xpath = "//input[contains(@class, 'r4vIwl') and contains(@class, 'zgwPDa') and contains(@class, 'Jr-g+f') and @maxLength='6']")
//    private WebElement enterOTPElement;
    
    @FindBy(xpath = "//button[@class='QqFHMw YhpBe+ _7Pd1Fp' and @type='submit']")
    private WebElement loginButtonElement;
    
//    Locators:=
    private By saveAndDeliverHereBtn = By.xpath("//button[text()='Save and Deliver Here'] | //span[@class='PXFoIh' and text()='Delivery Address']");
    private By nameErrorMsg = By.xpath("//input[@name='name']/following-sibling::span");
    private By phoneErrorMsg = By.xpath("//input[@name='phone']/following-sibling::span");

//    By decreaseQtyBy = By.xpath("//*[@id='container']/div/div[2]/div/div/div[1]/div/div[2]/div/div[2]/div[1]/div/button[1]");

    // ---------- Constructor ----------
    public CartPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    // ---------- Page Actions ----------

    /** Add product to cart from product details page */
    public void addToCart() {
        wait.until(ExpectedConditions.elementToBeClickable(addToCartBtn)).click();
        wait.until(ExpectedConditions.visibilityOf(cartContainer));
    }

    /** Modify quantity and return updated price */
    public String increaseQuantity() {
        wait.until(ExpectedConditions.elementToBeClickable(increaseQtyBtn)).click();
        return wait.until(ExpectedConditions.visibilityOf(totalPrice)).getText();
    }

    public String decreaseQuantity() throws InterruptedException  {
    	Thread.sleep(3000);
        System.out.println("Decrease Quantity");
    	wait.until(ExpectedConditions.elementToBeClickable(decreaseQtyBtn)).click();
        return wait.until(ExpectedConditions.visibilityOf(totalPrice)).getText();
    }

    /** Remove item from cart */
    public void removeItem() {
        wait.until(ExpectedConditions.elementToBeClickable(removeItemBtn)).click();
        wait.until(ExpectedConditions.elementToBeClickable(confirmRemoveBtn)).click();
    }

    public boolean isCartEmpty() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(text(),'Missing Cart items?')]")
            ));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }


    /** Proceed to checkout */
    public void proceedToBuy() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(placeOrderBtn)).click();
        } catch (StaleElementReferenceException e) {
            System.out.println("Stale element on Place Order button. Retrying...");
            WebElement freshPlaceOrderBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button/span[contains(text(),'Place Order')]"))); // update locator if needed
            freshPlaceOrderBtn.click();
        }
    }

    
    public void doLogin() {
        try {
            // Enter phone number
            wait.until(ExpectedConditions.visibilityOf(phoneNoElement))
                .sendKeys(ConfigLoader.getProperty("validPhoneNoForCorrectOTP"));

            // Click Continue
            wait.until(ExpectedConditions.elementToBeClickable(continueButtonElement)).click();

            // Instead of Thread.sleep(7000), wait until login button is clickable
            wait.until(ExpectedConditions.elementToBeClickable(loginButtonElement)).click();

        } catch (StaleElementReferenceException e) {
//            System.out.println("Stale element detected. Retrying login...");

            // Re-locate elements fresh and retry
//            WebElement phoneField = wait.until(ExpectedConditions.visibilityOfElementLocated(
//                By.xpath("//input[contains(@class, 'Jr-g+f') and @type='text' and @maxlength='auto']")));
//            phoneField.sendKeys(ConfigLoader.getProperty("validPhoneNoForCorrectOTP"));
//
//            WebElement continueBtn = wait.until(ExpectedConditions.elementToBeClickable(
//                By.xpath("//form/div[@class='aPGMpN']/button[@type='submit' and contains(@class, '_7Pd1Fp')]")));
//            continueBtn.click();
//
//            WebElement loginBtn = wait.until(ExpectedConditions.elementToBeClickable(
//                By.xpath("//button[contains(text(),'Login')]")));
//            loginBtn.click();
        }
    }


    public boolean isAddressPageLoaded() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(addressStep)).isDisplayed();
        } catch (StaleElementReferenceException e) {
            System.out.println("Stale element on Address Page. Retrying...");
            WebElement freshAddressStep = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//span[contains(text(),'Address')]"))); // adjust locator if needed
            return freshAddressStep.isDisplayed();
        }
    }

    public boolean isPaymentPageLoaded() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(paymentStep)).isDisplayed();
        } catch (StaleElementReferenceException e) {
            System.out.println("Stale element on Payment Page. Retrying...");
            WebElement freshPaymentStep = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//span[contains(text(),'Payment')]"))); // adjust locator if needed
            return freshPaymentStep.isDisplayed();
        }
    }

    
    public boolean isPlaceOrderButtonEnabled() {
        return driver.findElement(By.id("place-order-btn")).isEnabled();
    }
    
    public void clickSaveAndDeliverHere() {
        driver.findElement(saveAndDeliverHereBtn).click();
    }

    public boolean isNameErrorDisplayed() {
        return driver.findElement(nameErrorMsg).isDisplayed();
    }

    public boolean isPhoneErrorDisplayed() {
        return driver.findElement(phoneErrorMsg).isDisplayed();
    }
    public void closeCurrentWindow(String parentWindow) {
		driver.close();
		driver.switchTo().window(parentWindow);
	}
}
