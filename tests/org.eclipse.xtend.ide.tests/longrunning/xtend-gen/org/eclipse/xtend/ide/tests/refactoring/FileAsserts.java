package org.eclipse.xtend.ide.tests.refactoring;

import com.google.inject.Inject;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.xtend.ide.tests.WorkbenchTestHelper;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import org.junit.Assert;

@SuppressWarnings("all")
public class FileAsserts {
  @Inject
  @Extension
  private WorkbenchTestHelper _workbenchTestHelper;
  
  public IFile assertFileExists(final String fileName) throws Exception {
    IFile _xblockexpression = null;
    {
      IProject _project = this._workbenchTestHelper.getProject();
      final IResource file = _project.findMember(fileName);
      Assert.assertTrue((file instanceof IFile));
      boolean _exists = file.exists();
      Assert.assertTrue(_exists);
      _xblockexpression = (((IFile) file));
    }
    return _xblockexpression;
  }
  
  public void assertFileContains(final IFile file, final String... expectedContents) throws Exception {
    file.refreshLocal(IResource.DEPTH_ZERO, null);
    final String fileContents = this._workbenchTestHelper.getContents(file);
    final Procedure1<String> _function = new Procedure1<String>() {
        public void apply(final String expectation) {
          StringConcatenation _builder = new StringConcatenation();
          _builder.append("Substring \'");
          _builder.append(expectation, "");
          _builder.append("\' not found in \'");
          _builder.append(fileContents, "");
          _builder.append("\' ");
          boolean _contains = fileContents.contains(expectation);
          Assert.assertTrue(_builder.toString(), _contains);
        }
      };
    IterableExtensions.<String>forEach(((Iterable<String>)Conversions.doWrapArray(expectedContents)), _function);
  }
}
