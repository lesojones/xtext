/*******************************************************************************
 * Copyright (c) 2010 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtext.parser.unorderedGroups;

/**
 * @author Sebastian Zarnekow - Initial contribution and API
 */
public class BacktrackingBug325745ParserTest extends AbstractBacktrackingBug325745ParserTest {
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		with(SimpleBacktrackingBug325745TestLanguageStandaloneSetup.class);
	}

}
