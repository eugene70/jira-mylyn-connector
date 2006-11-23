/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.internal.jira.core;

import org.eclipse.mylar.tasks.core.AbstractAttributeFactory;

/**
 * @author Mik Kersten
 */
public class JiraAttributeFactory extends AbstractAttributeFactory {

	private static final long serialVersionUID = 8000933300692372211L;

	public static final String ATTRIBUTE_TYPE = "attribute.jira.type";
	
	public static final String ATTRIBUTE_ISSUE_KEY = "attribute.jira.issue_key";

	public static final String ATTRIBUTE_ENVIRONMENT = "attribute.jira.environment";
	public static final String ATTRIBUTE_COMPONENTS = "attribute.jira.components";
	public static final String ATTRIBUTE_FIXVERSIONS = "attribute.jira.fixversions";
	public static final String ATTRIBUTE_AFFECTSVERSIONS = "attribute.jira.affectsversions";
	public static final String ATTRIBUTE_ESTIMATE = "attribute.jira.estimate";
	
	@Override
	public boolean getIsHidden(String key) {
		return false;
	}

	@Override
	public String getName(String key) {
		return key;
	}

	@Override
	public boolean isReadOnly(String key) {
		return true;
	}

	@Override
	public String mapCommonAttributeKey(String key) {
		return key;
	}

}