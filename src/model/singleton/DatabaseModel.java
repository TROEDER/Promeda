package model.singleton;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseModel {

	private String db_host;
	private String db_port;
	private String db_user;
	private String db_pass;
	private String db_name;

	private static final String DRIVER = "com.mysql.jdbc.Driver";
	private Connection connection = null;
	private Statement statement = null;
	private PreparedStatement prepStatement;

	private ResultSet resultSet;
	private ArrayList<String[]> result;

	/**
	 * Constructor
	 *
	 * @param db_host
	 * @param db_port
	 * @param db_user
	 * @param db_pass
	 * @param db_name
	 * @throws java.sql.SQLException
	 */
	public DatabaseModel(String db_host, String db_port, String db_user, String db_pass, String db_name)
			throws SQLException {
		this.db_host = db_host;
		this.db_port = db_port;
		this.db_user = db_user;
		this.db_pass = db_pass;
		this.db_name = db_name;

		try {
			createConnection();
			prepStatement = connection.prepareStatement(
					"INSERT INTO boilerplate_data (storeview_id, boilerplate_uri, boilerplate_value) VALUES (?, ?, ?);");
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(DatabaseModel.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * Creates db.connection
	 *
	 * @throws SQLException
	 *             Wenn keine Verbindung zu einer Datenbank hergestellt werden
	 *             konnte.
	 * @throws ClassNotFoundException
	 *             Wenn der Datenbanktreiber nicht gefunden werden konnte.
	 */
	private void createConnection() throws SQLException, ClassNotFoundException {
		// Treiber initialisieren
		Class.forName(DRIVER);
		// Uri für die Verbindung zu der Datenbank
		String mySqlUrl = "jdbc:mysql://" + db_host + ":" + db_port + "/" + db_name;
		// Verbindung herstellen.
		connection = DriverManager.getConnection(mySqlUrl, db_user, db_pass);
	}

	public void createStatement() {
		try {
			if (connection == null || connection.isClosed()) {
				createConnection();
			}
			if (statement == null || statement.isClosed()) {
				statement = connection.createStatement();
			}
		} catch (SQLException ex) {
			Logger.getLogger(DatabaseModel.class.getName()).log(Level.SEVERE, null, ex);
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(DatabaseModel.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * Use this for executing SQL queries with SELECT statement.
	 *
	 * @param query
	 *            an query of SQL statements
	 * @return ResultSet
	 */
	public ResultSet selectQuery(String query) {
		try {
			createStatement();
			resultSet = statement.executeQuery(query);
		} catch (SQLException ex) {
			Logger.getLogger(DatabaseModel.class.getName()).log(Level.SEVERE, null, ex);
		}
		return resultSet;
	}

	public ArrayList<String[]> selectQueryArrayList(String query) {
		try {
			createStatement();
			resultSet = statement.executeQuery(query);
			if (resultSet != null || resultSet.getRow() != 0) {
				result = new ArrayList<String[]>();
				int columns = resultSet.getMetaData().getColumnCount();
				// Transform ResultSet --> ArrayList
				while (resultSet.next()) {
					String[] str = new String[columns];
					for (int k = 1; k <= columns; k++) {
						str[k - 1] = resultSet.getString(k);
					}
					result.add(str);
				}
			}
			resultSet.close();
			statement.close();
		} catch (SQLException ex) {
			Logger.getLogger(DatabaseModel.class.getName()).log(Level.SEVERE, null, ex);
		}

		return result;
	}

	/**
	 * Use this for executing SQL queries with INSERT, UPDATE or DELETE statements.
	 * Returns true if data manipulation was successfull.
	 *
	 * @param query
	 *            an query of SQL statements
	 * @return boolean
	 */
	public boolean updateQuery(String query) {
		boolean queryResult = true;
		try {
			createStatement();
			queryResult = statement.executeUpdate(query) == 1;
			statement.close();
		} catch (SQLException ex) {
			Logger.getLogger(DatabaseModel.class.getName()).log(Level.SEVERE, null, ex);
		}
		return queryResult;
	}

	public boolean insertQuery(String query) {
		boolean queryResult = true;
		try {
			createStatement();
			queryResult = statement.executeUpdate(query) == 1;
			statement.close();
		} catch (SQLException ex) {
			Logger.getLogger(DatabaseModel.class.getName()).log(Level.SEVERE, null, ex);
		}
		return queryResult;
	}

	/**
	 *
	 * @param storeviewId
	 */
	public void deleteBoilerplates(String storeviewId) {
		try {
			prepStatement = connection.prepareStatement("DELETE FROM boilerplate_data WHERE storeview_id = ?");
			prepStatement.setString(1, storeviewId);
			prepStatement.executeUpdate();
		} catch (SQLException ex) {
			Logger.getLogger(DatabaseModel.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 *
	 * @param boilerplateId
	 */
	public void deleteBoilerplate(String boilerplateId) {
		try {
			prepStatement = connection.prepareStatement("DELETE FROM boilerplate_data WHERE boilerplate_id = ?");
			prepStatement.setString(1, boilerplateId);
			prepStatement.executeUpdate();
		} catch (SQLException ex) {
			Logger.getLogger(DatabaseModel.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 *
	 * @param storeviewId
	 * @param boilerplateUri
	 * @param boilerplateValue
	 */
	public void insertBoilerplate(String storeviewId, String boilerplateUri, String boilerplateValue) {
		try {
			prepStatement = connection.prepareStatement(
					"INSERT INTO boilerplate_data (storeview_id, boilerplate_uri, boilerplate_value) VALUES (?, ?, ?)");
			prepStatement.setString(1, storeviewId);
			prepStatement.setString(2, boilerplateUri);
			prepStatement.setString(3, boilerplateValue);
			prepStatement.executeUpdate();
		} catch (SQLException ex) {
			Logger.getLogger(DatabaseModel.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 *
	 * @param searchQuery
	 * @return
	 */
	public ArrayList<String[]> selectGroupArticlesAsArrayList(String searchQuery) {

		try {
			createStatement();
			prepStatement = connection.prepareStatement(
					"SELECT parent_id FROM pro_product_relation WHERE parent_id LIKE ? OR child_id LIKE ? GROUP BY parent_id");
			prepStatement.setString(1, searchQuery);
			prepStatement.setString(2, searchQuery);
			resultSet = prepStatement.executeQuery();

			if (resultSet != null || resultSet.getRow() != 0) {
				result = new ArrayList<String[]>();
				int columns = resultSet.getMetaData().getColumnCount();
				// Transform ResultSet --> ArrayList
				while (resultSet.next()) {
					String[] str = new String[columns];
					for (int k = 1; k <= columns; k++) {
						str[k - 1] = resultSet.getString(k);
					}
					result.add(str);
				}
			}
			resultSet.close();
			statement.close();
		} catch (SQLException ex) {
			Logger.getLogger(DatabaseModel.class.getName()).log(Level.SEVERE, null, ex);
		}

		return result;
	}

	/**
	 *
	 * @param searchQuery
	 * @return
	 */
	public ResultSet selectGroupArticlesAsResultSet(String searchQuery) {

		try {
			createStatement();
			prepStatement = connection.prepareStatement(
					"SELECT parent_id FROM pro_product_relation WHERE parent_id LIKE ? OR child_id LIKE ? GROUP BY parent_id");
			prepStatement.setString(1, searchQuery);
			prepStatement.setString(2, searchQuery);
			resultSet = prepStatement.executeQuery();
		} catch (SQLException ex) {
			Logger.getLogger(DatabaseModel.class.getName()).log(Level.SEVERE, null, ex);
		}
		return resultSet;
	}

	public ArrayList<String[]> selectChildArticlesAsArrayList(String parent_id) {

		try {
			createStatement();
			prepStatement = connection.prepareStatement(
					"SELECT child_id FROM pro_product_relation WHERE parent_id LIKE ? ORDER BY child_id ASC");
			prepStatement.setString(1, parent_id);
			resultSet = prepStatement.executeQuery();

			if (resultSet != null || resultSet.getRow() != 0) {
				result = new ArrayList<String[]>();
				int columns = resultSet.getMetaData().getColumnCount();
				// Transform ResultSet --> ArrayList
				while (resultSet.next()) {
					String[] str = new String[columns];
					for (int k = 1; k <= columns; k++) {
						str[k - 1] = resultSet.getString(k);
					}
					result.add(str);
				}
			}
			resultSet.close();
			statement.close();
		} catch (SQLException ex) {
			Logger.getLogger(DatabaseModel.class.getName()).log(Level.SEVERE, null, ex);
		}

		return result;
	}

	/**
	 *
	 * @param parent_id
	 * @return
	 */
	public ResultSet selectChildArticlesAsResultSet(String parent_id) {

		try {
			createStatement();
			prepStatement = connection.prepareStatement(
					"SELECT child_id FROM pro_product_relation WHERE parent_id LIKE ? ORDER BY child_id ASC");
			prepStatement.setString(1, parent_id);
			resultSet = prepStatement.executeQuery();
		} catch (SQLException ex) {
			Logger.getLogger(DatabaseModel.class.getName()).log(Level.SEVERE, null, ex);
		}
		return resultSet;
	}

	/**
	 * closing a db-connection
	 *
	 * @throws SQLException
	 */
	public void closeConnection() throws SQLException {
		connection.close();
	}

}
