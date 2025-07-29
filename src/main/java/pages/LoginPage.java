package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import utils.WaitUtils;

public class LoginPage {
	private WebDriver driver;
	private WaitUtils waitUtils;

	public LoginPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
		waitUtils = new WaitUtils(driver);
	}

	@FindBy(id = "login_Layer")
	private WebElement loginBtn;

	@FindBy(xpath = "//input[@placeholder='Enter your active Email ID / Username']")
	private WebElement emailInput;

	@FindBy(xpath = "//input[@type='password']")
	private WebElement passwordInput;

	@FindBy(xpath = "//button[@type='submit']")
	private WebElement submitLogin;

	

	public void waitForLoginLogo() {
		waitUtils.waitForElementToBeVisible(By.id("login_Layer"));
	}

	public void waitForNaukriLogo() {
		waitUtils.waitForElementToBeVisible(By.xpath("//a[@class='nI-gNb-header__logo nI-gNb-company-logo']"));
	}

	public void loginToNaukri(String username, String password) {
		waitUtils.waitForElementToBeVisible(loginBtn);
		loginBtn.click();

		waitUtils.waitForElementToBeVisible(emailInput);
		emailInput.sendKeys(username);

		waitUtils.waitForElementToBeVisible(passwordInput);
		passwordInput.sendKeys(password);

		waitUtils.waitForElementToBeClickable(submitLogin);
		submitLogin.click();
	}
}
