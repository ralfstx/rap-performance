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

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.regex.Pattern;


public class H2Connector {

  private static final Pattern PATH_PATTERN
    = Pattern.compile( "[a-zA-Z0-9_.-]+" );
  private static final String JDBC_URI = "jdbc:h2:";
  private static final String USERNAME = "";
  private static final String PASSWORD = "";
  private final File dbPath;
  private Connection con;

  public H2Connector( File parent, String name ) {
    if( parent == null ) {
      throw new NullPointerException( "Parameter parent is null" );
    }
    if( !parent.isDirectory() ) {
      String message =   "Database parent directory missing: "
                       + parent.getAbsolutePath();
      throw new IllegalArgumentException( message );
    }
    if( !isLegalDbName( name ) ) {
      throw new IllegalArgumentException( "Invalid database name: " + name );
    }
    dbPath = new File( parent, name );
    try {
      Class.forName( "org.h2.Driver" );
    } catch( ClassNotFoundException e ) {
      throw new RuntimeException( "Failed to load database driver", e );
    }
  }

  public String getName() {
    return dbPath.getName();
  }

  public Connection getConnection() {
    connect();
    return con;
  }

  public void close() {
    if( con != null ) {
      try {
        con.close();
      } catch( SQLException e ) {
        throw new RuntimeException( "Failed to disconnect from database", e );
      }
    }
  }

  private void connect() {
    if( con == null ) {
      try {
        String url = JDBC_URI + dbPath.getAbsolutePath();
        con = DriverManager.getConnection( url, USERNAME, PASSWORD );
      } catch( SQLException e ) {
        throw new RuntimeException( "Failed to connect to database", e );
      }
    }
  }

  static boolean isLegalDbName( String dbPath ) {
    return PATH_PATTERN.matcher( dbPath ).matches();
  }
}
