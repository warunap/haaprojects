package c4j.haa.decisions.servlet;

import java.io.File;

import org.apache.commons.logging.LogFactory;

import com.corticon.init.CcProperties;
import com.corticon.log.Log;
import com.corticon.service.ccserver.CcServerFactory;
import com.corticon.service.ccserver.ICcServer;
import com.corticon.service.ccserver.exception.CcServerInvalidArgumentException;
import com.corticon.service.ccserver.exception.CcServerInvalidCddException;

public class CcSoapServerInit {

	private static final org.apache.commons.logging.Log soapLogger = LogFactory.getLog(CcSoapServerInit.class);

	private String cstrUserDirPropName = "user.dir";

	private String cstrCddPathCdd = "cdd";

	private static ICcServer sCcServer = null;

	//////////////////////////////////////////////////////////////////////
	//                   Constructor Methods   
	//////////////////////////////////////////////////////////////////////  
	public CcSoapServerInit() {
		//  Just initialize the Server on creation
		this.intializeCcServer();
	}

	//////////////////////////////////////////////////////////////////////
	//                   Primary Methods   
	//////////////////////////////////////////////////////////////////////  
	public void intializeCcServer() {

		//  Get the location of the Apache Corticon cdd directory
		String lstrPath = this.findCorticonDirectory();
		soapLogger.info("get path " + lstrPath);
		if (lstrPath == null)
			return;

		//  Log Messages
		Log.logDebug("CcSoapServerInit.intializeCcServer() ==== @@@ Found Corticon CDD directory: " + lstrPath, this);
		System.out.println("@@@ Found Corticon CDD directory: " + lstrPath);

		//  Create a new CcServer Instance
		ICcServer lCcServer = CcServerFactory.getCcServer();

		try {
			//  Call the CcServer's loadFromCddDir...this will locate all cdd files in the 
			//  directory and load them
			lCcServer.loadFromCddDir(lstrPath);
		} catch (CcServerInvalidArgumentException e) {
			//  Log Messages
			Log.logException(e,
					"CcSoapServerInit.intializeCcServer() ==== CcServerInvalidArgumentException was thrown because of: "
							+ lstrPath, this);
			e.printStackTrace();
		} catch (CcServerInvalidCddException e) {
			//  Log Messages
			Log.logException(e, "CcSoapServerInit.intializeCcServer() ==== CcServerInvalidCddException was thrown",
					this);
			e.printStackTrace();
		}
	}

	//////////////////////////////////////////////////////////////////////
	//                   Utility Methods   
	//////////////////////////////////////////////////////////////////////  

	/**
	 * Find the Corticon Cdd Directory under Apache.
	 */
	private String findCorticonDirectory() {
		//  Check to see if the user defined an alternative directory.
		//  If it is not there, then we default back to the Parent/cdd dir
		String lstrAutoLoadDir = CcProperties.getCcProperty("com.corticon.soap.autoloaddir", "");
		if (lstrAutoLoadDir.length() > 0) {
			//  Verify that the File Directory exists
			File lFile = new File(lstrAutoLoadDir);
			if (lFile.exists() && lFile.isDirectory())
				return lstrAutoLoadDir;

			Log.logDebug("Auto-loading of .cdd files was terminated.  Unable to find directory: " + lstrAutoLoadDir,
					this);
			System.out.println("Auto-loading of .cdd files was terminated.  Unable to find directory: "
					+ lstrAutoLoadDir);

			return null;
		}

		//  Property was not passed in...use defaults
		String lstrUserDir = System.getProperty(this.cstrUserDirPropName);
		if (lstrUserDir == null)
			return null;

		String lstrPath = lstrUserDir + File.separator + cstrCddPathCdd;

		//  Verify that the File Directory exists
		File lFile = new File(lstrPath);
		if (lFile.exists() && lFile.isDirectory())
			return lstrPath;

		Log.logDebug("Auto-loading of .cdd files was terminated.  Unable to find directory: " + lstrPath, this);
		System.out.println("Auto-loading of .cdd files was terminated.  Unable to find directory: " + lstrPath);

		return null;
	}
}
