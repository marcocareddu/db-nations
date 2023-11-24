package org.java.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) throws SQLException {
		

            String url = "jdbc:mysql://localhost:3306/db_nations";
            String username = "root";
            String password = "root";
            String sql = "SELECT countries.name AS Country, country_id AS ID, regions.name AS Region, continents.name As Continent "
            		+ "FROM countries "
            		+ "JOIN regions "
            		+ "ON countries.region_id = regions.region_id "
            		+ "JOIN continents "
            		+ "ON regions.continent_id = continents.continent_id ";
            
            Connection con = null;
            
            Scanner in = new Scanner(System.in);
            System.out.println("Vuoi cercare per id? ");
            String wantId = in.nextLine();
            
            if(wantId.equals("si"))
                System.out.println("Inserisci l'id: ");
                String filterInput = in.nextLine();
                sql += "WHERE countries.name LIKE " + "'%" + filterInput + "%'";
            try {
            	con = DriverManager.getConnection(url, username, password);
            	
            	try(PreparedStatement ps = con.prepareStatement(sql)){
            		try(ResultSet rs = ps.executeQuery()) {
            			
            			ArrayList<String[]> tableResults = new ArrayList<>();
            			tableResults.add(new String[]{"Country", "ID", "Region", "Continent"});
            			tableResults.add(new String[]{"", "", "", ""});
            			
            			while(rs.next()) {
            				String country = rs.getString(1);
            				int idInt = rs.getInt(2);
            				String region = rs.getString(3);
            				String continent = rs.getString(4);
            				String id = String.valueOf(idInt);
            				
                			tableResults.add(new String[]{country, id, region, continent});
                	        }
            	        for (String[] row : tableResults) {
            	            System.out.printf("%-50s%-10s%-30s%-20s%n", row[0], row[1], row[2], row[3]);
            			}
            		}
            	}
            } catch (SQLException ex) {
            	ex.printStackTrace();
            } finally {
            	if( con != null) {
            		con.close();
            	}
            }
	}
}
