/*******************************************************************************
 * Copyright (c) 2008 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtext.junit4.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.xtext.ISetup;
import org.eclipse.xtext.junit4.AbstractXtextTests;
import org.eclipse.xtext.junit4.util.ResourceLoadHelper;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.XtextSourceViewer;
import org.eclipse.xtext.ui.editor.XtextSourceViewerConfiguration;
import org.eclipse.xtext.ui.editor.contentassist.ConfigurableCompletionProposal;
import org.eclipse.xtext.ui.editor.contentassist.ContentAssistContext;
import org.eclipse.xtext.ui.editor.contentassist.ReplacementTextApplier;
import org.eclipse.xtext.ui.editor.model.DocumentPartitioner;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.ui.editor.model.XtextDocument;
import org.eclipse.xtext.util.StringInputStream;
import org.eclipse.xtext.util.Strings;
import org.junit.Assert;

import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * Represents a builder for <code>IContentAssistProcessor</code> tests.
 *
 * @author Michael Clay - Initial contribution and API
 * @author Sven Efftinge
 * @author Sebastian Zarnekow
 */
public class ContentAssistProcessorTestBuilder implements Cloneable {

	private String model;
	private int cursorPosition;
	private Injector injector;
	private final ResourceLoadHelper loadHelper;

	public static class Factory {
		private Injector injector;

		@Inject
	    public Factory(Injector injector) {
	    	this.injector = injector;
	    }

	    public ContentAssistProcessorTestBuilder create(ResourceLoadHelper resourceLoadHelper) throws Exception {
	    	return new ContentAssistProcessorTestBuilder(this.injector,resourceLoadHelper);
	    }
	}

	public ContentAssistProcessorTestBuilder(ISetup setupClazz, AbstractXtextTests tests) throws Exception {
		tests.with(setupClazz);
		injector = tests.getInjector();
		this.loadHelper = tests;
	}
	
	public ContentAssistProcessorTestBuilder(Injector injector, ResourceLoadHelper helper) throws Exception {
		this.injector = injector;
		this.loadHelper = helper;
	}

	public ContentAssistProcessorTestBuilder reset() throws Exception {
		return clone("",0);
	}

	public ContentAssistProcessorTestBuilder append(String model) throws Exception {
		return clone(getModel()+model, cursorPosition+model.length());
	}

	public ContentAssistProcessorTestBuilder appendNl(String model) throws Exception {
		return append(model).append(Strings.newLine());
	}

	public ContentAssistProcessorTestBuilder insert(String model, int cursorPosition) throws Exception {
		StringBuilder builder = new StringBuilder(getModel()).insert(cursorPosition, model);
		return clone(builder.toString(), cursorPosition + model.length());
	}

	public ContentAssistProcessorTestBuilder cursorBack(int times) throws Exception {
		return clone(model, this.cursorPosition -= times);
	}
	
	public ContentAssistProcessorTestBuilder applyProposal() throws Exception {
		return applyProposal(cursorPosition);
	}
	
	public ContentAssistProcessorTestBuilder applyProposal(String proposal) throws Exception {
		return applyProposal(cursorPosition, proposal);
	}

	public ContentAssistProcessorTestBuilder applyProposal(int position) throws Exception {
		return applyProposal(position, null);
	}
	
	public ContentAssistProcessorTestBuilder applyProposal(int position, String proposalString) throws Exception {
		ICompletionProposal[] proposals = computeCompletionProposals(getModel(), position);
		ICompletionProposal proposal = proposals[0];
		if (proposalString != null) {
			for(ICompletionProposal candidate: proposals) {
				if (proposalString.equals(getProposedText(candidate))) {
					proposal = candidate;
					break;
				}
			}
		}
		return applyProposal(proposal);
	}

	protected ContentAssistProcessorTestBuilder applyProposal(ICompletionProposal proposal) throws Exception {
		final XtextResource xtextResource = loadHelper.getResourceFor(new StringInputStream(Strings.emptyIfNull(model)));
		IXtextDocument document = getDocument(xtextResource, model);
		proposal.apply(document);
		ContentAssistProcessorTestBuilder reset = reset();
		return reset.append(document.get());
	}

	public ContentAssistProcessorTestBuilder expectContent(String expectation){
		Assert.assertEquals(expectation, getModel());
		return this;
	}

	public ContentAssistProcessorTestBuilder assertCount(int completionProposalCount) throws Exception {
		return assertCountAtCursorPosition(completionProposalCount, this.cursorPosition);
	}

	public ContentAssistProcessorTestBuilder assertText(String... expectedText) throws Exception {
		return assertTextAtCursorPosition(this.cursorPosition, expectedText);
	}
	
	public ProposalTester assertProposal(String expectedText) throws Exception {
		String currentModelToParse = getModel();

		ICompletionProposal[] proposals = computeCompletionProposals(currentModelToParse,
				cursorPosition);

		if (proposals == null)
			proposals = new ICompletionProposal[0];
		for(ICompletionProposal proposal: proposals) {
			if (expectedText.equals(toString(proposal))) {
				return new ProposalTester(proposal);
			}
		}
		Assert.fail("No such proposal: " + expectedText + " Found: " + toString(proposals));
		return null;
	}

