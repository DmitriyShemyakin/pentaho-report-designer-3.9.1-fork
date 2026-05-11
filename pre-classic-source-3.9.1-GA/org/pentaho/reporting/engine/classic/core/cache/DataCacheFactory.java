package org.pentaho.reporting.engine.classic.core.cache;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * Todo: Document me!
 * <p/>
 * Date: 17.01.11
 * Time: 17:10
 *
 * @author Thomas Morgner.
 */
public class DataCacheFactory
{
  private static final Log logger = LogFactory.getLog(DataCacheFactory.class);
  private static DataCache dataCache;
  private static boolean noCacheDefined;

  private DataCacheFactory()
  {
  }

  public static synchronized DataCache getCache()
  {
    if (dataCache == null && noCacheDefined == false)
    {
      final Configuration config = ClassicEngineBoot.getInstance().getGlobalConfig();
      final String cacheImpl = config.getConfigProperty("org.pentaho.reporting.engine.classic.core.cache.DataCache");

      dataCache = (DataCache) ObjectUtilities.loadAndInstantiate(cacheImpl, DataCacheFactory.class, DataCache.class);
      if (dataCache == null)
      {
        logger.info("Unable to create valid cache, returning <null>");
        noCacheDefined = true;
        dataCache = null;
      }
    }
    return dataCache;
  }
}
