package tests;

import org.testng.Assert;
import org.testng.annotations.*;

import base.ConfigLoader;
import base.DriverSetup;
import pages.CartPage;
import pages.ProductPage;
import org.openqa.selenium.WebDriver;

public class CartTest {
    private ProductPage productPage;
    private CartPage cartPage;
    private static String parentWindow = "";
    private WebDriver driver;
    private String baseUrl = ConfigLoader.getProperty("baseUrl");

    @Parameters("browser")
    @BeforeClass
    public void setUp(String browser) {
        DriverSetup.setDriver(browser);                 // init driver for given browser
        driver = DriverSetup.getDriver();
        driver.get(baseUrl);         // open base URL

        productPage = new ProductPage(driver);
        cartPage = new CartPage(driver);

        parentWindow = driver.getWindowHandle();
    }

    @AfterClass
    public void tearDown() {
        DriverSetup.quitDriver();
    }

    @Test(priority = 1, description = "Add product to cart from details page")
    public void testAddToCart() {
        productPage.searchProduct("mobile phone");
        productPage.openFirstProduct();
        cartPage.addToCart();
        Assert.assertTrue(!cartPage.isCartEmpty(),
                "Product should be added to cart");
    }

    @Test(priority = 2, description = "Modify quantity in cart and verify price update")
    public void testModifyQuantity() throws InterruptedException {
        String priceBefore = cartPage.increaseQuantity();
        Thread.sleep(2000);
        String priceAfter = cartPage.decreaseQuantity();
        Assert.assertNotEquals(priceBefore, priceAfter, "Total price should update when quantity changes");
    }

    @Test(priority = 3, description = "Remove item from cart and confirm removal")
    public void testRemoveItem() throws InterruptedException {
        Thread.sleep(2000);
        cartPage.removeItem();
        Thread.sleep(3000);
        Assert.assertTrue(cartPage.isCartEmpty(), "Cart should be empty after removing item");
    }

    @Test(priority = 4, description = "Proceed to buy and check address/payment steps")
    public void testProceedToBuy() throws InterruptedException {
        // save current window handle before opening new tab
        String currentParentWindow = driver.getWindowHandle();

        productPage.searchProduct("mobile phone");
        productPage.openFirstProduct();

        for (String windowHandle : driver.getWindowHandles()) {
            if (!(windowHandle.equals(currentParentWindow) || windowHandle.equals(parentWindow))) {
                driver.switchTo().window(windowHandle);
                cartPage = new CartPage(driver); // refresh page object
                Thread.sleep(10000);
                break;
            }
        }

        cartPage.addToCart();
        cartPage.proceedToBuy();
        Assert.assertTrue(cartPage.isAddressPageLoaded(), "Address page should load after Place Order");
        System.out.println("end");
    }

    @Test(priority = 5, description = "Try placing order without address or payment")
    public void testOrderWithoutAddressPayment() throws InterruptedException {
        cartPage.doLogin();
        cartPage.clickSaveAndDeliverHere();

        // Assert validation errors
        Assert.assertTrue(cartPage.isNameErrorDisplayed(),
                "Error message should be displayed for empty Name field");

        Assert.assertTrue(cartPage.isPhoneErrorDisplayed(),
                "Error message should be displayed for empty Phone field");
    }
}