	public ContentAssistProcessorTestBuilder assertTextAtCursorPosition(
			String cursorPosition, String... expectedText) throws Exception {
		return assertTextAtCursorPosition(getModel().indexOf(cursorPosition), expectedText);
	}
	
	public ContentAssistProcessorTestBuilder assertTextAtCursorPosition(
			String cursorPosition, int offset, String... expectedText) throws Exception {
		return assertTextAtCursorPosition(getModel().indexOf(cursorPosition) + offset, expectedText);
	}
	
	public ContentAssistProcessorTestBuilder assertTextAtCursorPosition(int cursorPosition, String... expectedText)
			throws Exception {

		String currentModelToParse = getModel();

		ICompletionProposal[] computeCompletionProposals = computeCompletionProposals(currentModelToParse,
				cursorPosition);

		if (computeCompletionProposals == null)
			computeCompletionProposals = new ICompletionProposal[0];

		Arrays.sort(expectedText);
		final String expectation = Strings.concat(", ", Arrays.asList(expectedText));
		final String actual = Strings.concat(", ", toString(computeCompletionProposals));
		
		Assert.assertEquals(expectation, actual);
		
		for (int i = 0; i < computeCompletionProposals.length; i++) {
			ICompletionProposal completionProposal = computeCompletionProposals[i];
			String proposedText = getProposedText(completionProposal);
			Assert.assertTrue("Missing proposal '" + proposedText + "'. Expect completionProposal text '" + expectation + "', but got " +
					actual,
					Arrays.asList(expectedText).contains(proposedText));
		}

		return this;
	}

	protected String getProposedText(ICompletionProposal completionProposal) {
		String proposedText = completionProposal.getDisplayString();
		if (completionProposal instanceof ConfigurableCompletionProposal) {
			ConfigurableCompletionProposal configurableProposal = (ConfigurableCompletionProposal) completionProposal;
			proposedText = configurableProposal.getReplacementString();
			if (configurableProposal.getTextApplier() instanceof ReplacementTextApplier) {
				proposedText = ((ReplacementTextApplier) configurableProposal.getTextApplier()).getActualReplacementString(configurableProposal);
			}
		}
		return proposedText;
	}

	public ContentAssistProcessorTestBuilder assertMatchString(String matchString)
			throws Exception {
		String currentModelToParse = getModel();
		final XtextResource xtextResource = loadHelper.getResourceFor(new StringInputStream(currentModelToParse));
		final IXtextDocument xtextDocument = getDocument(xtextResource, currentModelToParse);
		XtextSourceViewerConfiguration configuration = get(XtextSourceViewerConfiguration.class);
		Shell shell = new Shell();
		try {
			ISourceViewer sourceViewer = getSourceViewer(shell, xtextDocument, configuration);
			IContentAssistant contentAssistant = configuration.getContentAssistant(sourceViewer);
			String contentType = xtextDocument.getContentType(currentModelToParse.length());
			if (contentAssistant.getContentAssistProcessor(contentType) != null) {
				ContentAssistContext.Factory factory = get(ContentAssistContext.Factory.class);
				ContentAssistContext[] contexts = factory.create(sourceViewer, currentModelToParse.length(), xtextResource);
				for(ContentAssistContext context: contexts) {
					Assert.assertTrue("matchString = '" + matchString + "', actual: '" + context.getPrefix() + "'",
							"".equals(context.getPrefix()) || matchString.equals(context.getPrefix()));
				}
			} else {
				Assert.fail("No content assistant for content type " + contentType);
			}
			return this;
		} finally {
			shell.dispose();
		}
	}

	public ContentAssistProcessorTestBuilder assertCursorIsAfter(String text) {
		Assert.assertTrue("cursor should be after '" + text + "' but it's after " + model.substring(0, getCursorPosition()), 
				model.substring(getCursorPosition() - text.length()).startsWith(text));
		return this;
	}
	
	public ContentAssistProcessorTestBuilder assertCursorIsBefore(String text) {
		Assert.assertTrue("cursor should be before '" + text + "' but it's before " + model.substring(getCursorPosition()), 
				model.substring(getCursorPosition()).startsWith(text));
		return this;
	}
	
	protected String getModel() {
		return this.model == null ? "":model;
	}

	public List<String> toString(ICompletionProposal[] proposals) {
		if (proposals == null)
			return Collections.emptyList();
		List<String> res = new ArrayList<String>(proposals.length);
		for (ICompletionProposal proposal : proposals) {
			String proposedText = toString(proposal);
			res.add(proposedText);
		}
		Collections.sort(res);
		return res;
	}

	protected String toString(ICompletionProposal proposal) {
		String proposedText = proposal.getDisplayString();
		if (proposal instanceof ConfigurableCompletionProposal) {
			ConfigurableCompletionProposal configurableProposal = (ConfigurableCompletionProposal) proposal;
			proposedText = configurableProposal.getReplacementString();
			if (configurableProposal.getTextApplier() instanceof ReplacementTextApplier)
				proposedText = ((ReplacementTextApplier) configurableProposal.getTextApplier()).getActualReplacementString(configurableProposal);
		}
		return proposedText;
	}

