package jwt.util.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class DatabaseService {

	public UUID fetchUserID(String username) {
		String url = PropertiesHandler.getProperty("url");
		String user = PropertiesHandler.getProperty("username");
		String password = PropertiesHandler.getProperty("password");

		String query = "SELECT id from users where username = '" + username + "';";

		UUID userId = null;

		try (Connection conn = DriverManager.getConnection(url, user, password);
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
}
