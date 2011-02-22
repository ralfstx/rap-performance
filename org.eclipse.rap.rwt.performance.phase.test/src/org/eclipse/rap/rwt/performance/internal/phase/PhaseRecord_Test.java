package org.eclipse.rap.rwt.performance.internal.phase;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.performance.phase.PhaseRecord;
import org.eclipse.rwt.lifecycle.PhaseId;


public class PhaseRecord_Test extends TestCase {

  public void testCreate() {
    String sessionId = "session4711";
    int requestCounter = 2;
    PhaseId phaseId = PhaseId.READ_DATA;
    long startTime = 2300;
    long duration = 4200;
    PhaseRecord record = new PhaseRecord( sessionId,
                                          requestCounter,
                                          phaseId,
                                          startTime,
                                          duration );
    assertEquals( sessionId, record.getSessionId() );
    assertEquals( requestCounter, record.getRequestId() );
    assertEquals( phaseId, record.getPhaseId() );
    assertEquals( startTime, record.getStartTime() );
    assertEquals( duration, record.getDuration() );
  }
}
