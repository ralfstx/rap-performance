package org.eclipse.rap.rwt.performance.phase;

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
    int phase = getPhaseId( phaseId );
    PhaseRecord record
      = new PhaseRecord( sessionId, requestId, phase, startTime, duration );
    AppenderFactory.getAppender().append( record );
  }

  private static int getPhaseId( PhaseId phaseId ) {
    int result;
    if( phaseId == PhaseId.PREPARE_UI_ROOT ) {
      result = 0;
    } else if( phaseId == PhaseId.READ_DATA ) {
      result = 1;
    } else if( phaseId == PhaseId.PROCESS_ACTION ) {
      result = 2;
    } else if( phaseId == PhaseId.RENDER ) {
      result = 3;
    } else {
      throw new IllegalArgumentException( "Illegal phase " + phaseId );
    }
    return result;
  }
}
