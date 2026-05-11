/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2009 Pentaho Corporation.  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.testcases.samplereport;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

public abstract class JdbcTableModel extends AbstractTableModel {

	protected Object[][] reportData;

	protected int columnCount = 0;

	protected String[] columnNames = null;

	protected int[] columnTypes = null;

	public JdbcTableModel() {
		reportData = getReportData();
	}

	protected abstract String getQuery();

	public Class getColumnClass(int columnIndex) {
		return (reportData[0][columnIndex - 1]).getClass();
	}

	public String getColumnName(int column) {
		String columnName = (column >= 0 && column < columnNames.length ? columnNames[column] : null);
		return columnName;
	}

	public int getColumnCount() {
		int columnCount = (reportData != null && reportData.length > 0 ? reportData[0].length : 0);
		return columnCount;
	}

	public int getRowCount() {
		int rowCount = (reportData != null ? reportData.length : 0);
		return rowCount;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		Object result = reportData[rowIndex][columnIndex];
		return result;
	}

	protected Object[][] getReportData() {
		Object[][] results = null;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String query = getQuery();
		try {
			conn = getConnection();
			stmt = conn.createStatement();
			System.err.println("About to execute query: " + query); //$NON-NLS-1$
			rs = stmt.executeQuery(query);
			saveMetaData(rs);
			results = parseResultSet(rs);
		} catch (SQLException sqle) {
			System.err.println("SQLException trying to retrieve data: " + sqle); //$NON-NLS-1$
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			close(rs);
			close(stmt);
			close(conn);
		}
		return results;
	}

	protected void saveMetaData(ResultSet rs) throws SQLException {
		ResultSetMetaData metadata = rs.getMetaData();
		columnCount = metadata.getColumnCount();
		columnNames = new String[columnCount];
		columnTypes = new int[columnCount];
		for (int col = 1; col <= columnCount; ++col) {
			columnNames[col - 1] = metadata.getColumnName(col);
			columnTypes[col - 1] = metadata.getColumnType(col);
		}
	}

	protected Object[][] parseResultSet(ResultSet rs) throws SQLException {
		Object[][] results = null;
		ArrayList data = new ArrayList();
		if (rs != null) {
			while (rs.next()) {
				Object[] rowData = new Object[columnCount];
				for (int col = 1; col <= columnCount; ++col) {
					rowData[col - 1] = getObject(col, columnTypes[col - 1], rs);
				}
				data.add(rowData);
			}
			results = (Object[][]) data.toArray(new Object[data.size()][]);
		}
		return results;
	}

	protected Object getObject(int column, int columnType, ResultSet rs) throws SQLException {
		switch (columnType) {
		case Types.BIGINT:
		case Types.INTEGER:
		case Types.SMALLINT:
		case Types.TINYINT:
			return new Integer(rs.getInt(column));
		case Types.DECIMAL:
		case Types.DOUBLE:
		case Types.FLOAT:
		case Types.NUMERIC:
			return new Double(rs.getDouble(column));
		default:
			return rs.getString(column);
		}
	}

	protected void close(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException sqle) {
				System.err.println("SQLException closing connection: " + sqle); //$NON-NLS-1$
			}
		}
	}

	protected void close(Statement stmt) {
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException sqle) {
				System.err.println("SQLException closing statement: " + sqle); //$NON-NLS-1$
			}
		}
	}

	protected void close(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException sqle) {
				System.err.println("SQLException closing result set: " + sqle); //$NON-NLS-1$
			}
		}
	}

	protected Connection getConnection() throws SQLException, ClassNotFoundException {
		Class.forName(getDriverClassname());
		String url = getUrl();
		String user = getUsername();
		String password = getPassword();
		if (user == null) {
			return DriverManager.getConnection(url);
		} else {
			return DriverManager.getConnection(url, user, password);
		}
	}

	protected String getDriverClassname() {
		return HYPERSONIC_DRIVER_CLASSNAME;
	}

	protected String getUrl() {
		return PENTAHO_SAMPLEDATA_URL;
	}

	protected String getUsername() {
		return PENTAHO_SAMPLEDATA_USERNAME;
	}

	protected String getPassword() {
		return PENTAHO_SAMPLEDATA_PASSWORD;
	}

	protected static final String HYPERSONIC_DRIVER_CLASSNAME = "org.hsqldb.jdbcDriver"; //$NON-NLS-1$

	protected static final String PENTAHO_SAMPLEDATA_URL = "jdbc:hsqldb:hsql://localhost:9001/sampledata"; //$NON-NLS-1$

	protected static final String PENTAHO_SAMPLEDATA_USERNAME = "pentaho_user"; //$NON-NLS-1$ 

	protected static final String PENTAHO_SAMPLEDATA_PASSWORD = "password"; //$NON-NLS-1$

	public String toString(boolean formatted) {
		if (!formatted)
			return toString();
    StringBuilder sb = new StringBuilder();
		sb.append("columnCount: " + columnCount + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
		for (int col = 0; col < columnCount; ++col) {
			sb.append("\t").append(columnNames[col]); //$NON-NLS-1$
		}
		sb.append("\n"); //$NON-NLS-1$
		for (int row = 0; row < getRowCount(); ++row) { 
			sb.append("["+row+"]"); //$NON-NLS-1$ //$NON-NLS-2$
			for (int col = 0; col < columnCount; ++col) {
				sb.append("\t").append(reportData[row][col]); //$NON-NLS-1$
			}
			sb.append("\n"); //$NON-NLS-1$
		}
		return sb.toString();
	}
}
