package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import utils.WaitUtils;

public class HomePage {
	private WebDriver driver;
	private WaitUtils waitUtils;

	public HomePage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
		waitUtils = new WaitUtils(driver);
	}

	@FindBy(xpath = "//span[text()='Search jobs here']")
	private WebElement placeholderSpan;

	@FindBy(xpath = "//input[contains(@placeholder, 'designation')]")
	private WebElement skillInput;

	@FindBy(id = "experienceDD")
	private WebElement experienceDropdown;

	@FindBy(xpath = "//button[@class='nI-gNb-sb__icon-wrapper']/span[@class='ni-gnb-icn ni-gnb-icn-search']")
	private WebElement searchButton;

	private final By experience3YearsOption = By.xpath("//ul[contains(@class, 'dropdown')]//li[@title='3 years']");

	public void searchJob(String keyword) {

		waitUtils.waitForElementToBeVisible(placeholderSpan);
		placeholderSpan.click();

		waitUtils.waitForElementToBeVisible(skillInput);
		skillInput.sendKeys(keyword);

		waitUtils.waitForElementToBeVisible(experienceDropdown);
		experienceDropdown.click();
		WebElement experienceOption = waitUtils.waitForElementToBeClickable(experience3YearsOption);
		experienceOption.click();

		waitUtils.waitForElementToBeClickable(searchButton);
		searchButton.click();
	}

}
