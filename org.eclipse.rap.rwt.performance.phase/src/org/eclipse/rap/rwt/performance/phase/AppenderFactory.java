/*******************************************************************************
 * Copyright (c) 2010, 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.performance.phase;


public final class AppenderFactory {

  private static final Appender NULL_APPENDER = new NullAppender();

  private static Appender appender = NULL_APPENDER;

  public static Appender getAppender() {
    synchronized( AppenderFactory.class ) {
      return appender;
    }
  }

  public static void setAppender( Appender appender ) {
    synchronized( AppenderFactory.class ) {
      AppenderFactory.appender = appender != null ? appender : NULL_APPENDER;
    }
  }
}
