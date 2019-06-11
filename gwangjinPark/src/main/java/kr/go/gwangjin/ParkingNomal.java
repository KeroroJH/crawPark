package kr.go.gwangjin;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class ParkingNomal {
 public static void main(String[] args) throws InterruptedException, IOException, ClassNotFoundException  {
	 //운영체제 알아내기
	 String OS = System.getProperty("os.name").toLowerCase();
	 FileReader input = null;
	 String chromeDriverPath = "";
	 if(OS.indexOf("win") >= 0){
		 System.out.println("this system OS is window");
		 chromeDriverPath = "c://parking//chromedriver.exe";
		 //DB설정 정보 가져오기
			input = new FileReader("C://parking//dbconn.pom");
	 }else if(OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0){
		 System.out.println("this system OS is Unix");
		 chromeDriverPath = "/root/parking/chromedriver";
		 //DB설정 정보 가져오기
			input = new FileReader("/root/parking/dbconn.pom");
	 }

	BufferedReader bufRead = new BufferedReader(input);
	bufRead.readLine();
	String dbUrl =  bufRead.readLine().replace("URL=","");
	String dbUser =  bufRead.readLine().replace("USER=","");
	String dbPw =  bufRead.readLine().replace("PW=","");
	bufRead.readLine();
	bufRead.readLine();
	String siteUrl =  bufRead.readLine().replace("URL=","");
	String siteUser =  bufRead.readLine().replace("USER=","");
	String sitePw =  bufRead.readLine().replace("PW=","");	
	bufRead.readLine();
	bufRead.readLine();
	String serialNO =  bufRead.readLine().replace("serialNO=","");	
	bufRead.close();
	//DB생성
	Dbconn dbconn = new Dbconn(dbUrl,dbUser,dbPw);
	 outer :
	 while(true){
		 //2들전 데이터 삭제
		 dbconn.deleteOldData();
		 //시리얼 검사
		 try {
			 if(! CryptoUtil.checkLicense(serialNO)){
				 System.out.println("serial is Expired");
				 return;
			 };
		 } catch (Exception e) {
			 e.printStackTrace();
		 }
		 WebCrawl cw = new WebCrawl(siteUrl,siteUser,sitePw,chromeDriverPath);
			try {
				if(cw.goCrawlReady())
					while(true){
						Long before = System.currentTimeMillis();
						
						List<Car> carList = cw.getCrawlData();
						System.out.println("filterd_in20min_car_count : "+carList.size());
						for(Car c : carList){
							//당일 출입기록 존재 확인
							dbconn.isExist(c);
							//아직 안나갔으며 지금 나갔다면 업데이트	
							if(c.getToday() && (!c.getIsOut()) && ( !"-".equals(c.getOutTime()) ) ){
								dbconn.updateOutTime(c);
							//입차 기록 없으면 insert	
							}else if(!c.getToday()){
								dbconn.insertData(c);
							}
						}
						Long after = System.currentTimeMillis();
						Long duringTime = after - before;
						System.out.println("during time : "+duringTime);
						if(duringTime < 15000)
						Thread.sleep(15000 - duringTime);
					}
			} catch (Exception e1) {
				e1.printStackTrace();
				cw.driver.quit();
				continue outer;
				
			}
	 }
 }
}