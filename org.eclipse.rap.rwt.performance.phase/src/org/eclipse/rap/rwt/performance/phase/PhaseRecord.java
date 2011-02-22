package org.eclipse.rap.rwt.performance.phase;

import org.eclipse.rwt.lifecycle.PhaseId;


public class PhaseRecord {
  private final String sessionId;
  private final int requestId;
  private final PhaseId phaseId;
  private final long startTime;
  private final long duration;

  public PhaseRecord( final String sessionId,
                      final int requestCounter,
                      final PhaseId phaseId,
                      final long startTime,
                      final long duration )
  {
    this.sessionId = sessionId;
    this.requestId = requestCounter;
    this.phaseId = phaseId;
    this.startTime = startTime;
    this.duration = duration;
  }

  public String getSessionId() {
    return sessionId;
  }

  public int getRequestId() {
    return requestId;
  }

  public PhaseId getPhaseId() {
    return phaseId;
  }

  public long getStartTime() {
    return startTime;
  }

  public long getDuration() {
    return duration;
  }
}
