package org.onebusaway.nyc.webapp.actions.admin;

import org.onebusaway.nyc.webapp.actions.OneBusAwayNYCAdminActionSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;

public class DeployConfigAction extends OneBusAwayNYCAdminActionSupport
    implements ServletContextAware {

  private static Logger _log = LoggerFactory.getLogger(DeployConfigAction.class);
  private static final long serialVersionUID = 1L;
  private static final String DEPOT_ID_PATH = "depot_id_map";
  private static final String DSC_PATH = "destination_sign_codes";
  private String environment;
  private String s3DepotPath;
  private String s3DscPath;

  public String getEnvironment() {
    return environment;
  }
  
  public String getS3DepotPath() {
    return s3DepotPath;
  }
  
  public String getS3DscPath() {
    return s3DscPath;
  }
  
  @Override
  public void setServletContext(ServletContext context) {
    if (context != null) {
      String obanycEnv = context.getInitParameter("obanyc.environment");
      if (obanycEnv != null && obanycEnv.length() > 0) {
        environment = obanycEnv;
        s3DepotPath = "s3://"
            + context.getInitParameter("s3.config.bucketName") + "/" 
            + environment + "/" + DEPOT_ID_PATH + "/";
        s3DscPath = "s3://" + context.getInitParameter("s3.config.bucketName") + "/"
            + environment + "/" + DSC_PATH + "/";
        _log.info("injecting env=" + environment + ", s3DepotPath="
            + s3DepotPath);
      }
    }
  }
}
