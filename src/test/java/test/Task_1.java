package test;

import static org.testng.Assert.assertTrue;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import au.com.bytecode.opencsv.CSVReader;
import io.github.bonigarcia.wdm.WebDriverManager;
import utility.Browser;
import utility.ConfigReader;

public class Task_1 {

	public WebDriver driver;
	public ConfigReader config;
	public String downloadFilepath;

	//	Go to https://www.investing.com/currencies/eur-usd-historical-data
	//	If filter is set to “Daily” set to monthly. If filter is set to “Monthly” change to daily.
	//	Download data
	//	Wait for the data to be downloaded (having a delay isn’t a good option)
	//	Once file is downloaded open the file
	//	Do an average of the first 10 “Price” values.
	//	If average is lower than 1.2 test pass if greater test fail.

	@BeforeTest
	public void launchBrowser() throws MalformedURLException {		
		
		config = new ConfigReader();		
		ChromeOptions options = new ChromeOptions();
		options.setBinary("/usr/bin/google-chrome");
		downloadFilepath = System.getProperty("user.dir") + "/download";
		HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
		chromePrefs.put("profile.default_content_settings.popups", 0);
		chromePrefs.put("download.default_directory", downloadFilepath);
		options.setExperimentalOption("prefs", chromePrefs);	    
		//options.addArguments("-incognito");
		//options.addArguments("--disable-popup-blocking");			
		//options.addArguments("--headless", "window-size=1024,768", "--no-sandbox");
		//options.addArguments("--no-sandbox");
		//options.addArguments("--disable-dev-shm-usage");
		
		
		switch(config.getGridSettings()) {
		case "true":

			DesiredCapabilities capabilities = DesiredCapabilities.chrome();
			capabilities.setJavascriptEnabled(true);			
			capabilities.setBrowserName("Chrome");
			capabilities.setPlatform(Platform.WINDOWS);			
			capabilities.setCapability(ChromeOptions.CAPABILITY, options);			
			driver = new RemoteWebDriver(new URL(config.getGridHub()),capabilities);			
			break;
			
		default:
			
			//System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "/driver/chromedriver.exe");	
			//WebDriverManager.chromedriver().setup();
			driver = new ChromeDriver(options);
		}
		
		driver.get(config.getAUT());
		//driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(20L, TimeUnit.SECONDS);
	}

	@Test (priority=0)
	public void login()  {	
		driver.findElement(By.xpath("//*[@id='userAccount']/descendant::a[contains(@class,'login')]")).click();	
		driver.findElement(By.xpath("//*[@id='loginPopup']/descendant::input[@id='loginFormUser_email']")).sendKeys(config.getEmail());
		driver.findElement(By.xpath("//*[@id='loginPopup']/descendant::input[@id='loginForm_password']")).sendKeys(config.getPassword());
		driver.findElement(By.xpath("//*[@id='loginPopup']/descendant::a[contains(@class,'newButton')]")).click();
	}

	@Test(priority=1)
	public void selectAndDownload() throws FileNotFoundException, InterruptedException {		
		Select TimeFrame = new Select(driver.findElement(By.xpath("//*[@id='column-content']/descendant::select[@id='data_interval']")));
		//switch(TimeFrame.getFirstSelectedOption().getText()) {
		//switch(TimeFrame.getFirstSelectedOption().toString()) {
		//case "Monthly":
			TimeFrame.selectByVisibleText("Daily"); //break;
		//case "Daily":
			//TimeFrame.selectByVisibleText("Monthly");break;
		//}

		Browser.DeleteOldFile(downloadFilepath, "EUR_USD Historical Data.csv");			
		WebDriverWait wait = new WebDriverWait(driver,20L);
		try {		
			wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath("//*[@id='column-content']/descendant::a[contains(@class,'downloadBlueIcon')]"))));
			Thread.sleep(2000);//added this wait sometimes the file downloaded has no data
			driver.findElement(By.xpath("//*[@id='column-content']/descendant::a[contains(@class,'downloadBlueIcon')]")).click();
		}catch(Exception e) {}

		while(!Browser.IsFileDownloaded(downloadFilepath, "EUR_USD Historical Data.csv")) {
			Thread.sleep(1000);
		}

	}

	@AfterTest
	public void quit(){
		driver.close();
	}

	@Test(priority=2)
	public void calculate() throws FileNotFoundException, InterruptedException {
		String csvfile = downloadFilepath + "//EUR_USD Historical Data.csv";
		CSVReader data= new CSVReader(new FileReader(csvfile));
		//String[] cell ;
		String [] nextLine;		

		double sum = 0;
		try {
			data.readNext();
			for(int i=0;i<10;i++) {
				nextLine = data.readNext();
				Float price =Float.parseFloat(nextLine[1]);
				sum=sum+price;
			}			

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Sum is: " +sum);
		System.out.println("Average is: " +sum/10);

		if(sum/10<1.2) {
			System.out.println("Passed!");
		}else {
			System.out.println("Failed!");
			assertTrue(sum/10 < 1.2);
		}				
	}



}
