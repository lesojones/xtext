/*******************************************************************************
 * Copyright (c) 2012 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtext.xbase.typesystem.computation;

import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.xtext.common.types.JvmConstructor;
import org.eclipse.xtext.xbase.XConstructorCall;
import org.eclipse.xtext.xbase.typesystem.references.LightweightTypeReference;

/**
 * A constructor linking candidate represents an actual or a possible
 * value of a {@link XConstructorCall#getConstructor() constructor call}.
 * 
 * It's a typesafe specialization of the {@link ILinkingCandidate}
 * 
 * @author Sebastian Zarnekow - Initial contribution and API
 */
@NonNullByDefault
public interface IConstructorLinkingCandidate extends ILinkingCandidate {

	XConstructorCall getConstructorCall();
	 
	@Nullable
	JvmConstructor getConstructor();

	/**
	 * The resolved type arguments. If the constructor itself defines type parameters,
	 * their resolved representation is appended to the list of type arguments.
	 * Consider the following type:
	 * 
	 * <pre>
	 * class C&lt;T, V&gt; {
	 *   public <P> C(P p) {}
	 * }
	 * </pre>
	 * 
	 * An invocation of the constructor {@code C} will return three type arguments, the bound
	 * values of for {@code T}, {@code V} and {@code P}.
	 */
	List<LightweightTypeReference> getTypeArguments();
}
