

import java.util.ArrayList;

public class User {
	private int userId;
	private String userLogin;
	private ArrayList<LoggedActivity> activity;
	
	public User(int userId, String userLogin){
		this.userId = userId;
		this.userLogin = userLogin;
		activity = new ArrayList<LoggedActivity>();
	}
	
	public void addLoggedActivity(LoggedActivity act){
		this.activity.add(act);
	}
	
	public ArrayList<LoggedActivity> getActivity(){
		return activity;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUserLogin() {
		return userLogin;
	}

	public void setUserLogin(String userLogin) {
		this.userLogin = userLogin;
	}
	
	public void computeActivityTimes(){
		if (getActivity() != null && getActivity().size() > 0){
			double duration = 0.0;
			LoggedActivity previousAct = getActivity().get(0);
			previousAct.setTime(0.0);
			String prevSession = "";
			LoggedActivity currentAct;
			for(int i=1; i<getActivity().size(); i++){
				currentAct = getActivity().get(i);
				if(currentAct.getSession().equalsIgnoreCase(previousAct.getSession())){
					duration = (currentAct.getDateNS()-previousAct.getDateNS())/1000000000.0;
					currentAct.setTime(duration);
				}else{
					currentAct.setTime(0.0);
				}
				
				previousAct = currentAct;
			}			
		}
		
	}
}
