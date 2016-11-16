/*
 * TODO Copyright
 */

package com.company.cubamapexample.web.system;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Configuration;
import com.haulmont.cuba.core.global.GlobalConfig;
import com.haulmont.cuba.core.sys.AppContext;
import com.haulmont.cuba.core.sys.SecurityContext;
import com.haulmont.cuba.security.app.LoginService;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.cuba.web.auth.WebAuthConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.concurrent.Callable;

public class SystemUtils {
    private static final Logger log = LoggerFactory.getLogger(SystemUtils.class);

    public static void doAsPrivilegedUser(Runnable runnable) {
        getAsPrivilegedUser(() -> {
            runnable.run();
            return null;
        });
    }

    public static <T> T getAsPrivilegedUser(Callable<T> callable) {
        SecurityContext sc = AppContext.getSecurityContext();
        T result;
        try {
            LoginService loginService = AppBeans.get(LoginService.NAME);
            Configuration configuration = AppBeans.get(Configuration.NAME);
            WebAuthConfig webConfig = configuration.getConfig(WebAuthConfig.class);
            String trustedPassword = webConfig.getTrustedClientPassword();
            GlobalConfig globalConfig = configuration.getConfig(GlobalConfig.class);
            Locale systemLocale = globalConfig.getAvailableLocales().values().iterator().next();

            UserSession session = loginService.loginTrusted("anonymous", trustedPassword, systemLocale);
            AppContext.setSecurityContext(new SecurityContext(session));

            try {
                result = callable.call();
            } finally {
                loginService.logout();
            }
        } catch (Exception e) {
            log.debug("Unable to execute privileged action", e);

            throw new PrivilegedActionException(e);
        } finally {
            AppContext.setSecurityContext(sc);
        }
        return result;
    }
}
