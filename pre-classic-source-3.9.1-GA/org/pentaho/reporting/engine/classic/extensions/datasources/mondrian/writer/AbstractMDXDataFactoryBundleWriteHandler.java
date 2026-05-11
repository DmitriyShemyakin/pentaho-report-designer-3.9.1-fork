/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2009 Pentaho Corporation.  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.writer;

import java.io.IOException;
import java.util.Properties;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.PasswordEncryptionService;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleDataFactoryWriterHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterException;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterState;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.AbstractMDXDataFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.CubeFileProvider;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.DataSourceProvider;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.MondrianDataFactoryModule;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.docbundle.WriteableDocumentBundle;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriterSupport;

public abstract class AbstractMDXDataFactoryBundleWriteHandler
    implements BundleDataFactoryWriterHandler
{
  protected void writeBody(final WriteableDocumentBundle bundle,
                           final BundleWriterState state,
                           final AbstractMDXDataFactory mdxDataFactory,
                           final XmlWriter xmlWriter) throws IOException, BundleWriterException
  {

    final AttributeList configAttrs = new AttributeList();
    if (StringUtils.isEmpty(mdxDataFactory.getJdbcPassword()) == false)
    {
      configAttrs.setAttribute(MondrianDataFactoryModule.NAMESPACE,
          "jdbc-password", String.valueOf(mdxDataFactory.getJdbcPassword()));
    }
    if (StringUtils.isEmpty(mdxDataFactory.getJdbcUser()) == false)
    {
      configAttrs.setAttribute(MondrianDataFactoryModule.NAMESPACE,
          "jdbc-user", String.valueOf(mdxDataFactory.getJdbcUser()));
    }
    if (StringUtils.isEmpty(mdxDataFactory.getRole()) == false)
    {
      configAttrs.setAttribute(MondrianDataFactoryModule.NAMESPACE,
          "role", String.valueOf(mdxDataFactory.getRole()));
    }
    if (StringUtils.isEmpty(mdxDataFactory.getJdbcPasswordField()) == false)
    {
      configAttrs.setAttribute(MondrianDataFactoryModule.NAMESPACE,
          "jdbc-password-field", String.valueOf(mdxDataFactory.getJdbcPasswordField()));
    }
    if (StringUtils.isEmpty(mdxDataFactory.getJdbcUserField()) == false)
    {
      configAttrs.setAttribute(MondrianDataFactoryModule.NAMESPACE,
          "jdbc-user-field", String.valueOf(mdxDataFactory.getJdbcUserField()));
    }
    if (StringUtils.isEmpty(mdxDataFactory.getRoleField()) == false)
    {
      configAttrs.setAttribute(MondrianDataFactoryModule.NAMESPACE,
          "role-field", String.valueOf(mdxDataFactory.getRoleField()));
    }
    if (StringUtils.isEmpty(mdxDataFactory.getDesignTimeName()) == false)
    {
      configAttrs.setAttribute(MondrianDataFactoryModule.NAMESPACE,
          "design-time-name", String.valueOf(mdxDataFactory.getDesignTimeName()));
    }
    writeProperties(xmlWriter, mdxDataFactory.getBaseConnectionProperties(), "mondrian-properties");
    
    xmlWriter.writeTag(MondrianDataFactoryModule.NAMESPACE, "connection", configAttrs, XmlWriter.OPEN);

    final DataSourceProvider dataSourceProvider = mdxDataFactory.getDataSourceProvider();
    if (dataSourceProvider != null)
    {
      writeConnectionInfo(bundle, state, xmlWriter, dataSourceProvider);
    }
    final CubeFileProvider cubeFileProvider = mdxDataFactory.getCubeFileProvider();
    if (cubeFileProvider != null)
    {
      writeCubeInfo(bundle, state, xmlWriter, cubeFileProvider);
    }

    xmlWriter.writeCloseTag();
  }

  private void writeConnectionInfo(final WriteableDocumentBundle bundle,
                                   final BundleWriterState state,
                                   final XmlWriter xmlWriter,
                                   final DataSourceProvider connectionProvider)
      throws IOException, BundleWriterException
  {
    final String configKey = MondrianDataFactoryModule.DATASOURCE_BUNDLEWRITER_PREFIX + connectionProvider.getClass().getName();
    final Configuration globalConfig = ClassicEngineBoot.getInstance().getGlobalConfig();
    final String value = globalConfig.getConfigProperty(configKey);
    if (value == null)
    {
      throw new BundleWriterException("Unable to locate writer for " + connectionProvider.getClass().getName());
    }
    final DataSourceProviderBundleWriteHandler handler =
        (DataSourceProviderBundleWriteHandler) ObjectUtilities.loadAndInstantiate
            (value, AbstractMDXDataFactoryBundleWriteHandler.class, DataSourceProviderBundleWriteHandler.class);
    if (handler == null)
    {
      throw new BundleWriterException("Invalid write-handler for " + connectionProvider.getClass().getName());
    }
    handler.write(bundle, state, xmlWriter, connectionProvider);
  }

  private void writeProperties(final XmlWriter writer,
                               final Properties properties,
                               final String tagName) throws IOException
  {
    if (properties.isEmpty())
    {
      return;
    }
    writer.writeTag (MondrianDataFactoryModule.NAMESPACE, tagName, XmlWriterSupport.OPEN);
    final String[] propertyNames = properties.keySet().toArray(new String[properties.size()]);
    for (int i = 0; i < propertyNames.length; i++)
    {
      final String name = propertyNames[i];
      final String value = properties.getProperty(name);
      writer.writeTag(MondrianDataFactoryModule.NAMESPACE,
          "property", "name", name, XmlWriterSupport.OPEN);
      if (name.toLowerCase().contains("password"))
      {
        writer.writeTextNormalized(PasswordEncryptionService.getInstance().encrypt(value), false);
      }
      else
      {
        writer.writeTextNormalized(value, false);
      }
      writer.writeCloseTag();
    }
    writer.writeCloseTag();
  }

  private void writeCubeInfo(final WriteableDocumentBundle bundle,
                             final BundleWriterState state,
                             final XmlWriter xmlWriter,
                             final CubeFileProvider cubeFileProvider)
      throws IOException, BundleWriterException
  {
    final String configKey = MondrianDataFactoryModule.CUBEFILE_BUNDLEWRITER_PREFIX + cubeFileProvider.getClass().getName();
    final Configuration globalConfig = ClassicEngineBoot.getInstance().getGlobalConfig();
    final String value = globalConfig.getConfigProperty(configKey);
    if (value == null)
    {
      throw new BundleWriterException("Unable to locate writer for " + cubeFileProvider.getClass().getName());
    }

    final CubeFileProviderBundleWriteHandler handler =
        (CubeFileProviderBundleWriteHandler) ObjectUtilities.loadAndInstantiate
            (value, AbstractMDXDataFactoryWriteHandler.class, CubeFileProviderBundleWriteHandler.class);
    if (handler == null)
    {
      throw new BundleWriterException("Invalid write-handler for " + cubeFileProvider.getClass().getName());
    }
    handler.write(bundle, state, xmlWriter, cubeFileProvider);
  }

}
