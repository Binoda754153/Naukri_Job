package pages;

import java.io.*;
import java.util.*;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import utils.WaitUtils;

public class JobApply {
    private WebDriver driver;
    private WaitUtils waitUtils;

    public JobApply(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
        waitUtils = new WaitUtils(driver);
    }

    @FindBy(xpath = "//span[contains(text(), 'All Filters')]")
    private WebElement allFiltersSpan;

    @FindBy(xpath = "//button[@id='filter-freshness']")
    private WebElement freshnessDropdown;

    private final By last1DayOption = By.xpath("//li[@title='Last 1 day']");

    public void applyLast1DayFreshnessFilter() {
        waitUtils.waitForElementToBeVisible(allFiltersSpan);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", freshnessDropdown);
        waitUtils.waitForElementToBeClickable(freshnessDropdown).click();
        WebElement oneDay = waitUtils.waitForElementToBeClickable(last1DayOption);
        oneDay.click();
    }

    public void applyJobsFromSearchResults() {
        waitUtils.waitForElementToBeVisible(By.xpath("//button[@id='filter-freshness' and @title='Last 1 day']"));
        By jobCardLocator = By.xpath("//div[@class='cust-job-tuple layout-wrapper lay-2 sjw__tuple ']");
        List<WebElement> jobCards = driver.findElements(jobCardLocator);
        String parentWindow = driver.getWindowHandle();

        for (int i = 0; i < jobCards.size(); i++) {
            try {
                jobCards = driver.findElements(jobCardLocator);
                WebElement jobCard = jobCards.get(i);
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", jobCard);
                waitUtils.waitForElementToBeClickable(jobCard).click();

                Set<String> allHandles = driver.getWindowHandles();
                for (String handle : allHandles) {
                    if (!handle.equals(parentWindow)) {
                        driver.switchTo().window(handle);
                        break;
                    }
                }

                By applyButton = By.id("apply-button");
                if (driver.findElements(applyButton).size() > 0) {
                    WebElement applyBtn = driver.findElement(applyButton);
                    if (applyBtn.isDisplayed() && applyBtn.isEnabled()) {
                        applyBtn.click();
                        System.out.println("✅ Apply clicked for job #" + (i + 1));
                    }
                } else {
                    System.out.println("⚠️ Apply button not found for job #" + (i + 1));
                }

                handleRecruiterQuestions();

                driver.close();
                driver.switchTo().window(parentWindow);

            } catch (Exception e) {
                System.out.println("❌ Error on job #" + (i + 1) + ": " + e.getMessage());
                driver.switchTo().window(parentWindow);
            }
        }
    }

    public void handleRecruiterQuestions() {
        try {
            for (WebElement frame : driver.findElements(By.tagName("iframe"))) {
                if (frame.isDisplayed()) {
                    driver.switchTo().frame(frame);
                    break;
                }
            }

            Map<String, String> savedAnswers = loadQA();
            Set<String> answeredQuestions = new HashSet<>();

            for (int attempt = 0; attempt < 10; attempt++) {
                List<WebElement> questionSpans = driver.findElements(By.xpath("//div[@class='botMsg msg ']/div/span"));
                boolean newQuestionFound = false;

                for (WebElement questionSpan : questionSpans) {
                    String questionText = questionSpan.getText().trim();
                    if (answeredQuestions.contains(questionText)) continue;

                    String answer = savedAnswers.get(questionText);
                    if (answer == null) {
                        System.out.println("❌ No answer found for: " + questionText);
                        saveUnmatchedQuestion(questionText);
                        answeredQuestions.add(questionText);
                        continue;
                    }

                    newQuestionFound = true;

                    List<WebElement> radioOptions = driver.findElements(By.xpath("//div[@class='ssrc__radio-btn-container']//label"));
                    boolean radioSelected = false;
                    for (WebElement option : radioOptions) {
                        String optionText = option.getText().trim();
                        if (optionText.equalsIgnoreCase(answer)) {
                            waitUtils.waitForElementToBeClickable(option).click();
                            System.out.println("✅ Selected radio: " + optionText);
                            radioSelected = true;
                            break;
                        }
                    }

                    if (!radioSelected) {
                        WebElement chatInput = waitUtils.waitForElementToBeVisible(By.xpath("//div[@class='chatbot_InputContainer']//input"));
                        chatInput.clear();
                        chatInput.sendKeys(answer);
                        System.out.println("✅ Typed answer: " + questionText + " → " + answer);

                        WebElement sendButton = waitUtils.waitForElementToBeClickable(By.xpath("//div[@class='sendMsg']"));
                        sendButton.click();
                        System.out.println("✅ Clicked Send button.");
                    }

                    logAnsweredQuestion(questionText, answer);

                    answeredQuestions.add(questionText);
                    Thread.sleep(2000);
                    break;
                }

                if (!newQuestionFound) break;
            }

            try {
                WebElement finalSave = driver.findElement(By.xpath("//button[contains(text(), 'Save')]"));
                if (finalSave.isDisplayed() && finalSave.isEnabled()) {
                    finalSave.click();
                    System.out.println("✅ Clicked Final Save.");
                }
            } catch (Exception ignored) {}

            driver.switchTo().defaultContent();

        } catch (Exception e) {
            System.out.println("❌ Error in recruiter question handler: " + e.getMessage());
            driver.switchTo().defaultContent();
        }
    }

    private Map<String, String> loadQA() {
        Map<String, String> qaMap = new HashMap<>();
        File file = new File("C:\\Users\\kanhucharan.ghadai\\eclipse-workspace\\Auto-Apply\\src\\qa_answers.txt");

        System.out.println("📁 Looking for qa_answers.txt at: " + file.getAbsolutePath());

        if (!file.exists()) {
            System.out.println("⚠️ qa_answers.txt not found at the given path.");
            return qaMap;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || !line.contains("=")) continue;
                String[] parts = line.split("=", 2);
                qaMap.put(parts[0].trim(), parts[1].trim());
            }
        } catch (IOException e) {
            System.out.println("❌ Error reading qa_answers.txt: " + e.getMessage());
        }

        return qaMap;
    }

    private void saveUnmatchedQuestion(String question) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("unmatched_questions.txt", true))) {
            writer.write(question);
            writer.newLine();
            System.out.println("📝 Saved unmatched question: " + question);
        } catch (IOException e) {
            System.out.println("❌ Error saving unmatched question: " + e.getMessage());
        }
    }

    private void logAnsweredQuestion(String question, String answer) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("answered_questions_log.txt", true))) {
            writer.write("Question: " + question);
            writer.newLine();
            writer.write("Answer: " + answer);
            writer.newLine();
            writer.write("----------");
            writer.newLine();
        } catch (IOException e) {
            System.out.println("❌ Error logging answered question: " + e.getMessage());
        }
    }
}
