

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class ActivityReport
 */
@WebServlet("/ActivityReport")
public class ActivityReport extends HttpServlet {
	
	
	private static final long serialVersionUID = 1L;
	public static DecimalFormat df = new DecimalFormat("#.##");   
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ActivityReport() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		boolean error = false;

        String errorMsg = "";
        um2DBInterface um2_db;
        ConfigManager cm = new ConfigManager(this); // this object gets the database connections values

        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();

        String groupId = request.getParameter("grp"); // group id
        String header = request.getParameter("header"); // include or not the header
        boolean incHeader = (header != null && header.equalsIgnoreCase("yes"));
        String output = "";
        if (groupId == null || groupId.length() < 1) {
            error = true;
            errorMsg = "group identifier not provided or invalid";
        } else {

            um2_db = new um2DBInterface(cm.um2_dbstring, cm.um2_dbuser, cm.um2_dbpass);
            um2_db.openConnection();

            ArrayList<User> grp_activity = um2_db.getActivity(groupId);
            
            if(incHeader){
            	out.print("user,group,session,timebin,");
        		out.print("appid,applabel,");
        		out.print("activityid,parentactivityid,result,");
        		out.print("date,datestring,datenanoseconds,durationseconds");        		
        		out.print("svc,allparameters\n");
            }
            
            if (grp_activity == null) {
                error = true;
                out.print("no activity found");
            } else {
            	String userName = "";
            	String session = "";
                for (User user : grp_activity) {
                	user.computeActivityTimes();
                	userName = user.getUserLogin();
                	int att = 0;
                	session = "";
                	for(LoggedActivity a : user.getActivity()){
                		
                		if(session.equals("")){
                			att++;
                			session = a.getSession();
                		}else if(session.equalsIgnoreCase(a.getSession())) {
                			att++;
                			//session = a.getSession();
                		}else{
                			att = 1;
                			session = a.getSession();
                		}
                		out.print("'"+userName+"','"+groupId+"','"+a.getSession()+"',"+att+",");
                		out.print(a.getAppId()+",'"+LoggedActivity.getLabel(a.getAppId())+"',");
                		out.print("'"+a.getActivityId()+","+a.getParent()+","+a.getResult()+",");
                		out.print("'"+a.getDate().toString()+"','"+a.getDateStr().toString()+"',"+a.getDateNS()+",");
                		out.print(""+df.format(a.getTime())+",");
                		
                		out.print("'"+a.getSvc()+"','"+a.getAllParameters()+"'\n");
                	}
//                	out.print("'"+row[0]+
//                    		"','"+row[1]+
//                    		"','"+row[2]+
//                    		"','"+row[3]+
//                    		"','"+row[4]+
//                    		"','"+row[5]+
//                    		"','"+row[6]+
//                    		"','"+row[7]+
//                    		"','"+row[8]+
//                    		"','"+row[9]+
//                    		"','"+row[10]+
//                    		"'\n");
                }
            }

            um2_db.closeConnection();
        }
        

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}

}
