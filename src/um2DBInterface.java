import java.sql.*;
import java.util.*;

public class um2DBInterface extends dbInterface {
    public um2DBInterface(String connurl, String user, String pass) {
        super(connurl, user, pass);
    }

    // returns the user information given the username
    public String[] getUsrInfo(String usr) {
        try {
            String[] res = null;
            stmt = conn.createStatement();
            String query = "select U.name, U.email from ent_user U where U.login = '"
                    + usr + "';";
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                res = new String[2];
                res[0] = "";
                res[1] = "";
                res[0] = rs.getString("name").trim();
                res[1] = rs.getString("email").trim();
            }
            return res;
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            return null;
        } finally {
            this.releaseStatement(stmt, rs);
        }

    }

    // returns the activity of the user in content of type example
    public HashMap<String, String[]> getUserExamplesActivity(String usr) {
        try {
            HashMap<String, String[]> res = new HashMap<String, String[]>();
            stmt = conn.createStatement();
            String query = "select A.activity, "
                    + "AA.parentactivityid, count(UA.activityid) as nactions,  "
                    + "count(distinct(UA.activityid)) as distinctactions, "
                    + "(select count(AA2.childactivityid) from rel_activity_activity AA2 where AA2.parentactivityid = AA.parentactivityid) as totallines "
                    + "from ent_user_activity UA, rel_activity_activity AA, ent_activity A "
                    + " where UA.appid=3 and UA.userid = (select userid from ent_user where login='"
                    + usr
                    + "') "
                    + " and AA.parentactivityid=A.activityid and AA.childactivityid=UA.activityid "
                    + "group by AA.parentactivityid "
                    + "order by AA.parentactivityid;";
            // System.out.println(query);
            rs = stmt.executeQuery(query);

            // System.out.println(query);

            boolean noactivity = true;
            while (rs.next()) {
                noactivity = false;
                String[] act = new String[4];
                act[0] = rs.getString("activity");
                act[1] = rs.getString("nactions");
                act[2] = rs.getString("distinctactions");
                act[3] = rs.getString("totallines");
                res.put(act[0], act);
                // System.out.println(act[0]+" actions: "+act[2]+", "+act[3]+"/"+act[4]);
            }
            this.releaseStatement(stmt, rs);
            if (noactivity)
                return null;
            else
                return res;
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            return null;
        } finally {
            this.releaseStatement(stmt, rs);
        }

    }

    public HashMap<String, String[]> getUserQuestionsActivity(String usr) {
        try {
            HashMap<String, String[]> res = new HashMap<String, String[]>();
            stmt = conn.createStatement();
            String query = "(select AC.activity, count(UA.activityid) as nattempts,  sum(UA.Result) as nsuccess from um2.ent_user_activity UA, um2.ent_activity AC where UA.appid=25 and UA.userid = (select userid from um2.ent_user where login='"
                    + usr
                    + "') and AC.activityid=UA.activityid and UA.Result != -1 group by UA.activityid) \n";
            query += " UNION ALL \n";
            query += "(select QN.content_name as activity, count(UA.activityid) as nattempts,  sum(UA.Result) as nsuccess "
                    + " from um2.ent_user_activity UA, um2.sql_question_names QN where UA.appid=23 and "
                    + " UA.userid = (select userid from um2.ent_user where login='"
                    + usr
                    + "') and "
                    + " QN.activityid=UA.activityid and UA.Result != -1  "
                    + " group by UA.activityid); ";

            // System.out.println(query);
            rs = stmt.executeQuery(query);
            boolean noactivity = true;
            while (rs.next()) {
                noactivity = false;
                String[] act = new String[3];
                act[0] = rs.getString("activity");
                act[1] = rs.getString("nattempts");
                act[2] = rs.getString("nsuccess");
                if (act[0].length() > 0)
                    res.put(act[0], act);
            }
            this.releaseStatement(stmt, rs);
            if (noactivity)
                return null;
            else
                return res;
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            return null;
        } finally {
            this.releaseStatement(stmt, rs);
        }
    }

    public ArrayList<String[]> getClassList(String grp) {

        try {
            ArrayList<String[]> res = new ArrayList<String[]>();
            stmt = conn.createStatement();
            String query = "select U.userid, U.login, U.name, U.email "
                    + "from ent_user U, rel_user_user UU "
                    + "where UU.groupid = (select userid from ent_user where login='"
                    + grp + "' and isgroup=1) " + "and U.userid=UU.userid";
            // System.out.println(query);
            rs = stmt.executeQuery(query);
            int i = 0;
            while (rs.next()) {
                String[] act = new String[3];
                act[0] = rs.getString("login");
                act[1] = rs.getString("name").trim();
                act[2] = rs.getString("email").trim();
                res.add(act);
                // System.out.println(act[0]+" "+act[1]+" "+act[2]+" "+act[3]);
                i++;
            }
            this.releaseStatement(stmt, rs);
            return res;
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            return null;
        } finally {
            this.releaseStatement(stmt, rs);
        }
    }

    // get a list with the content and for each content item, all the concepts in an array list with  
    public ArrayList<String[]> getContentConcepts(String domain) {
        try {
            //HashMap<String, ArrayList<String[]>> res = new HashMap<String, ArrayList<String[]>>();
            ArrayList<String[]> res = new ArrayList<String[]>();
            stmt = conn.createStatement();
            String query = "SELECT CC.content_name, "
                    + " group_concat(CC.concept_name , ',', cast(CONVERT(CC.weight,DECIMAL(10,3)) as char ), ',' , cast(CC.direction as char) order by CC.weight separator ';') as concepts "
                    + " FROM agg_content_concept CC  "
                    + " WHERE CC.domain = '" + domain + "'"
                    + " group by CC.content_name order by CC.content_name;";
            rs = stmt.executeQuery(query);
            //System.out.println(query);
            //String content_name = "";
            //ArrayList<String[]> c_c = null;
            while (rs.next()) {
                String[] data = new String[2];
                data[0] = rs.getString("content_name");
                data[1] = rs.getString("concepts");
                
                res.add(data);
                //content_name = rs.getString("content_name");
                
                // System.out.println(topic);
                //String allconcepts = rs.getString("concepts");
                /*
                if (allconcepts == null
                        || allconcepts.equalsIgnoreCase("[null]")
                        || allconcepts.length() == 0) {
                    c_c = null;
                } else {
                    c_c = new ArrayList<String[]>();
                    String[] concepts = allconcepts.split(";");
                    for (int i = 0; i < concepts.length; i++) {
                        String[] concept = concepts[i].split(",");
                        c_c.add(concept); // concept_name, weight, direction
                        // System.out.println("  "+concept[0]+" "+concept[1]);
                    }
                }
                res.put(content_name, c_c);
                */
                
                // System.out.println();
            }
            this.releaseStatement(stmt, rs);
            return res;
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            this.releaseStatement(stmt, rs);
            return null;
        }
    }

}
