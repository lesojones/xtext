package org.eclipse.xtend.ide.builder;

import com.google.common.base.Objects;
import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.xtend.lib.Data;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.resource.IResourceDescription;
import org.eclipse.xtext.resource.IResourceDescriptions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.util.ToStringHelper;

@Data
@SuppressWarnings("all")
public class FilteringResourceDescriptions implements IResourceDescriptions {
  private final IResourceDescriptions _delegate;
  
  public IResourceDescriptions getDelegate() {
    return this._delegate;
  }
  
  private final IJavaProject _project;
  
  public IJavaProject getProject() {
    return this._project;
  }
  
  public Iterable<IResourceDescription> getAllResourceDescriptions() {
    IResourceDescriptions _delegate = this.getDelegate();
    Iterable<IResourceDescription> _allResourceDescriptions = _delegate.getAllResourceDescriptions();
    final Function1<IResourceDescription,Boolean> _function = new Function1<IResourceDescription,Boolean>() {
        public Boolean apply(final IResourceDescription it) {
          URI _uRI = it.getURI();
          boolean _isContainedUri = FilteringResourceDescriptions.this.isContainedUri(_uRI);
          return Boolean.valueOf(_isContainedUri);
        }
      };
    Iterable<IResourceDescription> _filter = IterableExtensions.<IResourceDescription>filter(_allResourceDescriptions, _function);
    return _filter;
  }
  
  public IResourceDescription getResourceDescription(final URI normalizedURI) {
    boolean _isContainedUri = this.isContainedUri(normalizedURI);
    if (_isContainedUri) {
      IResourceDescriptions _delegate = this.getDelegate();
      return _delegate.getResourceDescription(normalizedURI);
    }
    return null;
  }
  
  private boolean isContainedUri(final URI uri) {
    boolean _or = false;
    boolean _equals = Objects.equal(uri, null);
    if (_equals) {
      _or = true;
    } else {
      int _segmentCount = uri.segmentCount();
      boolean _lessThan = (_segmentCount < 2);
      _or = (_equals || _lessThan);
    }
    if (_or) {
      return false;
    }
    String _segment = uri.segment(1);
    IJavaProject _project = this.getProject();
    IProject _project_1 = _project.getProject();
    String _name = _project_1.getName();
    return Objects.equal(_segment, _name);
  }
  
  public Iterable<IEObjectDescription> getExportedObjects() {
    IResourceDescriptions _delegate = this.getDelegate();
    Iterable<IEObjectDescription> _exportedObjects = _delegate.getExportedObjects();
    final Function1<IEObjectDescription,Boolean> _function = new Function1<IEObjectDescription,Boolean>() {
        public Boolean apply(final IEObjectDescription it) {
          URI _eObjectURI = it.getEObjectURI();
          boolean _isContainedUri = FilteringResourceDescriptions.this.isContainedUri(_eObjectURI);
          return Boolean.valueOf(_isContainedUri);
        }
      };
    Iterable<IEObjectDescription> _filter = IterableExtensions.<IEObjectDescription>filter(_exportedObjects, _function);
    return _filter;
  }
  
  public Iterable<IEObjectDescription> getExportedObjects(final EClass type, final QualifiedName name, final boolean ignoreCase) {
    IResourceDescriptions _delegate = this.getDelegate();
    Iterable<IEObjectDescription> _exportedObjects = _delegate.getExportedObjects(type, name, ignoreCase);
    final Function1<IEObjectDescription,Boolean> _function = new Function1<IEObjectDescription,Boolean>() {
        public Boolean apply(final IEObjectDescription it) {
          URI _eObjectURI = it.getEObjectURI();
          boolean _isContainedUri = FilteringResourceDescriptions.this.isContainedUri(_eObjectURI);
          return Boolean.valueOf(_isContainedUri);
        }
      };
    Iterable<IEObjectDescription> _filter = IterableExtensions.<IEObjectDescription>filter(_exportedObjects, _function);
    return _filter;
  }
  
  public Iterable<IEObjectDescription> getExportedObjectsByObject(final EObject object) {
    IResourceDescriptions _delegate = this.getDelegate();
    Iterable<IEObjectDescription> _exportedObjectsByObject = _delegate.getExportedObjectsByObject(object);
    final Function1<IEObjectDescription,Boolean> _function = new Function1<IEObjectDescription,Boolean>() {
        public Boolean apply(final IEObjectDescription it) {
          URI _eObjectURI = it.getEObjectURI();
          boolean _isContainedUri = FilteringResourceDescriptions.this.isContainedUri(_eObjectURI);
          return Boolean.valueOf(_isContainedUri);
        }
      };
    Iterable<IEObjectDescription> _filter = IterableExtensions.<IEObjectDescription>filter(_exportedObjectsByObject, _function);
    return _filter;
  }
  
  public Iterable<IEObjectDescription> getExportedObjectsByType(final EClass type) {
    IResourceDescriptions _delegate = this.getDelegate();
    Iterable<IEObjectDescription> _exportedObjectsByType = _delegate.getExportedObjectsByType(type);
    final Function1<IEObjectDescription,Boolean> _function = new Function1<IEObjectDescription,Boolean>() {
        public Boolean apply(final IEObjectDescription it) {
          URI _eObjectURI = it.getEObjectURI();
          boolean _isContainedUri = FilteringResourceDescriptions.this.isContainedUri(_eObjectURI);
          return Boolean.valueOf(_isContainedUri);
        }
      };
    Iterable<IEObjectDescription> _filter = IterableExtensions.<IEObjectDescription>filter(_exportedObjectsByType, _function);
    return _filter;
  }
  
  public boolean isEmpty() {
    IResourceDescriptions _delegate = this.getDelegate();
    boolean _isEmpty = _delegate.isEmpty();
    return _isEmpty;
  }
  
  public FilteringResourceDescriptions(final IResourceDescriptions delegate, final IJavaProject project) {
    super();
    this._delegate = delegate;
    this._project = project;
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((_delegate== null) ? 0 : _delegate.hashCode());
    result = prime * result + ((_project== null) ? 0 : _project.hashCode());
    return result;
  }
  
  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    FilteringResourceDescriptions other = (FilteringResourceDescriptions) obj;
    if (_delegate == null) {
      if (other._delegate != null)
        return false;
    } else if (!_delegate.equals(other._delegate))
      return false;
    if (_project == null) {
      if (other._project != null)
        return false;
    } else if (!_project.equals(other._project))
      return false;
    return true;
  }
  
  @Override
  public String toString() {
    String result = new ToStringHelper().toString(this);
    return result;
  }
}
