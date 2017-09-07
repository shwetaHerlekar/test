package com.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Servlet implementation class AddGeo
 */
public class AddGeo extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static Connection conn = null;
	static Statement stmt = null;   
	
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddGeo() {
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
		
		try{
			
			//reading post parameters
			BufferedReader reader = request.getReader();
			StringBuilder buffer = new StringBuilder();
			String line;
			
			while ((line = reader.readLine()) != null) {
		        buffer.append(line);
		    }
		    String data = buffer.toString();
		    JSONParser parser = new JSONParser();
		    JSONObject obj = (JSONObject) parser.parse(data);
		    
		    String country= obj.get("country").toString();
		    String state= obj.get("state").toString();
		    String law_desc= obj.get("law_description").toString();
		    String sub_topic= obj.get("subtopic").toString();
		    
		    
		    response.setContentType("application/json");
			PrintWriter out =response.getWriter();
			
			//inserting data
			conn = createDBConnection();
			stmt= conn.createStatement();
			
			insertLawDesc(law_desc, country, state, sub_topic);
			
			JSONObject result = new JSONObject();
			result.put("status_code", "201");
			result.put("message", "created");
			
			out.println(result);
			
			stmt.close();
			conn.close();
		}
		catch(Exception e){
			
		}
	}
	
	public Connection createDBConnection() throws ClassNotFoundException, SQLException{
		Class.forName("com.mysql.jdbc.Driver");
		return DriverManager.getConnection("jdbc:mysql://localhost:3031/eyChatDB","root","");
	}
	
	public static int getstateId(String state) throws SQLException, ClassNotFoundException{
		//out.println(state);
		ResultSet rs = stmt.executeQuery("select state_id from State where state_name='"+state+"';");
		int id=-1;
		while(rs.next()){
	        
	         id  = rs.getInt("state_id");
	         rs.close();
	         return id;
	      }
		return id;
	}
	
	public static int getSubTopicId(String subtopic) throws SQLException, ClassNotFoundException{
		
		ResultSet rs = stmt.executeQuery("select sub_topic_id from SubTopics where sub_topic_name='"+subtopic+"';");
		int id=-1;
		while(rs.next()){
	         
	         id  = rs.getInt("sub_topic_id");
	         rs.close();
	         
	         return id;
	      }
		return id;
	}

	public static int getcountryId(String country) throws SQLException, ClassNotFoundException{
		//out.println(state);
		ResultSet rs = stmt.executeQuery("select country_id from Country where country_name='"+country+"';");
		int id=-1;
		while(rs.next()){
	        
	         id  = rs.getInt("country_id");
	         rs.close();
	         return id;
	      }
		return id;
	} 
	
	public static void insertLawDesc(String law_desc, String country,String state, String sub_topic) throws SQLException, ClassNotFoundException {
		
				int id = getcountryId(country);
				int id1 = getstateId(state);
				int id2 = getSubTopicId(sub_topic);
				int t = stmt.executeUpdate("insert into Law_Description(law_description,state_id,country_id,sub_topic_id) Values('"+law_desc+"','"+id1+"','"+id+"','"+id2+"')");		
	 }
}
