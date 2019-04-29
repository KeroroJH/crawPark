package kr.go.gwangjin;

public class Car {
	String carNum;
	String inTime;
	String outTime;
	int dayCount;
	Boolean today = false;
	Boolean isOut = false;

	public Car(String carNum, String inTime, String outTime, int dayCount) {
		super();
		this.carNum = carNum;
		this.inTime = inTime;
		this.outTime = outTime;
		this.dayCount = dayCount;
	}
	
	public String getCarNum() {
		return carNum;
	}
	public void setCarNum(String carNum) {
		this.carNum = carNum;
	}
	public String getInTime() {
		return inTime;
	}
	public void setInTime(String inTime) {
		this.inTime = inTime;
	}
	public String getOutTime() {
		return outTime;
	}
	public void setOutTime(String outTime) {
		this.outTime = outTime;
	}
	public int getDayCount() {
		return dayCount;
	}
	public void setDayCount(int dayCount) {
		this.dayCount = dayCount;
	}
	public Boolean getIsOut() {
		return isOut;
	}
	public void setIsOut(Boolean isOut) {
		this.isOut = isOut;
	}
	public Boolean getToday() {
		return today;
	}
	public void setToday(Boolean today) {
		this.today = today;
	}

	@Override
	public String toString() {
		return "Car [carNum=" + carNum + ", inTime=" + inTime + ", outTime=" + outTime + ", dayCount=" + dayCount + "]";
	}
	
	
}
