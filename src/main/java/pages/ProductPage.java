package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Set;

public class ProductPage {
    private WebDriver driver;
    private WebDriverWait wait;

    @FindBy(xpath = "//input[@type='text' and @name='q']")
    private WebElement searchBox;

    @FindBy(xpath = "//button[@type='submit']")
    private WebElement searchButton;

    @FindBy(xpath = "//a[@class='CGtC98' and @target='_blank']")
    private List<WebElement> productList;
    
    @FindBy(xpath = "//*[@id='container']/div/div[3]/div[1]/div[1]/div/div[1]/div/section[2]/div[4]/div[1]/select")
    private WebElement minValue;
    
    @FindBy(xpath = "//*[@id='container']/div/div[3]/div[1]/div[1]/div/div[1]/div/section[2]/div[4]/div[3]/select")
    private WebElement maxValue;
    
    

    public ProductPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        PageFactory.initElements(driver, this);
    }

    /** Search product */
    public void searchProduct(String keyword) {
        searchBox.clear();
        searchBox.sendKeys(keyword);
        searchButton.click();
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//a[@class='CGtC98']")));
    }

    /** Search without keyword */
    public void searchWithoutKeyword() {
        searchBox.clear();
        searchButton.click();
    }

    /** Filter by brand */
    public void filterByBrand(String brandName) {
        By brandLocator = By.xpath("//div[@class='ewzVkT _3DvUAf' and @title='" + brandName + "']//div[@class='XqNaEv']");
        WebElement brandFilter = wait.until(ExpectedConditions.elementToBeClickable(brandLocator));
        brandFilter.click();
        // wait for refreshed product list instead of stalenessOf
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//a[@class='CGtC98']")));
    }

    /** Filter by RAM */
    public void filterByRAM(String ramSize) {
    	try {
    		By ramLocator = By.xpath("//div[@class='ewzVkT _3DvUAf' and @title='" + ramSize + "']//div[@class='XqNaEv']");
            WebElement ramFilter = wait.until(ExpectedConditions.elementToBeClickable(ramLocator));
            ramFilter.click();
            wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//a[@class='CGtC98']")));
		} catch (StaleElementReferenceException e) {
			// wait for sometimes 
		}
        
    }

    /** Filter by Price Range */
    /** Filter by Price Range */
    public void filterByPrice(String min, String max) {
        By minSelectLocator = By.xpath("//div[@class='suthUA']/select[@class='Gn+jFg']");
        By maxSelectLocator = By.xpath("//div[@class='tKgS7w']/select[@class='Gn+jFg']");

        // --- Select Min ---
        WebElement minSelectElement = wait.until(ExpectedConditions.elementToBeClickable(minSelectLocator));
        Select minValueSelect = new Select(minSelectElement);
        minValueSelect.selectByValue(min);

        // --- Select Max (re-find after DOM refresh) ---
        WebElement maxSelectElement = wait.until(ExpectedConditions.elementToBeClickable(maxSelectLocator));
        Select maxValueSelect = new Select(maxSelectElement);
        maxValueSelect.selectByValue(max);

        // --- Wait for results to load ---
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//a[@class='CGtC98']")));
//        List<WebElement> appliedFilters = driver.findElements(By.xpath("//div[@class='tPjUPw']//span")); 
//        for (WebElement filter : appliedFilters) {
//            System.out.println("Applied filter: " + filter.getText());
//        }
    }



    /** Sort results */
    public void sortBy(String criteria) {
        By sortLocator = By.xpath("//div[@class='sHCOk2']//div[text()='" + criteria + "']");
        wait.until(ExpectedConditions.elementToBeClickable(sortLocator)).click();
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//a[@class='CGtC98']")));
    }

    /** Open first product in list */
    public void openFirstProduct() {
        WebElement firstProduct = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[@class='CGtC98']")));
        String parent = driver.getWindowHandle();
        firstProduct.click();

        // switch to new tab
        Set<String> handles = driver.getWindowHandles();
        for (String handle : handles) {
            if (!handle.equals(parent)) {
                driver.switchTo().window(handle);
                break;
            }
        }

        // wait for specs in product details page
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.DOjaWF.YJG4Cf")));
    }

    /** Verify product specifications are visible */
    public boolean isProductSpecsVisible() {
        try {
            WebElement specs = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.DOjaWF.YJG4Cf")));
            return specs.isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    /** Get list of products */
    public List<WebElement> getProductList() {
        return driver.findElements(By.xpath("//a[@class='CGtC98']"));
    }
}
