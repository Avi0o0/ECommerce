package jwt.util.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class DatabaseService {

	private static final String URL = PropertiesHandler.getProperty("url");
	private static final String USER = PropertiesHandler.getProperty("username");
	private static final String PASSWORD = PropertiesHandler.getProperty("password");

	public UUID fetchUserID(String username) {
		String query = "SELECT id from users where username = '" + username + "';";

		UUID userId = null;

		try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(query)) {

			while (rs.next()) {
				userId = (UUID) rs.getObject("id");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return userId;
	}
	
	public boolean isTokenBlacklisted(String token) {
	    String query = "SELECT 1 FROM blacklist_tokens WHERE token = ? LIMIT 1";

	    try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
	         PreparedStatement ps = conn.prepareStatement(query)) {

	        ps.setString(1, token);

	        try (ResultSet rs = ps.executeQuery()) {
	            return rs.next(); 
	        }

	    } catch (SQLException ex) {
	        ex.printStackTrace();
	        return false; 
	    }
	}

}
