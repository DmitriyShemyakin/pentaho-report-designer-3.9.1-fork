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

public class InvoiceReportData extends JdbcTableModel {

	private static final long serialVersionUID = 1L;

	protected String getQuery() {
		return "SELECT CUSTOMERS.CUSTOMERNAME , CUSTOMERS.ADDRESSLINE1 , CUSTOMERS.ADDRESSLINE2 , CUSTOMERS.CITY , CUSTOMERS.STATE , CUSTOMERS.POSTALCODE , CUSTOMERS.COUNTRY , CUSTOMERS.SALESREPEMPLOYEENUMBER , CUSTOMERS.CONTACTFIRSTNAME , CUSTOMERS.CONTACTLASTNAME , ORDERS.STATUS , ORDERS.CUSTOMERNUMBER , ORDERS.ORDERNUMBER , ORDERS.ORDERDATE , ORDERDETAILS.ORDERLINENUMBER , PRODUCTS.PRODUCTNAME , ORDERDETAILS.QUANTITYORDERED , ORDERDETAILS.PRODUCTCODE , ORDERDETAILS.PRICEEACH , PRODUCTS.QUANTITYINSTOCK , (ORDERDETAILS.QUANTITYORDERED*ORDERDETAILS.PRICEEACH)SOLD_PRICE FROM ORDERS , ORDERDETAILS , PRODUCTS , CUSTOMERS WHERE (ORDERS.ORDERNUMBER = ORDERDETAILS.ORDERNUMBER  AND PRODUCTS.PRODUCTCODE = ORDERDETAILS.PRODUCTCODE  AND ORDERS.CUSTOMERNUMBER = CUSTOMERS.CUSTOMERNUMBER)  AND ORDERS.CUSTOMERNUMBER  = 333 ORDER BY CUSTOMERS.CUSTOMERNAME ASC, ORDERS.ORDERNUMBER ASC, ORDERDETAILS.ORDERLINENUMBER ASC"; //$NON-NLS-1$
    
	}
}
