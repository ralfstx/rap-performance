/*******************************************************************************
 * Copyright (c) 2010, 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.performance.phase.h2db;

import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import org.eclipse.rwt.internal.theme.JsonArray;
import org.eclipse.rwt.internal.theme.JsonValue;


public class H2ResultsReader {

  private final H2Connector connector;

  public H2ResultsReader( H2Connector connector ) {
    this.connector = connector;
  }

  public void readResults( String query, PrintWriter writer ) {
    try {
      internalReadResults( query, writer );
    } catch( SQLException e ) {
      throw new RuntimeException( "Failed to read from database", e );
    }
  }

  private void internalReadResults( String query, PrintWriter writer )
    throws SQLException
  {
    Statement statement = connector.getConnection().createStatement();
    try {
      ResultSet resultSet = statement.executeQuery( query );
      writeJsonFromResultSet( resultSet, writer );
    } finally {
      statement.close();
    }
  }

  private void writeJsonFromResultSet( ResultSet resultSet, PrintWriter writer )
    throws SQLException
  {
    writer.write( "{\n" );
    writer.write( "\"headers\": " );
    String string = getHeadersArray( resultSet );
    writer.write( string );
    writer.write( ",\n" );
    writer.write( "\"values\": [" );
    boolean first = true;
    while( resultSet.next() ) {
      if( !first ) {
        writer.write( "," );
      }
      writer.write( "\n" );
      first = false;
      String res = getValuesArray( resultSet );
      writer.write( res );
    }
    if( !first ) {
      writer.write( " " );
    }
    writer.write( "]\n" );
    writer.write( "}\n" );
  }

  private String getHeadersArray( ResultSet resultSet ) throws SQLException {
    ResultSetMetaData metaData = resultSet.getMetaData();
    int colCount = metaData.getColumnCount();
    JsonArray headersArray = new JsonArray();
    for( int col = 1; col <= colCount; col++ ) {
      String name = metaData.getColumnName( col );
      headersArray.append( name );
    }
    return headersArray.toString();
  }

  private String getValuesArray( ResultSet resultSet ) throws SQLException {
    ResultSetMetaData metaData = resultSet.getMetaData();
    int colCount = metaData.getColumnCount();
    JsonArray valuesArray = new JsonArray();
    for( int col = 1; col <= colCount; col++ ) {
      JsonValue value = getJsonValue( resultSet.getObject( col ) );
      valuesArray.append( value );
    }
    return valuesArray.toString();
  }

  private static JsonValue getJsonValue( Object value ) {
    JsonValue result;
    if( value instanceof String ) {
      result = JsonValue.valueOf( ( String )value );
    } else if( value instanceof Integer ) {
      result = JsonValue.valueOf( ( ( Integer )value ).intValue() );
    } else if( value instanceof Long ) {
      result = JsonValue.valueOf( ( ( Long )value ).longValue() );
    } else {
      String type = value.getClass().getName();
      String message = "Unsupported column type: " + type;
      throw new UnsupportedOperationException( message );
    }
    return result;
  }
}
