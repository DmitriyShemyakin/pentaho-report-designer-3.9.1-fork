package org.pentaho.reporting.engine.classic.core.cache;

import javax.swing.table.TableModel;

/**
 * Todo: Document me!
 * <p/>
 * Date: 17.01.11
 * Time: 16:54
 *
 * @author Thomas Morgner.
 */
public interface DataCache
{
  public TableModel get(DataCacheKey key);
  public TableModel put (DataCacheKey key, TableModel model);

  public DataCacheManager getCacheManager();
}
