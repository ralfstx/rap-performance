package org.eclipse.rap.rwt.performance.phase;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.rap.rwt.performance.phase.h2db.H2Appender;
import org.eclipse.rap.rwt.performance.phase.h2db.H2Connector;
import org.eclipse.rap.rwt.performance.phase.h2db.H2ConnectorFactory;
import org.eclipse.rap.rwt.performance.phase.h2db.MeasurementIndex;
import org.eclipse.rap.rwt.performance.phase.h2db.MeasurementIndex.MeasurementRun;


public final class MeasurementManager {

  private final Object lock = new Object();
  private H2Connector connector;
  private H2Appender appender;
  
  MeasurementManager() {
    // package internal instantiation only
  }

  public void start() {
    synchronized( lock ) {
      if( connector == null ) {
        String dbName = createDbName();
        connector = H2ConnectorFactory.getConnector( dbName );
        appender = new H2Appender( connector );
        appender.initialize( 66000 );
        AppenderFactory.setAppender( appender );
      }
    }
  }

  public void stop() {
    synchronized( lock ) {
      if( connector != null ) {
        try {
          AppenderFactory.setAppender( null );
          appender.finish();
          MeasurementRun summary = appender.getSummary();
          addRun( summary );
        } finally {
          appender = null;
          connector.close();
          connector = null;
        }
      }
    }
  }

  public boolean isActive() {
    return connector != null;
  }

  private void addRun( MeasurementRun summary ) {
    MeasurementIndex runsTable = new MeasurementIndex();
    try {
      runsTable.initialize();
      runsTable.addRun( summary );
    } finally {
      runsTable.close();
    }
  }

  private static String createDbName() {
    SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd-HH-mm-ss" );
    return format.format( new Date() );
  }
}
