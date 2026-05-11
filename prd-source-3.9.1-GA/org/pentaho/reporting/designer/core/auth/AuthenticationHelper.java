package org.pentaho.reporting.designer.core.auth;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.NTCredentials;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.pentaho.reporting.libraries.base.util.StringUtils;

/**
 * Todo: Document me!
 *
 * @author Thomas Morgner.
 */
public class AuthenticationHelper
{
  private static final char DOMAIN_SEPARATOR = '\\';

  private AuthenticationHelper()
  {
  }

  public static Credentials getCredentials(final String user,
                                           final String password)
  {
    if (StringUtils.isEmpty(user))
    {
      return null;
    }

    final int domainIdx = user.indexOf(DOMAIN_SEPARATOR);
    if (domainIdx == -1)
    {
      return new UsernamePasswordCredentials(user, password);
    }
    try
    {
      final String domain = user.substring(0, domainIdx);
      final String username = user.substring(domainIdx + 1);
      final String host = InetAddress.getLocalHost().getHostName();
      return new NTCredentials(username, password, host, domain);
    }
    catch (UnknownHostException uhe)
    {
      return new UsernamePasswordCredentials(user, password);
    }
  }

  public static Credentials getCredentials(final String url, final AuthenticationStore store)
  {
    final String user = store.getUsername(url);
    if (user == null)
    {
      return null;
    }

    final String password = store.getPassword(url);
    final int domainIdx = user.indexOf(DOMAIN_SEPARATOR);
    if (domainIdx == -1)
    {
      return new UsernamePasswordCredentials(user, password);
    }
    try
    {
      final String domain = user.substring(0, domainIdx);
      final String username = user.substring(domainIdx + 1);
      final String host = InetAddress.getLocalHost().getHostName();
      return new NTCredentials(username, password, host, domain);
    }
    catch (UnknownHostException uhe)
    {
      return new UsernamePasswordCredentials(user, password);
    }
  }
}
