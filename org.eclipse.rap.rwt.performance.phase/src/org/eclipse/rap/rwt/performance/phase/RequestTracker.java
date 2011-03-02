package org.eclipse.rap.rwt.performance.phase;

import org.eclipse.rwt.internal.lifecycle.RWTRequestVersionControl;
import org.eclipse.rwt.lifecycle.PhaseId;


public class RequestTracker {

  private final String sessionId;
  private final int requestId;
  private PhaseId currentPhaseId;
  private long startTime;

  public RequestTracker( String sessionId, int requestId ) {
    this.sessionId = sessionId;
    this.requestId = requestId;
  }

  public void startPhase( PhaseId phaseId ) {
    if( phaseId == null ) {
      throw new NullPointerException( "phaseId" );
    }
    if( currentPhaseId != null ) {
      throw new IllegalStateException( "In phase " + currentPhaseId );
    }
    currentPhaseId = phaseId;
    startTime = System.nanoTime();
  }

  public void endPhase( PhaseId phaseId ) {
    if( phaseId == null ) {
      throw new NullPointerException( "phaseId" );
    }
    if( currentPhaseId != phaseId ) {
      throw new IllegalStateException( "In phase " + currentPhaseId );
    }
    currentPhaseId = null;
    long endTime = System.nanoTime();
    long duration = endTime - startTime;
    boolean valid = RWTRequestVersionControl.isValid();
    if( !valid ) {
      throw new IllegalStateException( "grrr" );
    }
    PhaseRecord record
      = new PhaseRecord( sessionId, requestId, phaseId, startTime, duration );
    AppenderFactory.getAppender().append( record );
  }
}
