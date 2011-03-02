package org.eclipse.rap.rwt.performance.phase.h2db;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.performance.phase.PhaseRecord;
import org.eclipse.rwt.lifecycle.PhaseId;


public class H2Appender_Test extends TestCase {

  private static final String DB_NAME = "test";
  private H2Connector connector;
  private File tmpDir;

  @Override
  protected void setUp() throws Exception {
    tmpDir = new File( System.getProperty( "java.io.tmpdir" ) );
    connector = new H2Connector( tmpDir, DB_NAME );
  }

  @Override
  protected void tearDown() throws Exception {
    connector.close();
    File[] files = tmpDir.listFiles();
    for( int i = 0; i < files.length; i++ ) {
      File file = files[ i ];
      String name = file.getName();
      if( name.startsWith( DB_NAME + "." ) && name.endsWith( ".db" ) ) {
        file.delete();
      }
    }
  }

  public void testAppend() throws SQLException {
    H2Appender appender = new H2Appender( connector );
    appender.initialize( 10 );
    PhaseId phaseId = PhaseId.RENDER;
    PhaseRecord record = new PhaseRecord( "session23", 23, phaseId, 4711, 42 );
    appender.append( record );
    appender.finish();
    Statement statement = connector.getConnection().createStatement();
    ResultSet resultSet = statement.executeQuery( "SELECT * FROM phases" );
    resultSet.next();
    assertEquals( record.getSessionId(), resultSet.getString( "SESSION" ) );
    assertEquals( record.getRequestId(), resultSet.getInt( "REQUEST" ) );
    int expected = H2Appender.translatePhase( phaseId );
    assertEquals( expected, resultSet.getInt( "PHASE" ) );
    assertEquals( record.getStartTime(), resultSet.getInt( "TIME" ) );
    assertEquals( record.getDuration(), resultSet.getInt( "DURATION" ) );
    assertFalse( resultSet.next() );
  }

  public void testAppendTooOften() {
    H2Appender appender = new H2Appender( connector );
    appender.initialize( 2 );
    PhaseId render = PhaseId.RENDER;
    appender.append( new PhaseRecord( "session23", 1, render, 4711, 42 ) );
    appender.append( new PhaseRecord( "session23", 2, render, 4711, 42 ) );
    try {
      appender.append( new PhaseRecord( "session23", 3, render, 4711, 42 ) );
      fail();
    } catch( IllegalStateException e ) {
      // expected
    }
  }

  
}
