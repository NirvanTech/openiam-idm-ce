/*
 * Copyright 2009, OpenIAM LLC This file is part of the OpenIAM Identity and
 * Access Management Suite
 * 
 * OpenIAM Identity and Access Management Suite is free software: you can
 * redistribute it and/or modify it under the terms of the Lesser GNU General
 * Public License version 3 as published by the Free Software Foundation.
 * 
 * OpenIAM is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the Lesser GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * OpenIAM. If not, see <http://www.gnu.org/licenses/>. *
 */

/**
 *
 */
package org.openiam.idm.srvc.auth.spi;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.exception.AuthenticationException;
import org.openiam.idm.searchbeans.ResourceSearchBean;
import org.openiam.idm.srvc.auth.context.AuthenticationContext;
import org.openiam.idm.srvc.auth.context.PasswordCredential;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.dto.SSOToken;
import org.openiam.idm.srvc.auth.dto.Subject;
import org.openiam.idm.srvc.auth.service.AuthenticationConstants;
import org.openiam.idm.srvc.auth.sso.SSOTokenFactory;
import org.openiam.idm.srvc.auth.sso.SSOTokenModule;
import org.openiam.idm.srvc.auth.ws.LoginResponse;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSystemObjectMatchEntity;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.mngsys.service.ManagedSysDAO;
import org.openiam.idm.srvc.policy.dto.Policy;
import org.openiam.idm.srvc.policy.dto.PolicyAttribute;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.dto.ResourceProp;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import java.util.*;

/**
 * LDAPLoginModule provides basic password based authentication using an LDAP directory.
 *
 * @author suneet
 */
@Component("ldapLoginModule")
public class LDAPLoginModule extends AbstractLoginModule {

    @Autowired
    private ManagedSysDAO managedSysDAO;

    private static final Log log = LogFactory.getLog(LDAPLoginModule.class);

    public LDAPLoginModule() {
    }

