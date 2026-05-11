package org.pentaho.reporting.engine.classic.extensions.datasources.cda;

import java.util.LinkedHashMap;
import javax.swing.table.TableModel;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ParameterMapping;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

public class CdaDataFactory implements DataFactory, Cloneable
{
  private LinkedHashMap<String, CdaQueryEntry> queries;
  private String baseUrl;
  private String baseUrlField;
  private boolean useLocalCall;
  private String username;
  private String password;

  private String solution;
  private String path;
  private String file;

  private transient Configuration configuration;
  private transient ResourceManager resourceManager;
  private transient ResourceKey contextKey;
  private transient ResourceBundleFactory resourceBundleFactory;
  private CdaQueryBackend backend;
  private transient CdaQueryBackend effectiveBackend;

  public CdaDataFactory()
  {
    this.useLocalCall = true;
    this.queries = new LinkedHashMap<String, CdaQueryEntry>();
  }

  public boolean isUseLocalCall()
  {
    return useLocalCall;
  }

  public void setUseLocalCall(final boolean useLocalCall)
  {
    this.useLocalCall = useLocalCall;
  }

  public void cancelRunningQuery()
  {
    if (effectiveBackend != null)
    {
      effectiveBackend.cancelRunningQuery();
    }
  }

  public void open() throws ReportDataFactoryException
  {
  }

  public void close()
  {
  }

  public DataFactory derive()
  {
    return (CdaDataFactory) clone();
  }

  public String[] getQueryNames()
  {
    return queries.keySet().toArray(new String[queries.size()]);
  }

  @Override
  public void initialize(final Configuration configuration, final ResourceManager resourceManager,
                         final ResourceKey contextKey, final ResourceBundleFactory resourceBundleFactory)
  {
    this.configuration = configuration;
    this.resourceManager = resourceManager;
    this.contextKey = contextKey;
    this.resourceBundleFactory = resourceBundleFactory;

    if (backend != null)
    {
      effectiveBackend = backend;
    }
    else
    {
      if (useLocalCall)
      {
        final String className = configuration.getConfigProperty(CdaQueryBackend.class.getName());
        effectiveBackend = (CdaQueryBackend)
            ObjectUtilities.loadAndInstantiate(className, CdaQueryBackend.class, CdaQueryBackend.class);
      }
      if (effectiveBackend == null)
      {
        effectiveBackend = new HttpQueryBackend();
      }
    }
  }

  /**
   * Checks whether the query would be executable by this datafactory. This performs a rough check, not a full query.
   *
   * @param query
   * @param parameters
   * @return
   */
  public boolean isQueryExecutable(final String query, final DataRow parameters)
  {
    return queries.containsKey(query);
  }

  /**
   * @deprecated Replaced by setQueryEntry
   */
  public void setQuery(final String name, final String id)
  {
    if (id == null)
    {
      queries.remove(name);
    }
    else
    {
      queries.put(name, new CdaQueryEntry(name, id));
    }
  }

  public void setQueryEntry(final String name, final CdaQueryEntry cdaqueryentry)
  {
    if (cdaqueryentry == null)
    {
      queries.remove(name);
    }
    else
    {
      queries.put(name, cdaqueryentry);
    }
  }

  /**
   * @deprecated Replaced by getQueryEntry(name)
   */
  public String getQuery(final String name)
  {
    return queries.get(name).getId();
  }

  public CdaQueryEntry getQueryEntry(final String name)
  {
    return queries.get(name);
  }

  /**
   * @deprecated Not used anymore
   */
  public ParameterMapping[] getQueryParameters(final String name)
  {
    return queries.get(name).getParameters();
  }

  public TableModel queryData(final String query, final DataRow parameters)
      throws ReportDataFactoryException
  {
    final CdaQueryEntry realQuery = getQueryEntry(query);
    effectiveBackend.setFile(getFile());
    effectiveBackend.setSolution(getSolution());
    effectiveBackend.setPath(getPath());
    effectiveBackend.setUsername(getUsername());
    effectiveBackend.setPassword(getPassword());
    effectiveBackend.setBaseUrl(computeBaseUrl(parameters));
    effectiveBackend.initialize(configuration, resourceManager, contextKey, resourceBundleFactory);
    return effectiveBackend.queryData(realQuery, parameters);
  }

  private String computeBaseUrl(final DataRow dataRow)
  {
    if (baseUrlField != null)
    {
      final Object baseUrlRaw = dataRow.get(baseUrlField);
      if (baseUrlRaw != null)
      {
        return String.valueOf(baseUrlRaw);
      }
    }
    return baseUrl;
  }

  public Object clone()
  {
    try
    {
      final CdaDataFactory dataFactory = (CdaDataFactory) super.clone();
      dataFactory.queries = (LinkedHashMap<String, CdaQueryEntry>) queries.clone();
      if (backend != null)
      {
        dataFactory.backend = (CdaQueryBackend) backend.clone();
      }
      return dataFactory;
    }
    catch (CloneNotSupportedException cne)
    {
      throw new IllegalStateException(cne);
    }
  }

  public void setBackend(final CdaQueryBackend backend)
  {
    if (backend == null)
    {
      throw new NullPointerException();
    }
    this.backend = backend;
  }

  public CdaQueryBackend getBackend()
  {
    return backend;
  }

  public String getUsername()
  {
    return username;
  }

  public void setUsername(final String username)
  {
    this.username = username;
  }

  public String getPassword()
  {
    return password;
  }

  public void setPassword(final String password)
  {
    this.password = password;
  }

  public String getSolution()
  {
    return solution;
  }

  public void setSolution(final String solution)
  {
    this.solution = solution;
  }

  public String getPath()
  {
    return path;
  }

  public void setPath(final String path)
  {
    this.path = path;
  }

  public String getFile()
  {
    return file;
  }

  public void setFile(final String file)
  {
    this.file = file;
  }

  public String getBaseUrl()
  {
    return baseUrl;
  }

  public void setBaseUrl(final String baseUrl)
  {
    this.baseUrl = baseUrl;
  }

  public String getBaseUrlField()
  {
    return baseUrlField;
  }

  public void setBaseUrlField(final String baseUrlField)
  {
    this.baseUrlField = baseUrlField;
  }

}
