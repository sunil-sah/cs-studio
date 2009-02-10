/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
 package org.csstudio.platform.internal.usermanagement;

import org.csstudio.platform.internal.rightsmanagement.RightsManagementService;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.security.ILoginCallbackHandler;
import org.csstudio.platform.security.ILoginModule;
import org.csstudio.platform.security.User;
import org.csstudio.platform.workspace.WorkspaceIndependentStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;

/** The LoginContext performs the authentication by invoking
 *  the Login module and storing the authenticated user.
 *  <p>
 *  Expects exactly one authentication module.
 *  
 *  @author Original author wasn't listed
 *  @author Kay Kasemir
 *  @author Xihui Chen
 */
@SuppressWarnings("nls")
public final class LoginContext {
    /** ID of extension point for login modules */
	private static final String LOGIN_MODULE_EXT_ID = "org.csstudio.platform.loginModule";
	
    final private String _name;
	private User _user = null;
	
	/** Initialize
	 *  @param name Name of this login context
	 */
	public LoginContext(final String name)
	{
		_name = name;
	}
	
    /** @return Name of this login context */
	public String getName()
	{
		return _name;
	}
	
	/** Perform login, setting user name if successful
	 *  @param handler ILoginCallbackHandler
	 */
	public void login(final ILoginCallbackHandler handler) {
		final ILoginModule loginModule = getLoginModule();
		if (loginModule != null) {
			_user = loginModule.login(handler);
			if (_user != null) {
				CentralLogger.getInstance().getLogger(this).info(
						"User logged in: " + _user.getUsername());
				WorkspaceIndependentStore.writeLastLoginUser(_user.getUsername());
				RightsManagementService.getInstance().readRightsForUser(_user);
			} else {
				CentralLogger.getInstance().getLogger(this).info("Anonymous login");
			}
		} else {
			_user = null;
			CentralLogger.getInstance().getLogger(this).warn("No login module provided. " +
					"The system acts as anonymous login");
		}
	}

	/** Obtain the login module.
	 *  Expects exactly one implementation.
	 *  
	 *  @return ILoginModule or <code>null</code>
	 */
	private ILoginModule getLoginModule()
	{
		IExtension[] extension = Platform.getExtensionRegistry()
			.getExtensionPoint(LOGIN_MODULE_EXT_ID)
			.getExtensions();
		if (extension.length == 1)
		{
		    final IExtension lmExtension = extension[0];
			final IConfigurationElement lmConfigElement = lmExtension
					.getConfigurationElements()[0];
			try
			{
				return (ILoginModule) lmConfigElement
						.createExecutableExtension("class");
			}
			catch (CoreException e)
			{
			    CentralLogger.getInstance().getLogger(this).
			        error("Cannot obtain login module", e);
			}
		}
		else if (extension.length > 1)
            CentralLogger.getInstance().getLogger(this).
                error("Found multiple login modules");
		return null;
	}
	
	public void logout() {
	    // NOP
	}
	
	public User getUser() {
		return _user;
	}
	
	/** @return <code>true</code> if we located exactly one login module */
	public boolean isLoginAvailable() {
		return (getLoginModule() != null);
	}
}
