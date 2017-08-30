package com.example;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

public class DBInsert {

	@SuppressWarnings("deprecation")
	static String JDBC_DRIVER = "com.mysql.jdbc.GoogleDriver";  
	static String DB_URL = "";
	static String USER = "root";
	static String PASS = "root";
	static Connection conn = null;
	static Statement stmt = null;   
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Running in class");
		try {
			
			Class.forName(JDBC_DRIVER);
			DB_URL = System.getProperty("ae-cloudsql.cloudsql-database-url");

			System.out.println("Connecting to a selected database...");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			
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
                Row currentRow = iterator.next();
                Iterator<Cell> cellIterator = currentRow.iterator();

                while (cellIterator.hasNext()) {
                	
                    Cell currentCell = cellIterator.next();
                    //getCellTypeEnum shown as deprecated for version 3.15
                    //getCellTypeEnum ill be renamed to getCellType starting from version 4.0
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
               firstRow = false;
               //System.out.println(cRow[0]);
               insertTopic(conn,cRow[0]);
            }
            
            /*for (int i = 0; i < headers.length; i++) {
				System.out.println(headers[i]);
			}
            System.out.println("colsize : "+headers.length);*/
			
		} catch (Exception e) {
			System.out.println("exception!!");
			e.printStackTrace();
		}
	}
	
	public static void insertTopic(Connection conn, String topic) throws SQLException {
		stmt = conn.createStatement();
		int t = stmt.executeUpdate("insert into Topics(topic_name) Values('"+topic+"')");
	}

}
