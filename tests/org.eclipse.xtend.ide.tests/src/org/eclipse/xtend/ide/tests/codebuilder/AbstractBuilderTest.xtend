package org.eclipse.xtend.ide.tests.codebuilder

import com.google.inject.Inject
import org.eclipse.xtend.core.jvmmodel.IXtendJvmAssociations
import org.eclipse.xtend.core.xtend.XtendClass
import org.eclipse.xtend.ide.codebuilder.ICodeBuilder
import org.eclipse.xtend.ide.tests.AbstractXtendUITestCase
import org.eclipse.xtend.ide.tests.WorkbenchTestHelper
import org.eclipse.xtext.common.types.JvmDeclaredType
import org.eclipse.xtext.common.types.util.TypeReferences
import org.eclipse.xtext.xbase.compiler.StringBuilderBasedAppendable

import static org.eclipse.xtext.junit4.ui.util.IResourcesSetupUtil.*
import org.eclipse.xtext.common.types.JvmType
import org.eclipse.xtext.xbase.typesystem.legacy.StandardTypeReferenceOwner
import org.eclipse.xtext.xbase.typesystem.util.CommonTypeComputationServices
import org.eclipse.xtext.xbase.typesystem.references.ParameterizedTypeReference
import org.eclipse.emf.ecore.EObject
import org.eclipse.xtext.xbase.typesystem.references.LightweightTypeReference

class AbstractBuilderTest extends AbstractXtendUITestCase {
	
	protected static val DEFAULT_BODY = 'throw new UnsupportedOperationException("TODO: auto-generated method stub")'
	
	@Inject extension WorkbenchTestHelper
	
	@Inject extension IXtendJvmAssociations  
	
	@Inject extension TypeReferences
	
	@Inject CommonTypeComputationServices services
	
	JvmDeclaredType xtendClass

	JvmDeclaredType javaClass
	
	def protected getXtendClass() {
		if(xtendClass == null) {
			xtendClass = (xtendFile('Foo', '''
				class Foo {
				}
			''').xtendTypes.head as XtendClass).inferredType
		}
		xtendClass
	}
	
	def protected getJavaClass() {
		if(javaClass == null) {	
			createFile('Bar.java', '''
				public class Bar {
				}
			''')
			waitForAutoBuild
			javaClass = getTypeForName('Bar', getXtendClass).type as JvmDeclaredType
		}
		javaClass
	}
	
	def protected LightweightTypeReference createTypeRef(JvmType type) {
		new ParameterizedTypeReference(new StandardTypeReferenceOwner(services, type), type)
	}
	
	def protected LightweightTypeReference createTypeRef(Class<?> clazz, EObject context) {
		new ParameterizedTypeReference(new StandardTypeReferenceOwner(services, context), clazz.findDeclaredType(context))
	}
	
	def protected assertBuilds(ICodeBuilder builder, String expectedCode) {
		assertTrue(builder.valid)
		val appendable = new StringBuilderBasedAppendable
		builder.build(appendable)
		assertEquals(expectedCode, appendable.toString)
	}
}