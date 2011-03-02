package org.eclipse.rap.rwt.performance.internal.phase;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.performance.phase.Appender;
import org.eclipse.rap.rwt.performance.phase.AppenderFactory;
import org.eclipse.rap.rwt.performance.phase.PhaseRecord;
import org.eclipse.rap.rwt.performance.phase.RequestTracker;
import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.lifecycle.PhaseId;


public class RequestTracker_Test extends TestCase {

  static List<PhaseRecord> log;

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    log = new ArrayList<PhaseRecord>();
    AppenderFactory.setAppender( new Appender() {
      
      @Override
      public void append( final PhaseRecord record ) {
        log.add( record );
      }
    } );
  }

  @Override
  protected void tearDown() throws Exception {
    AppenderFactory.setAppender( null );
    Fixture.tearDown();
  }

  public void testNanoTime() {
    long startTime = System.nanoTime();
    long endTime = System.nanoTime();
    long duration = endTime - startTime;
    assertTrue( duration > 100 );
    assertTrue( duration < 10000 );
  }

  public void testCreate() {
    String sessionId = "session-4711";
    int requestCounter = 2;
    new RequestTracker( sessionId, requestCounter );
    assertEquals( 0, log.size() );
  }

  public void testStartPhaseWithNullParameter() {
    RequestTracker tracker = new RequestTracker( "foo4711", 3 );
    try {
      tracker.endPhase( null );
      fail();
    } catch( NullPointerException e ) {
      // expected
    }
  }

  public void testEndPhaseWithNullParameter() {
    RequestTracker tracker = new RequestTracker( "foo4711", 3 );
    try {
      tracker.endPhase( null );
      fail();
    } catch( NullPointerException e ) {
      // expected
    }
  }

  public void testEndWithoutStart() {
    RequestTracker request = new RequestTracker( "session-4711", 3 );
    try {
      request.endPhase( PhaseId.READ_DATA );
      fail();
    } catch( IllegalStateException e ) {
      // expected
    }
  }

  public void testEndWithWrongPhaseId() {
    RequestTracker request = new RequestTracker( "session-4711", 3 );
    request.startPhase( PhaseId.READ_DATA );
    try {
      request.endPhase( PhaseId.PROCESS_ACTION );
      fail();
    } catch( IllegalStateException e ) {
      // expected
    }
  }

  public void testNormalOperation() {
    RequestTracker request = new RequestTracker( "session-4711", 3 );
    request.startPhase( PhaseId.READ_DATA );
    request.endPhase( PhaseId.READ_DATA );
    request.startPhase( PhaseId.PROCESS_ACTION );
    request.endPhase( PhaseId.PROCESS_ACTION );
    assertEquals( 2, log.size() );
    PhaseRecord firstRecord = log.get( 0 );
    assertEquals( "session-4711", firstRecord.getSessionId() );
    assertEquals( 3, firstRecord.getRequestId() );
    assertEquals( PhaseId.READ_DATA, firstRecord.getPhaseId() );
    assertTrue( firstRecord.getStartTime() > 0 );
    assertTrue( firstRecord.getDuration() > 0 );
  }
}