    /*
    public void globalLogout(String securityDomain, String principal) {
        // TODO Auto-generated method stub

    }
    */

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.idm.srvc.auth.spi.LoginModule#login(org.openiam.idm.srvc.
     * auth.context.AuthenticationContext)
     */
    @Override
    public Subject login(AuthenticationContext authContext) throws Exception {
        Subject sub = new Subject();
        log.debug("login() in LDAPLoginModule called");
        String clientIP = authContext.getClientIP();
        String nodeIP = authContext.getNodeIP();

        String authPolicyId = sysConfiguration.getDefaultAuthPolicyId();
        Policy authPolicy = policyDataService.getPolicy(authPolicyId);
        PolicyAttribute policyAttribute = authPolicy
                .getAttribute("MANAGED_SYS_ID");
        if (policyAttribute == null) {
            throw new AuthenticationException(
                    AuthenticationConstants.RESULT_INVALID_CONFIGURATION);
        }
        ManagedSysEntity mse = managedSysDAO.findById(policyAttribute.getValue1());
        if (mse == null)
            throw new AuthenticationException(
                    AuthenticationConstants.RESULT_INVALID_CONFIGURATION);

        String host = mse.getHostUrl();
        String adminUserName = mse.getUserId();
        String adminPassword = this.decryptPassword(systemUserId, mse.getPswd());
        String protocol = mse.getCommProtocol();
        String managedSysId = mse.getId();
        String baseDn = null;
        String searchFilter = null;
        String pkAttribute = null;
        String dn = null;
        Set<ManagedSystemObjectMatchEntity> managedSystemObjectMatchEntities = mse.getMngSysObjectMatchs();
        if (CollectionUtils.isNotEmpty(managedSystemObjectMatchEntities)) {
            for (ManagedSystemObjectMatchEntity objectMatchEntity : managedSystemObjectMatchEntities) {
                if ("USER".equals(objectMatchEntity.getObjectType())) {
                    baseDn = objectMatchEntity.getBaseDn();
                    searchFilter = objectMatchEntity.getSearchFilter();
                    pkAttribute = objectMatchEntity.getKeyField();
                    dn = objectMatchEntity.getKeyField();
                    break;
                }
            }
        }

        // current date
        Date curDate = new Date(System.currentTimeMillis());
        PasswordCredential cred = (PasswordCredential) authContext
                .getCredential();

        String principal = cred.getPrincipal();
        String password = cred.getPassword();
        String distinguishedName = null;

        // search for the login value passed in
        // if found - get the dn
        // authenticate with the dn
        // if ok - then success
        // // get the user status in idm and check that

        LdapContext ldapCtx = connect(adminUserName, adminPassword, host, protocol);
        NamingEnumeration ne = search(ldapCtx, principal,dn,searchFilter,baseDn);
        if (ne == null) {
//            log("AUTHENTICATION", "AUTHENTICATION", "FAIL", "INVALID LOGIN",
//                    domainId, null, principal, null, null, clientIP, nodeIP);
            throw new AuthenticationException(
                    AuthenticationConstants.RESULT_INVALID_LOGIN);
        }
        try {
            while (ne.hasMore()) {
                SearchResult sr = (SearchResult) ne.next();

                distinguishedName = sr.getNameInNamespace();

            }

        } catch (NamingException e) {
            log.error(e);
//            log("AUTHENTICATION", "AUTHENTICATION", "FAIL", "INVALID LOGIN",
//                    domainId, null, principal, null, null, clientIP, nodeIP);
            throw new AuthenticationException(
                    AuthenticationConstants.RESULT_INVALID_LOGIN);

        }

        log.debug("Distinguished name=" + distinguishedName);

        if (distinguishedName == null || distinguishedName.length() == 0) {
//            log("AUTHENTICATION", "AUTHENTICATION", "FAIL", "INVALID LOGIN",
//                    domainId, null, principal, null, null, clientIP, nodeIP);
            throw new AuthenticationException(
                    AuthenticationConstants.RESULT_INVALID_LOGIN);
        }

        log.debug("Authentication policyid="
                + sysConfiguration.getDefaultAuthPolicyId());
        // get the authentication lock out policy
        Policy plcy = policyDataService.getPolicy(sysConfiguration.getDefaultAuthPolicyId());
        String attrValue = getPolicyAttribute(plcy.getPolicyAttributes(), "FAILED_AUTH_COUNT");

        String tokenType = getPolicyAttribute(plcy.getPolicyAttributes(), "TOKEN_TYPE");
        String tokenLife = getPolicyAttribute(plcy.getPolicyAttributes(), "TOKEN_LIFE");
        String tokenIssuer = getPolicyAttribute(plcy.getPolicyAttributes(), "TOKEN_ISSUER");

        Map tokenParam = new HashMap();
        tokenParam.put("TOKEN_TYPE", tokenType);
        tokenParam.put("TOKEN_LIFE", tokenLife);
        tokenParam.put("TOKEN_ISSUER", tokenIssuer);
        tokenParam.put("PRINCIPAL", principal);

       LoginResponse loginResponce = loginManager.getLoginByManagedSys(distinguishedName, managedSysId);

        if (loginResponce.getStatus() == ResponseStatus.FAILURE) {
//            log("AUTHENTICATION", "AUTHENTICATION", "FAIL",
//                    "MATCHING IDENTITY NOT FOUND", domainId, null, principal,
//                    null, null, clientIP, nodeIP);
            throw new AuthenticationException(
                    AuthenticationConstants.RESULT_INVALID_LOGIN);
        }
        Login lg = loginResponce.getPrincipal();
        UserEntity user = this.userManager.getUser(lg.getUserId());

        // try to login to AD with this user
        LdapContext tempCtx = connect(distinguishedName, password, host, protocol);
        if (tempCtx == null) {
//            log("AUTHENTICATION", "AUTHENTICATION", "FAIL",
//                    "RESULT_INVALID_PASSWORD", domainId, null, principal, null,
//                    null, clientIP, nodeIP);
            // update the auth fail count
            if (attrValue != null && attrValue.length() > 0) {

                int authFailCount = Integer.parseInt(attrValue);
                // increment the auth fail counter
                int failCount = 0;
                if (lg.getAuthFailCount() != null) {
                    failCount = lg.getAuthFailCount().intValue();
                }
                failCount++;
                lg.setAuthFailCount(failCount);
                lg.setLastAuthAttempt(new Date(System.currentTimeMillis()));
                if (failCount >= authFailCount) {
                    // lock the record and save the record.
                    lg.setIsLocked(1);
                    loginManager.saveLogin(lg);

                    // set the flag on the primary user record
                    user.setSecondaryStatus(UserStatusEnum.LOCKED);
                    userManager.updateUser(user);

//                    log("AUTHENTICATION", "AUTHENTICATION", "FAIL",
//                            "ACCOUNT_LOCKED", domainId, null, principal, null,
//                            null, clientIP, nodeIP);
                    throw new AuthenticationException(
                            AuthenticationConstants.RESULT_LOGIN_LOCKED);
                } else {
                    // update the counter save the record
                    loginManager.saveLogin(lg);
//                    log("AUTHENTICATION", "AUTHENTICATION", "FAIL",
//                            "INVALID_PASSWORD", domainId, null, principal,
//                            null, null, clientIP, nodeIP);

                    throw new AuthenticationException(
                            AuthenticationConstants.RESULT_INVALID_PASSWORD);
                }
            } else {
                log.debug("No auth fail password policy value found");
                throw new AuthenticationException(
                        AuthenticationConstants.RESULT_INVALID_PASSWORD);

            }

        }

        if (user.getStatus() != null) {
            if (user.getStatus().equals(UserStatusEnum.PENDING_START_DATE)) {
                if (!pendingInitialStartDateCheck(user, curDate)) {
//                    log("AUTHENTICATION", "AUTHENTICATION", "FAIL",
//                            "INVALID USER STATUS", domainId, null, principal,
//                            null, null, clientIP, nodeIP);
                    throw new AuthenticationException(
                            AuthenticationConstants.RESULT_INVALID_USER_STATUS);
                }
            }
            if (!user.getStatus().equals(UserStatusEnum.ACTIVE)
                    && !user.getStatus().equals(
                    UserStatusEnum.PENDING_INITIAL_LOGIN)) {
                // invalid status
//                log("AUTHENTICATION", "AUTHENTICATION", "FAIL",
//                        "INVALID USER STATUS", domainId, null, principal, null,
//                        null, clientIP, nodeIP);
                throw new AuthenticationException(
                        AuthenticationConstants.RESULT_INVALID_USER_STATUS);
            }
            this.checkSecondaryStatus(user);
        }

        int pswdResult = passwordExpired(lg, curDate);
        if (pswdResult == AuthenticationConstants.RESULT_PASSWORD_EXPIRED) {
//            log("AUTHENTICATION", "AUTHENTICATION", "FAIL", "PASSWORD_EXPIRED",
//                    domainId, null, principal, null, null, clientIP, nodeIP);
            throw new AuthenticationException(
                    AuthenticationConstants.RESULT_PASSWORD_EXPIRED);
        }
        Integer daysToExp = setDaysToPassworExpiration(lg, curDate, sub, null);
        if (daysToExp != null) {
            sub.setDaysToPwdExp(0);
            if (daysToExp > -1)
                sub.setDaysToPwdExp(daysToExp);
        }

        // update the login and user records to show this authentication
        lg.setLastAuthAttempt(curDate);

        // move the current login to prev login fields
        lg.setPrevLogin(lg.getLastLogin());
        lg.setPrevLoginIP(lg.getLastLoginIP());

        // assign values to the current login
        lg.setLastLogin(curDate);
        lg.setLastLoginIP(clientIP);

        // lg.setLastAuthAttempt(new Date(System.currentTimeMillis()));
        // lg.setLastLogin(new Date(System.currentTimeMillis()));

        lg.setAuthFailCount(0);
        lg.setFirstTimeLogin(0);

        log.info("Good Authn: Login object updated.");

        loginManager.saveLogin(lg);

        // check the user status
        if (user.getStatus() != null) {

            if (user.getStatus().equals(UserStatusEnum.PENDING_INITIAL_LOGIN) ||
                    // after the start date
                    user.getStatus().equals(UserStatusEnum.PENDING_START_DATE)) {

                user.setStatus(UserStatusEnum.ACTIVE);
                userManager.updateUser(user);
            }
        }

        // Successful login
        sub.setUserId(lg.getUserId());
        sub.setPrincipal(distinguishedName);
        sub.setSsoToken(token(lg.getUserId(), tokenParam));
        setResultCode(lg, sub, curDate, null);

        // send message into to audit log

//        log("AUTHENTICATION", "AUTHENTICATION", "SUCCESS", null, domainId,
//                user.getId(), distinguishedName, null, null, clientIP,
//                nodeIP);

        return sub;
    }

    public LdapContext connect(String userName, String password, String host, String protocol) {

        // LdapContext ctxLdap = null;
        Hashtable<String, String> envDC = new Hashtable();

        System.setProperty("javax.net.ssl.trustStore", keystore);

        log.info("Connecting to ldap using principal=" + userName);

        envDC.put(Context.PROVIDER_URL, host);
        envDC.put(Context.INITIAL_CONTEXT_FACTORY,
                "com.sun.jndi.ldap.LdapCtxFactory");
        envDC.put(Context.SECURITY_AUTHENTICATION, "simple"); // simple
        envDC.put(Context.SECURITY_PRINCIPAL, userName); // "administrator@diamelle.local"
        envDC.put(Context.SECURITY_CREDENTIALS, password);
        if (protocol != null && protocol.equalsIgnoreCase("SSL")) {
            envDC.put(Context.SECURITY_PROTOCOL, protocol);
        }

        try {
            return (new InitialLdapContext(envDC, null));
        } catch (NamingException ne) {
            log.info(ne.getMessage());
            return null;

        }
    }

    private NamingEnumeration search(LdapContext ctx, String searchValue, String dn, String searchFilter, String baseDn) {
        SearchControls searchCtls = new SearchControls();

        // Specify the attributes to returned
        String returnedAtts[] = {dn};
        searchCtls.setReturningAttributes(returnedAtts);

        // Specify the search scope
        try {
            searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);

            String searchFilter_ = searchFilter.replace("?", searchValue);


            log.debug("Search Filter=" + searchFilter);
            log.debug("BaseDN=" + baseDn);

            return ctx.search(baseDn, searchFilter_, searchCtls);
        } catch (NamingException ne) {
            ne.printStackTrace();
        }
        return null;

    }

    private String getDN(NamingEnumeration nameEnum) {
        String uid = null;

        try {
            while (nameEnum.hasMoreElements()) {
                SearchResult sr = (SearchResult) nameEnum.next();
                Attributes attrs = sr.getAttributes();
                if (attrs != null) {
                    uid = (String) attrs.get("uid").get();
                    log.info("getDN: uid=" + uid);
                    if (uid != null) {
                        return uid;
                    }
                }
            }
        } catch (NamingException ne) {
            ne.printStackTrace();
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openiam.idm.srvc.auth.spi.LoginModule#logout(java.lang.String,
     * java.lang.String, java.lang.String)
     */
    /*
    public void logout(String securityDomain, String principal,
            String managedSysId) {

        log("AUTHENTICATION", "LOGOUT", "SUCCESS", null, securityDomain, null,
                principal, null, null, null, null);

    }
    */

    /* supporting methods */


}
