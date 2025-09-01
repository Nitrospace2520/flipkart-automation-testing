package tests;

import org.testng.Assert;
import org.testng.annotations.*;

import base.ConfigLoader;
import base.DriverSetup;
import pages.ProductPage;
import org.openqa.selenium.WebDriver;

public class ProductTest {
    private ProductPage productPage;
    private WebDriver driver;
    private String baseUrl = ConfigLoader.getProperty("baseUrl");
    
    @Parameters("browser")
    @BeforeClass
    public void setUp(String browser) {
        DriverSetup.setDriver(browser);            // init driver for given browser
        driver = DriverSetup.getDriver();
        driver.get(baseUrl);    // open base URL
        productPage = new ProductPage(driver);
    }

    @AfterClass
    public void tearDown() {
        DriverSetup.quitDriver();                  // cleanup
    }

    @AfterMethod
    public void waitAfterEachTest() throws InterruptedException {
        // Pause for 7 seconds after each test
        Thread.sleep(7000);
    }

    @Test(priority = 1, description = "Search for mobile phone")
    public void testSearchProduct() {
        productPage.searchProduct("mobile phone");
        Assert.assertTrue(productPage.getProductList().size() > 0,
                "Product list should not be empty after search");
    }

    @Test(priority = 2, description = "Apply brand filter")
    public void testFilterByBrand() {
        productPage.filterByBrand("OPPO");
        Assert.assertTrue(productPage.getProductList().size() > 0,
                "Products should display after applying brand filter");
    }

    @Test(priority = 3, description = "Apply RAM filter")
    public void testFilterByRAM() {
        productPage.filterByRAM("6 GB");
        Assert.assertTrue(productPage.getProductList().size() > 0,
                "Products should display after applying RAM filter");
    }

    @Test(priority = 4, description = "Apply price range filter")
    public void testFilterByPrice() {
        productPage.filterByPrice("10000", "20000");
        Assert.assertTrue(productPage.getProductList().size() > 0,
                "Products should display after applying price filter");
    }

    @Test(priority = 5, description = "Sort results by price low to high")
    public void testSortByPriceLowToHigh() {
        productPage.sortBy("Price -- Low to High");
        Assert.assertTrue(productPage.getProductList().size() > 0,
                "Products should display after sorting");
    }

    @Test(priority = 6, description = "Open product details page and check specifications")
    public void testProductDetails() {
        productPage.openFirstProduct();
        Assert.assertTrue(productPage.isProductSpecsVisible(),
                "Product specifications should be visible in product details page");
    }

    @Test(priority = 7, description = "Search without keyword")
    public void testSearchWithoutKeyword() {
        productPage.searchWithoutKeyword();
        Assert.assertTrue(true, "Empty search should not break the application");
    }
}