	public ContentAssistProcessorTestBuilder assertCountAtCursorPosition(int completionProposalCount, int cursorPosition)
			throws Exception {

		String currentModelToParse = getModel();

		ICompletionProposal[] computeCompletionProposals = computeCompletionProposals(currentModelToParse,
				cursorPosition);

		StringBuffer computedProposals = new StringBuffer();
		for (int i = 0; i < computeCompletionProposals.length; i++) {
			computedProposals.append(computeCompletionProposals[i].getDisplayString());
			if (i<(computeCompletionProposals.length-1)) {
				computedProposals.append(",");
			}
		}
		Assert.assertEquals("expect only " + completionProposalCount + " CompletionProposal item for model '"
				+ currentModelToParse + "' but got '"+computedProposals+"'", completionProposalCount, computeCompletionProposals.length);

		return this;
	}

	public ICompletionProposal[] computeCompletionProposals(final String currentModelToParse, int cursorPosition)
			throws Exception {
		final XtextResource xtextResource = loadHelper.getResourceFor(new StringInputStream(currentModelToParse));
		final IXtextDocument xtextDocument = getDocument(xtextResource, currentModelToParse);
		
		XtextSourceViewerConfiguration configuration = get(XtextSourceViewerConfiguration.class);
		Shell shell = new Shell();
		try {
			ISourceViewer sourceViewer = getSourceViewer(shell, xtextDocument, configuration);
			IContentAssistant contentAssistant = configuration.getContentAssistant(sourceViewer);
			String contentType = xtextDocument.getContentType(cursorPosition);
			IContentAssistProcessor processor = contentAssistant.getContentAssistProcessor(contentType);
			if (processor != null) {
				return processor.computeCompletionProposals(sourceViewer, cursorPosition);
			}
			return new ICompletionProposal[0];
		} finally {
			shell.dispose();
		}
	}

	protected ISourceViewer getSourceViewer(Shell shell, final IXtextDocument xtextDocument,
			XtextSourceViewerConfiguration configuration) {
		XtextSourceViewer.Factory factory = get(XtextSourceViewer.Factory.class);
		ISourceViewer sourceViewer = factory.createSourceViewer(shell, null, null, false, 0);
		sourceViewer.configure(configuration);
		sourceViewer.setDocument(xtextDocument);
		return sourceViewer;
	}

	public ICompletionProposal[] computeCompletionProposals(int cursorPosition) throws Exception {
		return computeCompletionProposals(getModel(), cursorPosition);
	}
	
	public ICompletionProposal[] computeCompletionProposals(String cursorPosition) throws Exception {
		return computeCompletionProposals(getModel(), getModel().indexOf(cursorPosition));
	}
	
	public ICompletionProposal[] computeCompletionProposals() throws Exception {
		return computeCompletionProposals(getModel(), cursorPosition);
	}

	@Override
	public String toString() {
		return getModel() + "\n length: " + getModel().length() + "\n cursor at: "
				+ this.cursorPosition;
	}

	public IXtextDocument getDocument(final XtextResource xtextResource, final String model) {
		XtextDocument document = get(XtextDocument.class);
		document.set(model);
		document.setInput(xtextResource);
		DocumentPartitioner partitioner = get(DocumentPartitioner.class);
		partitioner.connect(document);
		document.setDocumentPartitioner(partitioner);
		return document;
	}

	public ITextViewer getSourceViewer(final String currentModelToParse, final IXtextDocument xtextDocument) {
		ITextViewer result = new MockableTextViewer() {
			@Override
			public IDocument getDocument() {
				return xtextDocument;
			}
			
			@Override
			public ISelectionProvider getSelectionProvider() {
				return new MockableSelectionProvider() {
					@Override
					public ISelection getSelection() {
						return TextSelection.emptySelection();
					}
				};
			}
			
			@Override
			public StyledText getTextWidget() {
				return null;
			}
		};
		return result;
	}

	protected ContentAssistProcessorTestBuilder clone(String model, int offset) throws Exception {
		ContentAssistProcessorTestBuilder builder = (ContentAssistProcessorTestBuilder) clone();
		builder.model = model;
		builder.cursorPosition = offset;
		return builder;
	}

	public <T> T get(Class<T> clazz) {
		return injector.getInstance(clazz);
	}

	protected int getCursorPosition() {
		return cursorPosition;
	}
	
	public class ProposalTester {
		
		private ICompletionProposal proposal;

		protected ProposalTester(ICompletionProposal proposal) {
			this.proposal = proposal;
		}
		
		public ProposalTester withDisplayString(String displayString) {
			Assert.assertEquals("displayString", displayString, proposal.getDisplayString());
			return this;
		}
		
		public ContentAssistProcessorTestBuilder apply() throws Exception {
			return ContentAssistProcessorTestBuilder.this.applyProposal(proposal);
		}
		
	}
}
