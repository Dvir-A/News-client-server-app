package mmn16_2;

import java.sql.Time;

public class News {
	private Time _time;
	private String _newsMsg;
	
	public News(Time time,String massage) {
		this._newsMsg=massage;
		this._time=time;
	}
	
	public String toString() {
		return (this._time.toString()+">>>"+this._newsMsg);
	}
}
