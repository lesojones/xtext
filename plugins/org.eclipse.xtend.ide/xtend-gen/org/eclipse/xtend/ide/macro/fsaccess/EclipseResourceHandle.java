package org.eclipse.xtend.ide.macro.fsaccess;

import com.google.common.base.Objects;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.URI;
import org.eclipse.xtend.lib.macro.services.ResourceHandle;
import org.eclipse.xtext.parser.IEncodingProvider;

@SuppressWarnings("all")
public abstract class EclipseResourceHandle implements ResourceHandle {
  protected final IPath path;
  
  protected final IProject project;
  
  protected final IEncodingProvider encodingProvider;
  
  public EclipseResourceHandle(final IProject project, final IPath path, final IEncodingProvider encodingProvider) {
    this.project = project;
    this.path = path;
    this.encodingProvider = encodingProvider;
  }
  
  public boolean exists() {
    boolean _exists = this.project.exists(this.path);
    return _exists;
  }
  
  public String getName() {
    IPath _location = this.getLocation();
    String _lastSegment = _location.lastSegment();
    return _lastSegment;
  }
  
  public String getPath() {
    IPath _location = this.getLocation();
    String _string = _location.toString();
    return _string;
  }
  
  public IFile getFile() {
    IFile _file = this.project.getFile(this.path);
    return _file;
  }
  
  public IFolder getFolder() {
    IFolder _folder = this.project.getFolder(this.path);
    return _folder;
  }
  
  public IPath getLocation() {
    IPath _xblockexpression = null;
    {
      IResource resource = this.project.findMember(this.path);
      boolean _equals = Objects.equal(resource, null);
      if (_equals) {
        IResource _member = this.getMember();
        resource = _member;
      }
      IPath _location = resource.getLocation();
      IPath _makeAbsolute = _location.makeAbsolute();
      _xblockexpression = (_makeAbsolute);
    }
    return _xblockexpression;
  }
  
  public abstract IResource getMember();
  
  public String getEncoding() {
    String _path = this.getPath();
    URI _createFileURI = URI.createFileURI(_path);
    String _encoding = this.encodingProvider.getEncoding(_createFileURI);
    return _encoding;
  }
}
