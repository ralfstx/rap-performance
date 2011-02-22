package org.eclipse.rap.rwt.performance.phase.h2db;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public final class H2TestUtil {

  private static final String EXAMPLE_DB = "resources/example.h2.db";

  public H2TestUtil() {
    // prevent instantiation
  }

  public static File fakeExampleDb() throws IOException {
    File tempFile = File.createTempFile( "example-", ".h2.db" );
    tempFile.deleteOnExit();
    ClassLoader classLoader = H2TestUtil.class.getClassLoader();
    InputStream inputStream = classLoader.getResourceAsStream( EXAMPLE_DB );
    try {
      copy( inputStream, tempFile );
    } finally {
      inputStream.close();
    }
    return tempFile;
  }

  public static String getDbName( File dbFile ) {
    String dbPath = dbFile.getName();
    String suffix = ".h2.db";
    if( dbPath.endsWith( suffix ) ) {
      dbPath = dbPath.substring( 0, dbPath.length() - suffix.length() );
    }
    return dbPath;
  }

  private static void copy( final InputStream inputStream, final File outputFile )
    throws IOException
  {
    FileOutputStream outputStream = new FileOutputStream( outputFile );
    byte[] buffer = new byte[ 4096 ];
    try {
      int read;
      while( ( read = inputStream.read( buffer ) ) != -1 ) {
        outputStream.write( buffer, 0, read );
      }
    } finally {
      outputStream.close();
    }
  }
}
