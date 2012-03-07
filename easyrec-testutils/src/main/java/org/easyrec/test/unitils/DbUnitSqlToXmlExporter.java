package org.easyrec.test.unitils;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.xml.FlatXmlWriter;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Created by david IDavidEA.
 * User: dmann
 * Date: 25.02.11
 * Time: 11:42
 * To change this template use File | Settings | File Templates.
 */
public class DbUnitSqlToXmlExporter {

public static void main(String[] args)
  throws Exception
 {
  //Connect to the database

  Class.forName( "com.mysql.jdbc.Driver" );
  Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/easyrec_test", "root", "root");

  IDatabaseConnection connection = new DatabaseConnection( conn );

  QueryDataSet partialDataSet = new QueryDataSet(connection);
  //Specify the SQL to run to retrieve the data
  partialDataSet.addTable("actionarchive1", " SELECT * FROM actionarchive1");

  //Specify the location of the flat file(XML)
  // file is stored in /target folder
  FlatXmlWriter datasetWriter = new FlatXmlWriter(new FileOutputStream("easyrec-testutils/target/temp.xml"));

  //Export the data
     datasetWriter.write( partialDataSet );
 }

}
