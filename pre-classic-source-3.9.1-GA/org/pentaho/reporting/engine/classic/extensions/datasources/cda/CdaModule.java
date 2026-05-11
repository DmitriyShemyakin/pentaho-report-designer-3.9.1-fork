package org.pentaho.reporting.engine.classic.extensions.datasources.cda;

import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaDataParser;
import org.pentaho.reporting.libraries.base.boot.AbstractModule;
import org.pentaho.reporting.libraries.base.boot.ModuleInitializeException;
import org.pentaho.reporting.libraries.base.boot.SubSystem;

/**
 * Todo: Document me!
 * <p/>
 * Date: 16.12.10
 * Time: 18:19
 *
 * @author Thomas Morgner.
 */
public class CdaModule extends AbstractModule
{
  public static final String NAMESPACE = "http://jfreereport.sourceforge.net/namespaces/datasources/cda";
  public static final String TAG_DEF_PREFIX = "org.pentaho.reporting.engine.classic.extensions.datasources.cda.tag-def.";

  public CdaModule() throws ModuleInitializeException
  {
    loadModuleInfo();
  }

  public void initialize(final SubSystem subSystem) throws ModuleInitializeException
  {
    ElementMetaDataParser.initializeOptionalDataFactoryMetaData
        ("org/pentaho/reporting/engine/classic/extensions/datasources/cda/meta-datafactory.xml");

  }
}
