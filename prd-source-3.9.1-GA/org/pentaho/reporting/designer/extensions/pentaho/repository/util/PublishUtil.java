package org.pentaho.reporting.designer.extensions.pentaho.repository.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.MessageFormat;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.ByteArrayPartSource;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.FileSystemOptions;
import org.apache.commons.vfs.auth.StaticUserAuthenticator;
import org.apache.commons.vfs.impl.DefaultFileSystemConfigBuilder;
import org.pentaho.core.util.PublisherUtil;
import org.pentaho.reporting.designer.core.ReportDesignerBoot;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.actions.global.OpenReportAction;
import org.pentaho.reporting.designer.core.auth.AuthenticationData;
import org.pentaho.reporting.designer.core.auth.AuthenticationHelper;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;
import org.pentaho.reporting.designer.core.util.ExternalToolLauncher;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriter;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterException;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.pensol.vfs.WebSolutionFileProvider;
import org.pentaho.reporting.libraries.repository.ContentIOException;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.xmlns.common.ParserUtil;

/**
 * Todo: Document me!
 *
 * @author Thomas Morgner.
 */
public class PublishUtil
{
  private static final String WEB_SOLUTION_PREFIX = "web-solution:";

  private PublishUtil()
  {

  }

  public static ReportRenderContext openReport(final ReportDesignerContext context,
                                               final AuthenticationData loginData,
                                               final String path) throws IOException, ReportDataFactoryException, ResourceException
  {
    if (StringUtils.isEmpty(path))
    {
      throw new IOException("Path is empty.");
    }

    final String[] pathElements = StringUtils.split(path, "/");
    if (pathElements.length < 2)
    {
      throw new IOException("Path is invalid.");
    }

    final int lastElementIndex = pathElements.length - 1;

    final String solution = pathElements[0];
    final String filename = pathElements[lastElementIndex];
    final StringBuilder contentPath = new StringBuilder();
    for (int i = 1; i < lastElementIndex; i++)
    {
      contentPath.append('/');
      contentPath.append(pathElements[i]);
    }

    final Configuration config = ReportDesignerBoot.getInstance().getGlobalConfig();
    final String urlMessage =
        config.getConfigProperty("org.pentaho.reporting.designer.extensions.pentaho.repository.OpenFileServicePath");
    final MessageFormat fmt = new MessageFormat(urlMessage);
    final String urlText = fmt.format(new Object[]{URLEncoder.encode(solution, "UTF-8"),
        URLEncoder.encode(contentPath.toString(), "UTF-8"), URLEncoder.encode(filename, "UTF-8")});

    final GetMethod method = new GetMethod(loginData.getUrl() + urlText);

    final HttpClient httpClient = createHttpClient(loginData);
    final int status = httpClient.executeMethod(method);
    if (status != HttpStatus.SC_OK)
    {
      //noinspection ThrowCaughtLocally
      throw new IOException("Failure: " + status);
    }

    final MasterReport report = loadReport(method.getResponseBody(), path);
    final int index = context.addMasterReport(report);
    return context.getReportRenderContext(index);
  }

  public static HttpClient createHttpClient(final AuthenticationData loginData)
  {
    final HttpClient httpClient = createHttpClient();
    httpClient.getState().setCredentials(AuthScope.ANY,
        AuthenticationHelper.getCredentials(loginData.getUsername(), loginData.getPassword()));
    return httpClient;
  }

  private static MasterReport loadReport(final byte[] data, final String fileName) throws
      IOException, ResourceException
  {
    if (data == null)
    {
      throw new NullPointerException();
    }
    final ResourceManager resourceManager = new ResourceManager();
    resourceManager.registerDefaults();

    final MasterReport resource = OpenReportAction.loadReport(data, resourceManager);
    resource.setAttribute
        (ReportDesignerBoot.DESIGNER_NAMESPACE, "report-save-path", fileName); // NON-NLS
    return resource;
  }

  public static void launchReportOnServer(final String baseUrl, final String path) throws IOException
  {

    if (StringUtils.isEmpty(path))
    {
      throw new IOException("Path is empty.");
    }

    final String[] pathElements = StringUtils.split(path, "/");
    if (pathElements.length < 2)
    {
      throw new IOException("Path is invalid.");
    }

    final int lastElementIndex = pathElements.length - 1;

    final String solution = pathElements[0];
    final String filename = pathElements[lastElementIndex];
    final StringBuilder contentPath = new StringBuilder();
    for (int i = 1; i < lastElementIndex; i++)
    {
      contentPath.append('/');
      contentPath.append(pathElements[i]);
    }


    final Configuration config = ReportDesignerBoot.getInstance().getGlobalConfig();
    final String urlMessage = config.getConfigProperty
        ("org.pentaho.reporting.designer.extensions.pentaho.repository.LaunchReport");
    final MessageFormat fmt = new MessageFormat(urlMessage);
    final String fullpath = fmt.format(new Object[]{URLEncoder.encode(solution, "UTF-8"),
        URLEncoder.encode(contentPath.toString(), "UTF-8"), URLEncoder.encode(filename, "UTF-8")});
    final String url = baseUrl + fullpath;
    ExternalToolLauncher.openURL(url);
  }

