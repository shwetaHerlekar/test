package com.example;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.io.IOException;
import java.util.logging.Logger;

public class DBInsert {

	@SuppressWarnings("deprecation")
	static Connection conn = null;
	static Statement stmt = null;   
	static int law_id= 0;
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Running in class");
		try {
			
			conn = createDBConnection();
			stmt = conn.createStatement();
			
			String path = DBInsert.class.getResource("/sample_data.xlsx").getPath();
			FileInputStream excelFile = new FileInputStream(new File(path));
            Workbook workbook = new XSSFWorkbook(excelFile);
            Sheet datatypeSheet = workbook.getSheetAt(0);
            Iterator<Row> iterator = datatypeSheet.iterator();
            
            String[] headers = new String[55];
            String[] cRow = new String[55];
            boolean firstRow = true ;
            int index = 0;
  
           
            while (iterator.hasNext()) {
            	
            	index = 0;
            	law_id = 0;
                Row currentRow = iterator.next();
                Iterator<Cell> cellIterator = currentRow.iterator();
                //out.println("inside main while");

                while (cellIterator.hasNext()) {
                	
                	//out.println("inside second while");
                    Cell currentCell = cellIterator.next();
                    
                    if (currentCell.getCellTypeEnum() == CellType.STRING) {
                    	
                        //System.out.print(currentCell.getStringCellValue() + "--");
                        if(firstRow){
                        	headers[index] = currentCell.getStringCellValue();
                        	//System.out.println(headers[index]);
                        	index++;
                        }
                        else{
                        	cRow[index] = currentCell.getStringCellValue();
                        	index++;
                        }
                     
                        
                    } else if (currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                    	
                        //System.out.print(currentCell.getNumericCellValue() + "--");
                        if(firstRow){
                            headers[index] = currentCell.getStringCellValue();
                            index++;
                        }
                        else{
                        	cRow[index] = currentCell.getStringCellValue();
                        	index++;
                        }
                    }
                }
                if(!firstRow){
             	   
                	if(getTopicId(cRow[0])==-1)
                	{
                		//System.out.println(cRow[0]);
                		insertTopic(cRow[0]);
                	}
                	insertSubTopic(cRow[1], cRow[0]);    
                	insertLawDesc(headers, cRow);
         
                }
                else
                {
                	insertState(headers, "US");
                	
                }
               firstRow = false;
            }
			conn.close();
			stmt.close();
			return;
			
		} catch (Exception e) {
			System.out.print("exception!!");
			e.printStackTrace();
		}
	  System.out.println("Good Bye!!");
	}
	
	public static Connection createDBConnection() throws ClassNotFoundException, SQLException{
		Class.forName("com.mysql.jdbc.Driver");
		return DriverManager.getConnection("jdbc:mysql://localhost:3031/eyChatDB","root","");
	}
	
	public static void insertTopic(String topic) throws SQLException, ClassNotFoundException {
		
		int t = stmt.executeUpdate("insert into Topics(topic_name) Values('"+topic+"')");	
	}
	
	public static void insertSubTopic(String subtopic, String topic) throws SQLException, ClassNotFoundException {
		int topic_id = getTopicId(topic);
		int t = stmt.executeUpdate("insert into SubTopics(sub_topic_name,topic_id) Values('"+subtopic+"','"+topic_id+"')");	
	}
	
	public static int getTopicId(String topic) throws SQLException, ClassNotFoundException{
		
		ResultSet rs = stmt.executeQuery("select topic_id from Topics where topic_name='"+topic+"';");
		int id=-1;
		while(rs.next()){
	         
	         id  = rs.getInt("topic_id");
	         rs.close();
	         return id;
	      }
		return id;
	}
	
	public static void insertState(String[] headers, String country) throws SQLException, ClassNotFoundException {
		
		for (int i = 4; i < headers.length; i++) {
			int t1 = 1;
			int t = stmt.executeUpdate("insert into State(state_name,country_id) Values('"+headers[i]+"','"+t1+"')");
		}
	}

	public static void insertLawDesc(String[] headers, String[] curRow) throws SQLException, ClassNotFoundException {
		//System.out.println("inside law desc");
		for (int i = 3; i < curRow.length; i++) {
			
			curRow[i] = curRow[i].replaceAll("\'", "");
			
			if(i==3)
			{
				law_id++;
				int id = getcountryId("US");
				int id1= getSubTopicId(curRow[1]);
				int t = stmt.executeUpdate("insert into Law_Description(law_description,country_id,sub_topic_id) Values('"+curRow[i]+"','"+id+"','"+id1+"')");	
				insertQuestion(curRow[2], curRow[0], curRow[1]);
			}
			else
			{
				int id = 1;
				int id1 = getstateId(headers[i]);
				int id2 = getSubTopicId(curRow[1]);
				int t = stmt.executeUpdate("insert into Law_Description(law_description,state_id,country_id,sub_topic_id) Values('"+curRow[i]+"','"+id1+"','"+id+"','"+id2+"')");		
				//insertQuestion(curRow[2], curRow[0], curRow[1]);
			}
		}
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
	
	public static void insertQuestion(String question, String topic,String subtopic) throws SQLException, ClassNotFoundException {
		question = question.replaceAll("\'", "");
		
		int topic_id = getTopicId(topic);
		int sub_topic_id = getSubTopicId(subtopic);
		int uid = 1;
		String sql = "insert into QuestionsMgnt(possible_questions,questions_type, law_desc_id,User_id,topic_id,sub_topic_id) Values('"+question+"','SYSTEM','"+law_id+"','"+uid+"','"+topic_id+"','"+sub_topic_id+"')";
		
		int t = stmt.executeUpdate(sql);	
	}
}
