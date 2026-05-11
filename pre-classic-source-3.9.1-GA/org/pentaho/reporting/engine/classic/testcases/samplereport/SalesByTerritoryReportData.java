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
 * Copyright (c) 2001 - 2009 Object Refinery Ltd, Pentaho Corporation and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.testcases.samplereport;

public class SalesByTerritoryReportData extends JdbcTableModel {

	private static final long serialVersionUID = 1L;

	protected String getQuery() {
		return "SELECT OFFICES.TERRITORY AS TERRITORY, ORDERS.ORDERNUMBER AS ORDERNUMBER , ORDERS.ORDERDATE AS ORDERDATE, ORDERS.STATUS AS STATUS, ORDERDETAILS.ORDERLINENUMBER AS ORDERLINENUMBER, ORDERDETAILS.QUANTITYORDERED AS QUANTITYORDERED, ORDERDETAILS.PRICEEACH AS PRICEEACH, ORDERDETAILS.PRODUCTCODE AS PRODUCTCODE FROM ORDERS INNER JOIN ORDERDETAILS ON ORDERS.ORDERNUMBER = ORDERDETAILS.ORDERNUMBER INNER JOIN PRODUCTS ON ORDERDETAILS.PRODUCTCODE =PRODUCTS.PRODUCTCODE   INNER JOIN CUSTOMERS ON ORDERS.CUSTOMERNUMBER =CUSTOMERS.CUSTOMERNUMBER  INNER JOIN EMPLOYEES ON CUSTOMERS.SALESREPEMPLOYEENUMBER = EMPLOYEES.EMPLOYEENUMBER INNER JOIN OFFICES ON EMPLOYEES.OFFICECODE=OFFICES.OFFICECODE WHERE ORDERS.STATUS NOT IN('Shipped') ORDER BY OFFICES.TERRITORY DESC"; //$NON-NLS-1$
	}
}
