package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import utils.WaitUtils;

public class ProfileUpdate {
	private WebDriver driver;
	private WaitUtils waitUtils;
	
	public ProfileUpdate(WebDriver driver) {
		this.driver=driver;
		PageFactory.initElements(driver, this);
		waitUtils = new WaitUtils(driver);
	}
	@FindBy(xpath="//div[@class='view-profile-wrapper']/a")
	private WebElement viewProfile;
	
	@FindBy(xpath="//div[@id='result']//following::input[@value='Update resume']")
	private WebElement updateResumeButton;
	private By fullName = By.xpath("//div[@class='hdn']/span[@class='fullname']");



	
	public void updateResume(String resumeFilePath) {
		waitUtils.waitForElementToBeClickable(viewProfile).click();
		waitUtils.waitForElementToBeVisible(fullName);
		waitUtils.waitForElementToBeClickable(updateResumeButton).click();
		WebElement fileUploadInput = driver.findElement(By.xpath("//input[@type='file']"));
		fileUploadInput.sendKeys(resumeFilePath);
	}

}

