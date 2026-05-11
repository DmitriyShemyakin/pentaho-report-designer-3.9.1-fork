package org.pentaho.reporting.designer.extensions.pentaho.repository.model;

import org.pentaho.reporting.designer.core.auth.AuthenticationData;

/**
 * Todo: Document me!
 *
 * @author Thomas Morgner.
 * @deprecated 
 */
public class LoginData extends AuthenticationData
{
  public LoginData(final String baseUrl)
  {
    super(baseUrl);
  }

  public LoginData(final String baseUrl, final String username, final String password, final int timeout)
  {
    this(baseUrl);
    setOption("username", username);
    setOption("password", password);
    setOption("timeout", String.valueOf(timeout));
  }
}
