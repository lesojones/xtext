/*******************************************************************************
 * Copyright (c) 2012 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtext.xbase.typesystem.internal;

import java.util.Collections;
import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.xtext.xbase.XExpression;
import org.eclipse.xtext.xbase.scoping.batch.IFeatureScopeSession;
import org.eclipse.xtext.xbase.typesystem.references.LightweightTypeReference;

/**
 * @author Sebastian Zarnekow - Initial contribution and API
 */
@NonNullByDefault
public class RootExpressionTypeComputationState extends ExpressionTypeComputationState {
	protected LightweightTypeReference expectedType;

	protected RootExpressionTypeComputationState(StackedResolvedTypes resolvedTypes,
			IFeatureScopeSession featureScopeSession,
			AbstractTypeComputationState parent, 
			XExpression expression, 
			@Nullable LightweightTypeReference expectedType) {
		super(resolvedTypes, featureScopeSession, parent, expression);
		this.expectedType = expectedType;
	}

	@Override
	public List<AbstractTypeExpectation> getExpectations(
			AbstractTypeComputationState actualState) {
		AbstractTypeExpectation result = createTypeExpectation(expectedType, actualState, true);
		return Collections.singletonList(result);
	}

	@Override
	protected List<AbstractTypeExpectation> getReturnExpectations(AbstractTypeComputationState actualState, boolean asActualExpectation) {
		AbstractTypeExpectation result = createTypeExpectation(expectedType, actualState, !asActualExpectation);
		return Collections.singletonList(result);
	}
	
	protected AbstractTypeExpectation createTypeExpectation(@Nullable LightweightTypeReference expectedType, AbstractTypeComputationState actualState, boolean voidAllowed) {
		AbstractTypeExpectation result = null;
		if (expectedType != null) {
			LightweightTypeReference copied = expectedType.copyInto(actualState.getReferenceOwner());
			result = new RootTypeExpectation(copied, actualState);
		} else {
			result = new RootNoExpectation(actualState, voidAllowed);
		}
		return result;
	}
	
}