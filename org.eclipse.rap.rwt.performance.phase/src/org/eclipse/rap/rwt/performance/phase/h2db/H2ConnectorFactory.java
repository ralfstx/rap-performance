package org.eclipse.rap.rwt.performance.phase.h2db;

import java.io.File;

public final class H2ConnectorFactory {

  static final String DB_PATH_PROPERTY = "org.eclipse.rap.performance.dbpath";

  private H2ConnectorFactory() {
    // prevent instantiation
  }

  public static H2Connector getConnector( String dbName ) {
    File dbParentDir = getDbParentDir();
    ensureParentDir( dbParentDir );
    checkDbName( dbName );
    return new H2Connector( dbParentDir, dbName );
  }

  private static File getDbParentDir() {
    String dbParentPath = System.getProperty( DB_PATH_PROPERTY );
    if( dbParentPath == null ) {
      String message = "Must set database path system property: "
                       + DB_PATH_PROPERTY;
      throw new IllegalArgumentException( message );
    }
    return new File( dbParentPath );
  }

  private static void ensureParentDir( File dbParentDir ) {
    if( !dbParentDir.isDirectory() && !dbParentDir.mkdirs() ) {
      String message = "Failed to create database directory: "
        + dbParentDir.getAbsolutePath();
      throw new IllegalArgumentException( message );
    }
  }

  private static void checkDbName( String dbName ) {
    if( !H2Connector.isLegalDbName( dbName ) ) {
      throw new IllegalArgumentException( "Invalid database name: " + dbName );
    }
  }
}
