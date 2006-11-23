/*******************************************************************************
 * Copyright (c) 2006 - 2006 Mylar eclipse.org project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Mylar project committers - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.jira.core;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.mylar.context.core.MylarStatusHandler;
import org.eclipse.mylar.internal.jira.core.ui.JiraUiPlugin;
import org.eclipse.mylar.tasks.core.ITaskRepositoryListener;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.tigris.jira.core.JiraCorePlugin;
import org.tigris.jira.core.ServerManager;
import org.tigris.jira.core.service.JiraServer;
import org.tigris.jira.core.service.exceptions.AuthenticationException;
import org.tigris.jira.core.service.exceptions.ServiceUnavailableException;

/**
 * This class acts as a layer of indirection between clients in this project and
 * the server API implemented by the Jira Dashboard, and also abstracts some
 * Mylar implementation details. It initializes a jiraServer object and serves
 * as the central location to get a reference to it.
 * 
 * @author Mik Kersten
 * @author Wesley Coelho (initial integration patch)
 */
public class JiraServerFacade implements ITaskRepositoryListener {

	private ServerManager serverManager = null;

	private static JiraServerFacade instance = null;

	public JiraServerFacade() {
		TasksUiPlugin.getRepositoryManager().addListener(this);
		serverManager = JiraCorePlugin.getDefault().getServerManager();
	}

	/**
	 * Lazily creates server.
	 */
	public JiraServer getJiraServer(TaskRepository repository) {
		try {
			String serverHostname = getServerHost(repository);
			JiraServer server = serverManager.getServer(serverHostname);
			if (server == null) {
				server = serverManager.createServer(serverHostname, repository.getUrl(), false,
						repository.getUserName(), repository.getPassword());
				serverManager.addServer(server);
			}
			return server;
		} catch (ServiceUnavailableException sue) {
			throw sue;
		} catch (RuntimeException e) {
			MylarStatusHandler.log("Error connecting to Jira Server", this);
			throw e;
		}
	}

	public static JiraServerFacade getDefault() {
		if (instance == null) {
			instance = new JiraServerFacade();
		}
		return instance;
	}

	public void logOutFromAll() {
		try {
			JiraServer[] allServers = serverManager.getAllServers();
			for (int i = 0; i < allServers.length; i++) {
				allServers[i].logout();
			}
		} catch (Exception e) {
			// ignore
		}
	}

	public void repositoriesRead() {
		// ignore
	}

	public void repositoryAdded(TaskRepository repository) {
		if (repository.getKind().equals(JiraUiPlugin.REPOSITORY_KIND)) {
			getJiraServer(repository);
		}
	}

	public void repositoryRemoved(TaskRepository repository) {
		if (repository.getKind().equals(JiraUiPlugin.REPOSITORY_KIND)) {
			String serverHostname = getServerHost(repository);
			JiraServer server = serverManager.getServer(serverHostname);
			removeServer(server);
		}
	}
	
	public void repositorySettingsChanged(TaskRepository repository) {
		repositoryRemoved(repository);
		repositoryAdded(repository);
	}
	
	public void refreshServerSettings(TaskRepository repository) {
		String serverHostname = getServerHost(repository);
		JiraServer server = serverManager.getServer(serverHostname);
		if (server != null) {
			server.refreshDetails();
		}
	}
	
	private void removeServer(JiraServer server) {
		if (server != null) {
			server.logout();
			serverManager.removeServer(server);
		}
	} 

	/**
	 * Validate the server URL and user credentials
	 * @param serverUrl Location of the Jira Server
	 * @param user Username
	 * @param password Password
	 * @return String describing validation failure or null if the details are valid
	 */
	public String validateServerAndCredentials(String serverUrl, String user, String password) {
		try {
			serverManager.testConnection(serverUrl, user, password);
			return null;
		} catch (ServiceUnavailableException e) {
			return e.getMessage();
		} catch (AuthenticationException e) {
			return "The supplied credentials are invalid";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	private static String getServerHost(TaskRepository repository) {
		try {
			return new URL(repository.getUrl()).getHost();
		} catch (MalformedURLException ex) {
			throw new RuntimeException("Invalid url "+repository.getUrl(), ex);
		}
	}
	
	/**
	 * TODO: refactor
	 */
	public static void handleConnectionException(Exception e) {
		if (e instanceof ServiceUnavailableException) {
			MylarStatusHandler.fail(e, "Jira Connection Failure.\n\n"
					+ "Please check your network connection and Jira server settings in the Task Repositories view.",
					true);
		} else if (e instanceof AuthenticationException) {
			MylarStatusHandler.fail(e, "Jira Authentication Failed.\n\n"
					+ "Please check your Jira username and password in the Task Repositories view", true);
		} else if (e instanceof RuntimeException) {
			MylarStatusHandler.fail(e, "No Jira repository found.\n\n"
					+ "Please verify that a vaild Jira repository exists in the Task Repositories view", true);
		} else {
			MylarStatusHandler.fail(e, "Could not connect to Jira repository.\n\n"
					+ "Please check your credentials in the Task Repositories view", true);
		}
	}

	
}