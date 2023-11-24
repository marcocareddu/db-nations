package org.java.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/db_nations";
        String username = "root";
        String password = "root";
        String sql = "SELECT countries.name AS Country, country_id AS ID, regions.name AS Region, continents.name As Continent "
                + "FROM countries " + "JOIN regions " + "ON countries.region_id = regions.region_id "
                + "JOIN continents " + "ON regions.continent_id = continents.continent_id ";

        ArrayList<String[]> languages = new ArrayList<>();
        int year = 0;
        Long population = null;
        Long gdp = null;
        String language = "";
        
        try (Scanner in = new Scanner(System.in);
             Connection con = DriverManager.getConnection(url, username, password)) {

            System.out.println("Vuoi filtrare il risultato? ");
            String wantFilter = in.nextLine();

            if (wantFilter.equals("si")) {
                System.out.println("Inserisci il filtro: ");
                String filterInput = in.nextLine();
                sql += "WHERE countries.name LIKE '%" + filterInput + "%'";
            }

            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                ArrayList<String[]> tableResults = new ArrayList<>();
                tableResults.add(new String[]{"Country", "ID", "Region", "Continent"});
                tableResults.add(new String[]{"", "", "", ""});

                while (rs.next()) {
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

                System.out.println("Vuoi selezionare un Id? ");
                String wantId = in.nextLine();
                if (wantId.equals("si")) {
                    System.out.println("Inserisci l'id: ");
                    String filterId = in.nextLine();

                    String sqlSecondQuery = "SELECT languages.language AS Lingua, ("
                            + "SELECT MAX(year) "
                            + "FROM country_stats "
                            + "WHERE country_id = " + filterId + ") AS Anno_Più_Recente, "
                            + "(SELECT population "
                            + "FROM country_stats "
                            + "WHERE country_id = " + filterId + " "
                            + "ORDER BY year DESC LIMIT 1) AS Popolazione, "
                            + "(SELECT gdp "
                            + "FROM country_stats "
                            + "WHERE country_id = " + filterId + " "
                            + "ORDER BY year DESC "
                            + "LIMIT 1) AS GDP "
                            + "FROM languages "
                            + "JOIN country_languages "
                            + "ON languages.language_id = country_languages.language_id "
                            + "WHERE country_languages.country_id = " + filterId;

                    try (PreparedStatement ps1 = con.prepareStatement(sqlSecondQuery);
                         ResultSet rs1 = ps1.executeQuery()) {

                        while (rs1.next()) {
                            
                        	language = rs1.getString("Lingua");
                            year = rs1.getInt("Anno_Più_Recente");
                            population = rs1.getLong("Popolazione");
                            gdp = rs1.getLong("GDP");
                            languages.add(new String[]{language});
                        }

                        System.out.print("Lingua: ");
                        for (String[] array : languages) {
                            for (String str : array) {
                                System.out.print(str + " ");
                            }
                        }
                        
                        System.out.println("\nYear: " + year + "\n"
                                + "Population: " + population + "\n"
                                + "GDP: " + gdp + "\n");
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}