  public static byte[] createBundleData(final MasterReport report) throws PublishException, BundleWriterException
  {
    try
    {
      final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      BundleWriter.writeReportToZipStream(report, outputStream);
      return outputStream.toByteArray();
    }
    catch (final ContentIOException e)
    {
      throw new BundleWriterException("Failed to write report", e);
    }
    catch (final IOException e)
    {
      throw new BundleWriterException("Failed to write report", e);
    }
  }

  public static void publish(final byte[] data,
                             final String reportPathOnServer,
                             final AuthenticationData loginData,
                             final String publishPassword)
      throws PublishException
  {
    try
    {
      final String path = IOUtils.getInstance().getPath(reportPathOnServer);
      final String fileName = IOUtils.getInstance().getFileName(reportPathOnServer);

      final Configuration config = ReportDesignerBoot.getInstance().getGlobalConfig();
      final String publishPattern = config.getConfigProperty
          ("org.pentaho.reporting.designer.extensions.pentaho.repository.PublishService");

      final String encodedPublishKey;
      if (publishPassword.isEmpty())
      {
        encodedPublishKey = "";
      }
      else
      {
        encodedPublishKey = PublisherUtil.getPasswordKey(publishPassword);
      }

      final String publishURL = loginData.getUrl() + MessageFormat.format(publishPattern,
          URLEncoder.encode(encodedPublishKey, "UTF-8"),
          URLEncoder.encode(path, "UTF-8"),
          "true");// NON-NLS

      final String reportNameEncoded = (URLEncoder.encode(fileName, "UTF-8"));
      final ByteArrayPartSource source = new ByteArrayPartSource(reportNameEncoded, data);
      final FilePart filePart = new FilePart
          (reportNameEncoded, source, FilePart.DEFAULT_CONTENT_TYPE, "UTF-8");

      final PostMethod filePost = new PostMethod(publishURL);
      filePost.setRequestEntity(new MultipartRequestEntity(new Part[]{filePart}, filePost.getParams()));

      final HttpClient httpClient = createHttpClient(loginData);
      final int status = httpClient.executeMethod(filePost);
      if (status != HttpStatus.SC_OK)
      {
        if (status == HttpStatus.SC_MOVED_TEMPORARILY ||
            status == HttpStatus.SC_FORBIDDEN ||
            status == HttpStatus.SC_UNAUTHORIZED)
        {
          throw new PublishException(PublishException.ERROR_INVALID_USERNAME_OR_PASSWORD);
        }
        else
        {
          throw new PublishException(PublishException.ERROR_FAILED, status);
        }
      }

      final String postResult = filePost.getResponseBodyAsString();
      System.out.println(postResult);
      final int rtn = Integer.parseInt(postResult.trim());
      if (rtn == 3)
      {
        return;
      }
      throw new PublishException(PublishException.ERROR_FAILED);
    }
    catch (IOException e)
    {
      throw new PublishException(PublishException.ERROR_FAILED, e);
    }
  }

  private static HttpClient createHttpClient()
  {
    final HttpClient client = new HttpClient();
    client.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
    client.getParams().setSoTimeout(WorkspaceSettings.getInstance().getConnectionTimeout() * 1000);
    client.getParams().setAuthenticationPreemptive(true);
    return client;
  }


  public static boolean acceptFilter(final String[] filters, final String name)
  {
    if (filters == null || filters.length == 0)
    {
      return true;
    }
    for (int i = 0; i < filters.length; i++)
    {
      if (name.endsWith(filters[i]))
      {
        return true;
      }
    }
    return false;
  }

  public static FileObject createVFSConnection(final FileSystemManager fileSystemManager,
                                               final AuthenticationData loginData) throws FileSystemException
  {
    if (fileSystemManager == null)
    {
      throw new NullPointerException();
    }
    if (loginData == null)
    {
      throw new NullPointerException();
    }

    WebSolutionFileProvider.setConnectionTimeout(getTimeout(loginData) * 1000);
    final String normalizedUrl = normalizeURL(loginData.getUrl());
    final FileSystemOptions fileSystemOptions = new FileSystemOptions();
    final DefaultFileSystemConfigBuilder configBuilder = new DefaultFileSystemConfigBuilder();
    configBuilder.setUserAuthenticator(fileSystemOptions, new StaticUserAuthenticator(normalizedUrl,
        loginData.getUsername(), loginData.getPassword()));
    return fileSystemManager.resolveFile(normalizedUrl, fileSystemOptions);
  }

  public static int getTimeout(final AuthenticationData loginData)
  {
    final String s = loginData.getOption("timeout");
    return ParserUtil.parseInt(s, WorkspaceSettings.getInstance().getConnectionTimeout());
  }

  public static String normalizeURL(final String baseURL)
  {
    if (baseURL == null)
    {
      throw new NullPointerException();
    }
    final StringBuilder prefix = new StringBuilder(100);
    final String url2;
    if (baseURL.toLowerCase().startsWith("http://")) // NON-NLS
    {
      url2 = baseURL.substring("http://".length());// NON-NLS
      prefix.append(WEB_SOLUTION_PREFIX);
      prefix.append("http://");// NON-NLS
    }
    else if (baseURL.toLowerCase().startsWith("https://"))// NON-NLS
    {
      url2 = baseURL.substring("https://".length());// NON-NLS
      prefix.append(WEB_SOLUTION_PREFIX);
      prefix.append("https://");// NON-NLS
    }
    else
    {
      throw new IllegalArgumentException("Not a expected URL");
    }
    return prefix.append(url2).toString();
  }
}
