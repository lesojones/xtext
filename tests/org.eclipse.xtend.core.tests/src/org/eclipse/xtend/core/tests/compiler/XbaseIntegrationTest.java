/*******************************************************************************
 * Copyright (c) 2011 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtend.core.tests.compiler;

import org.eclipse.xtend.core.tests.RuntimeInjectorProvider;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.eclipse.xtext.xbase.junit.evaluation.AbstractXbaseEvaluationTest;
import org.junit.Before;
import org.junit.runner.RunWith;

import com.google.inject.Inject;

/**
 * @author Sven Efftinge - Initial contribution and API
 */
@RunWith(XtextRunner.class)
@InjectWith(RuntimeInjectorProvider.class)
public class XbaseIntegrationTest extends AbstractXbaseEvaluationTest {

	@Inject
	private CompilerTestHelper testHelper;
	
	@Before
	public void setUp() throws Exception {
		testHelper.setUp();
	}
	
	@Override
	protected void assertEvaluatesTo(Object object, String string) {
		testHelper.assertEvaluatesTo(object, string);
	}
	
	@Override
	protected void assertEvaluatesToArray(Object[] object, String string) {
		testHelper.assertEvaluatesToArray(object, string);
	}
	
	@Override
	protected void assertEvaluatesWithException(Class<? extends Throwable> class1, String string) {
		testHelper.assertEvaluatesWithException(class1, string);
	}

}
