package org.eclipse.rap.rwt.performance.phase;


public class StdOutAppender implements Appender {

  public void append( final PhaseRecord record ) {
    System.out.print( record.getSessionId() );
    System.out.print( " " );
    System.out.print( record.getRequestId() );
    System.out.print( " " );
    System.out.print( record.getPhaseId() );
    System.out.print( " " );
    System.out.print( record.getDuration() );
    System.out.println();
  }
}
