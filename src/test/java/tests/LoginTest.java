package tests;

import org.testng.annotations.Test;

import base.BaseTest;
import pages.HomePage;
import pages.JobApply;
import pages.LoginPage;
import pages.ProfileUpdate;
import utils.ConfigReader;

public class LoginTest extends BaseTest {
	@Test
	public void testLoginToNaukri() {
		LoginPage loginPage = new LoginPage(driver);
		loginPage.waitForLoginLogo();
		loginPage.loginToNaukri(ConfigReader.get("username"), ConfigReader.get("password"));
		loginPage.waitForNaukriLogo();
		/*
		 * ProfileUpdate profileUpdate = new ProfileUpdate(driver); String resumePath =
		 * "C:\\Users\\kanhucharan.ghadai\\Documents\\Zoom\\KanhuCharanGhadai_Resume.pdf";
		 * profileUpdate.updateResume(resumePath);
		 */

		HomePage homePage = new HomePage(driver);
		homePage.searchJob(
				"Java Selenium, Manual Testing, Qa Engineer, Automation Testing, QA Automation, SDET, API Testing");
		JobApply jobApply = new JobApply(driver);
		jobApply.applyLast1DayFreshnessFilter();
		jobApply.applyJobsFromSearchResults();
		

	}

}
