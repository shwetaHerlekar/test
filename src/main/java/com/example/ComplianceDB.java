package com.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Servlet implementation class ComplianceDB
 */
public class ComplianceDB extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static Connection conn = null;
	static Statement stmt = null;  
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ComplianceDB() {
        super();
        // TODO Auto-generated constructor stub
    }
    

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
	}

	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		try {
			
			//read post request parameters
			BufferedReader reader = request.getReader();
			StringBuilder buffer = new StringBuilder();
			String line;
			
			while ((line = reader.readLine()) != null) {
		        buffer.append(line);
		    }
		    String data = buffer.toString();
		    JSONParser parser = new JSONParser();
		    JSONObject obj = (JSONObject) parser.parse(data);
		    
		    int pageno = Integer.parseInt(obj.get("pageno").toString());
		    int size = Integer.parseInt(obj.get("size").toString());
		    
			response.setContentType("application/json");
			PrintWriter out =response.getWriter();
			//out.println(pageno);
			//out.println(size);
			
			//database connections
			conn = createDBConnection();
			stmt = conn.createStatement();
			String topic,sub_topic;
			int cnt;
			
			String sql = "select "+
					"(select topic_name from topics where topics.topic_id=q.topic_id) as topic, "+
					"(select sub_topic_name from subtopics where subtopics.sub_topic_id=q.sub_topic_id) as sub_topic, "+
					"count(*) as QuestionsCnt "+
					"from QuestionsMgnt q "+
					"group by q.topic_id, q.sub_topic_id limit "+(pageno*size-size)+","+(pageno*size)+";";
			
			ResultSet rs = stmt.executeQuery(sql);
			JSONObject temp,temp1;
			JSONArray arr=null,result=new JSONArray();
			
			ArrayList<String> topics = new ArrayList<String>();
			boolean gotTopic= false;
			
			while(rs.next()){
		         
				if(!topics.contains(rs.getString("topic").trim())){
					topics.add(rs.getString("topic").trim());
					temp = new JSONObject();
					arr = new JSONArray();
					temp.put("topic", rs.getString("topic").trim());
					temp.put("array", arr);
					result.add(temp);
				}
				
				temp1=new JSONObject();
				temp1.put("subtopic", rs.getString("sub_topic").trim());
				temp1.put("QuestionsCount", rs.getInt("QuestionsCnt"));
				arr.add(temp1);
				
		      }
			out.print(result);
			rs.close();
			stmt.close();
			conn.close();
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Connection createDBConnection() throws ClassNotFoundException, SQLException{
		Class.forName("com.mysql.jdbc.Driver");
		return DriverManager.getConnection("jdbc:mysql://localhost:3031/eyChatDB","root","");
	}

}
