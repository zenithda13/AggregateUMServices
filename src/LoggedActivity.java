

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class LoggedActivity {
	public static HashMap<Integer,String> APP_MAP;
	static{
		APP_MAP = new HashMap<Integer,String>();
		APP_MAP.put(2, "QUIZPACK");
		APP_MAP.put(3, "WEBEX");
		APP_MAP.put(5, "KNOWLEDGE_SEA");
		APP_MAP.put(8, "KT");
		APP_MAP.put(20, "QUIZGUIDE");
		APP_MAP.put(23, "SQLKNOT");
		APP_MAP.put(25, "QUIZJET");
		APP_MAP.put(35, "ANIMATED_EXAMPLE");
	}
	private int appId;
	private String session;
	private String label;
	private int activityId;
	private int parent;
	private double result;
	private Date date;
	private String dateStr;
	private double time; // time in seconds

	private long dateNS;
	private String svc;
	private String allParameters;
	
	public LoggedActivity(int appId, String session, String label,
			int activityId, int parent, double result, String dateStr, long dateNS,
			String svc, String allParameters) {
		super();
		this.appId = appId;
		this.session = session;
		this.label = label;
		this.activityId = activityId;
		this.parent = parent;
		this.result = result;
		
		try {
			this.date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(dateStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.date = null;
		}
    	
		this.dateStr = dateStr;
		this.dateNS = dateNS;
		this.svc = svc;
		this.allParameters = allParameters;
	}

	public int getAppId() {
		return appId;
	}

	public void setAppId(int appId) {
		this.appId = appId;
	}

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public int getActivityId() {
		return activityId;
	}

	public void setActivityId(int activityId) {
		this.activityId = activityId;
	}

	public int getParent() {
		return parent;
	}

	public void setParent(int parent) {
		this.parent = parent;
	}

	public double getResult() {
		return result;
	}

	public void setResult(double result) {
		this.result = result;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getDateStr() {
		return dateStr;
	}

	public void setDateStr(String dateStr) {
		this.dateStr = dateStr;
	}
	
	public long getDateNS() {
		return dateNS;
	}

	public void setDateNS(long dateNS) {
		this.dateNS = dateNS;
	}

	public String getSvc() {
		return svc;
	}

	public void setSvc(String svc) {
		this.svc = svc;
	}

	public String getAllParameters() {
		return allParameters;
	}

	public void setAllParameters(String allParameters) {
		this.allParameters = allParameters;
	}
	
	public double getTime() {
		return time;
	}

	public void setTime(double time) {
		this.time = time;
	}

	public static String getLabel(int appId){
		String res = APP_MAP.get(appId);
		if (res == null) res = "OTHER";
		return res;
	}
	
	
	
}
