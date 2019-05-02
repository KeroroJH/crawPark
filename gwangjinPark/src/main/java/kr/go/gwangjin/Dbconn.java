package kr.go.gwangjin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Dbconn {
	Connection conn = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	
	final String DRIVER = "com.mysql.jdbc.Driver";
	 String URL;
	 String USER;
	 String PW;
		
	public Dbconn(String url, String user, String pw) throws ClassNotFoundException {
		Class.forName(DRIVER);
		this.URL = url;
		this.USER = user;
		this.PW = pw;
	}

	Car isExist(Car car) throws SQLException{
		try {
			String query = "SELECT car,DATE_FORMAT(intime,'%Y%m%d %H%i') as intime,DATE_FORMAT(outtime,'%Y%m%d %H%i') as outtime,daycount from inoutcars WHERE car = ? AND DATE_FORMAT(intime,'%Y%m%d %H%i') = ?;";
			 conn = DriverManager.getConnection(URL, USER, PW);
			 stmt = conn.prepareStatement(query);
			 stmt.setString(1, car.getCarNum());
			 stmt.setString(2, timeForm(car.getInTime()));
			 rs = stmt.executeQuery();
			 if(rs.next()){
				 car.setToday(true);
				 car.setCarNum(rs.getString("car"));
				 car.setInTime(rs.getString("intime"));
				 car.setDayCount(rs.getInt("daycount"));
				 if(!(rs.getString("outtime") == null)){
					 car.setIsOut(true);
					 car.setOutTime(rs.getString("outtime"));
				 }
			 }
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			stmt.close();
			conn.close();
		}		
		return car;
	}
	
	void updateOutTime(Car car){
		try {
			String query = "update inoutcars set outtime=STR_TO_DATE(?,'%Y%m%d %H%i') WHERE car = ? AND DATE_FORMAT(intime,'%Y%m%d %H%i') = ?;";
			conn = DriverManager.getConnection(URL, USER, PW);
			stmt = conn.prepareStatement(query);
			stmt.setString(1, timeForm(car.getOutTime()));
			stmt.setString(2, car.getCarNum());
			stmt.setString(3, car.getInTime());
			stmt.executeUpdate();
			System.out.println("updated outTime : "+car.getCarNum());
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				stmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}		
	}
	
	void deleteOldData(){
		try {
			String query = "delete from inoutcars WHERE regdate < SUBDATE(CURDATE(),1);";
			conn = DriverManager.getConnection(URL, USER, PW);
			stmt = conn.prepareStatement(query);
			System.out.println("deleted 2dayBefore count : "+stmt.executeUpdate());
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				stmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}		
	}
	
	int insertData(Car car) throws SQLException{
		int result = 0;
		try {
			String query = "INSERT INTO inoutcars(car,intime,outtime,regdate,daycount) VALUES (?,STR_TO_DATE(?,'%Y%m%d %H%i'),STR_TO_DATE(?,'%Y%m%d %H%i'),now(),?)";
			 conn = DriverManager.getConnection(URL, USER, PW);
			 stmt = conn.prepareStatement(query);
			 stmt.setString(1, car.getCarNum());
			 stmt.setString(2, timeForm(car.getInTime()));
			 stmt.setString(3, timeForm(car.getOutTime()));
			 stmt.setInt(4, car.getDayCount());
			 result = stmt.executeUpdate();
			 System.out.println("insertd : "+car.getCarNum());
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			stmt.close();
			conn.close();
		}
		return result;
	}
	
	String timeForm(String a){
		if("-".equals(a))
			return null;
		String result="";
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("YYYY");
		String year = sdf.format(date);
			result = a.replace("/", "");
			result = result.replace(":", "");
		return year+result;
	}
}
