/*******************************************************************************
 * Copyright (c) 2007 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Brock Janiczak - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.jira.core.service.web.rss;

import org.eclipse.mylar.internal.jira.core.service.JiraServer;

/**
 * @author Brock Janiczak
 */
public class RssJiraFilterConverterFactory {
	private static final RssFilterConverter filterConverter = new RssFilterConverter();

	private static final RssFilterConverter jira33FilterConverter = new Jira33RssFilterConverter();

	public static RssFilterConverter getConverter(JiraServer server) {
		if (server.getServerInfo().getVersion().compareTo("3.3") >= 0) { //$NON-NLS-1$
			return jira33FilterConverter;
		} else {
			return filterConverter;
		}
	}
}