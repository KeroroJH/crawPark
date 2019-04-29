package kr.go.gwangjin;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class WebCrawl {
	 String introUrl;
	 String id;
	 String pw;
	 WebDriver driver;
	 private  WebElement text_box_id;
	 private  WebElement text_box_pw;
	 private  WebElement btn_login;
	 private  WebElement temp;
	 private  List<WebElement> trs;
	 private JavascriptExecutor js;
	 
	 public WebCrawl(String introUrl,String id, String pw, String chromeDriverPath) {
		 this.introUrl = introUrl;
		 this.id = id;
		 this.pw = pw;
		  System.setProperty("webdriver.chrome.driver",chromeDriverPath);
		  // Set Headless
		  ChromeOptions options = new ChromeOptions();
		  options.addArguments("headless");
		  options.addArguments("--no-sandbox");
		  options.addArguments("--disable-dev-shm-usage");		  
		  driver = new ChromeDriver(options);
	}

public boolean goCrawlReady() throws Exception{
	  driver.get(introUrl);
	  
	  //아이디 입력
	  text_box_id = driver.findElement(By.id("memb_id"));
	  text_box_id.clear();
	  text_box_id.sendKeys(id);
	  
	  //패스워드 입력
	  text_box_pw = driver.findElement(By.id("memb_pwd"));
	  text_box_pw.clear();
	  text_box_pw.sendKeys(pw);
	  
	  driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
	  // 로그인 버튼 클릭
	  btn_login = driver.findElement(By.xpath("//*[@id=\"login_form\"]/input[3]"));
		  btn_login.click();
		  System.out.println(new WebDriverWait(driver, 20000).until(webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete")));
	  // 주차관리 버튼 클릭
	  btn_login = driver.findElement(By.xpath("//*[@id=\"slider1_container\"]/div/div/div[2]/div[2]/ul/li[1]/div/p"));
	  btn_login.click();
	 	
	  driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
	 return new WebDriverWait(driver, 20000).until(webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));

 }
public List<Car> getCrawlData() throws Exception{
		List<Car> cars = new ArrayList<>();
	  js = (JavascriptExecutor) driver;
	 //페이징 삭제
	  js.executeScript("onPaging.value = false;");
	 // 주차리스트 가져오기
	 js.executeScript("getListData(1);");
	 Thread.sleep(900);
	 	//현재시간
		Date date = new Date();
		//10분전 시간
		Date date2 = new Date(System.currentTimeMillis() - 600 * 1000);
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		String time1 = sdf.format(date).substring(0, 4);
		String time2 = sdf.format(date2).substring(0, 4);
		//현재시간 기준 차량 임시저장
	  js.executeScript("__prepareService__ = $('#listData > table > tbody > tr:contains(\""+time1+"\")');");
	  Thread.sleep(300);
	  	//10분전시간 기준 차량 임시저장
	  js.executeScript("__preparePaymentService__ = $('#listData > table > tbody > tr:contains(\""+time2+"\")');");
	  Thread.sleep(300);
	  	//화면 모두 지우기
	  js.executeScript("$('#listData > table > tbody > tr').remove();");
	  Thread.sleep(300);
	   //현재시간 기준 차량 넣기
	  js.executeScript("$('#listData > table > tbody').append(__prepareService__);");
	  Thread.sleep(600);	  
	  	//10분전시간 기준 차량 넣기
	  js.executeScript("$('#listData > table > tbody').append(__preparePaymentService__);");
	  Thread.sleep(600);
	  	//차량 리스트 가져오기
	 temp = driver.findElement(By.xpath("//*[@id=\"listData\"]/table/tbody"));
	  trs = temp.findElements(By.cssSelector("tr")); 
	  Thread.sleep(200);
	  	//차량 객체들 리스트에 넣기
	  for(WebElement tr : trs){
		  int dayCount = Integer.parseInt(tr.findElement(By.cssSelector("td:nth-child(1)")).getText());
		  String carNum = tr.findElement(By.cssSelector("td:nth-child(2)")).getText();
		  String inTime = tr.findElement(By.cssSelector("td:nth-child(3)")).getText();
		  String[] timeSplit = inTime.split("\n");
		  		 inTime = timeSplit[0];
		  String outTime = timeSplit[1];
		  	Car car = new Car(carNum, inTime, outTime, dayCount);
		  cars.add(car);
		  System.out.println(car.toString());
	  }
	 
	 return cars;
}
}
