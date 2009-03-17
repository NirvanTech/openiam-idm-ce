/**
 * ------------------------------------------------------------------------------
 * Title: UserAction
 * Author: LD 04-09-2004
 * Overview:Handles all functions like adding , updating, deleting and viewing
 * list of users.
 * ------------------------------------------------------------------------------
 * Copyright (c) 2000-2004 Diamelle Inc. All Rights Reserved.
 *
 * This SOURCE CODE FILE, which has been provided by Diamelle Technologies as part
 * of a Diamelle Software product for use ONLY by licensed users of the product,
 * includes CONFIDENTIAL and PROPRIETARY information of Diamelle Technologies.
 *
 * This code or parts or derivatives of it cannot be used for any commercial
 * products without written permission from Diamelle Technologies.
 *
 * USE OF THIS SOFTWARE IS GOVERNED BY THE TERMS AND CONDITIONS
 * OF THE LICENSE STATEMENT FURNISHED WITH THE PRODUCT.
 *
 * IN PARTICULAR, YOU WILL INDEMNIFY AND HOLD Diamelle Technologies, ITS
 * RELATED COMPANIES AND ITS SUPPLIERS, HARMLESS FROM AND AGAINST ANY
 * CLAIMS OR LIABILITIES ARISING OUT OF THE USE, REPRODUCTION, OR
 * DISTRIBUTION OF YOUR PROGRAMS, INCLUDING ANY CLAIMS OR LIABILITIES
 * ARISING OUT OF OR RESULTING FROM THE USE, MODIFICATION, OR
 * DISTRIBUTION OF PROGRAMS OR FILES CREATED FROM, BASED ON, AND/OR
 * DERIVED FROM THIS SOURCE CODE FILE.
 * ------------------------------------------------------------------------------
 * CHANGE CONTROL:
 *
 * ------------------------------------------------------------------------------
 */

package org.openiam.webadmin.user;


import org.openiam.webadmin.busdel.base.*;
import org.openiam.webadmin.busdel.security.*;
import org.openiam.webadmin.busdel.identity.*;

//import org.openiam.idm.connector.Spml2Service;


import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.secdomain.dto.SecurityDomain;
import org.openiam.idm.srvc.secdomain.service.SecurityDomainDataService;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.audit.service.IdmAuditLogDataService;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserAttribute;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.idm.srvc.continfo.dto.Address;
import org.openiam.idm.srvc.continfo.dto.ContactConstants;
import org.openiam.idm.srvc.continfo.dto.EmailAddress;
import org.openiam.idm.srvc.continfo.dto.Phone;
import org.openiam.idm.srvc.meta.dto.MetadataElement;
import org.openiam.idm.srvc.meta.service.MetadataService;


import diamelle.base.composite.Component;
import diamelle.base.composite.ComponentFactory;

import diamelle.common.status.StatusCodeValue;


import diamelle.ebc.user.*;
import diamelle.security.auth.LoginValue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;

import java.text.ParseException;
import java.util.*;
import java.util.Date;
import java.rmi.RemoteException;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.struts.action.*;
import org.apache.struts.util.LabelValueBean;
import org.apache.struts.validator.DynaValidatorForm;
import org.springframework.web.context.WebApplicationContext;



public class UserAction extends NavigationDispatchAction  {
	AuthenticatorAccess authAccess = null; 

	
	UserDataServiceAccess userDataAccess = null;
	GroupDataServiceAccess groupDataAcc = null;
	RoleDataServiceAccess roleDataAcc = null;
	ServiceAccess servDataAccess = null;

	PolicyAccess policyAccess = new PolicyAccess();
	
	MetadataService metadataSrvc = null;
	UserDataService userDataSrvc = null;
	IdmAuditLogDataService auditService = null;

	private static final Log log = LogFactory.getLog(UserAction.class);

	
	//protected Spml2Service ldapService;
	
	public UserAction() {
		try {
			authAccess = new AuthenticatorAccess();
		
			
		}catch(Exception e) {
			log.error("UserAction - Constructor failed.", e);
			e.printStackTrace();
		}
	}
	
	public void init() {
		WebApplicationContext webCtx = getWebApplicationContext();
		auditService =  (IdmAuditLogDataService)webCtx.getBean("auditDataService");
		userDataSrvc = (UserDataService)webCtx.getBean("userManager");
		metadataSrvc = (MetadataService)webCtx.getBean("metadataService");
		
	}

	
	public ActionForward userForm ( ActionMapping mapping, 
			ActionForm form, HttpServletRequest request, 
			HttpServletResponse res ) throws IOException, ServletException {

        try {
           init();
           
           HttpSession session = request.getSession();
            DynaValidatorForm userForm = (DynaValidatorForm)form;
           userForm.set("personId", "" );
           userForm.set("country","USA");
           
                      
        } catch(Exception e) {
        	log.error("UserAction:userForm", e);
        }

        return mapping.findForward("addForm");
    }
	
	public ActionForward showNewUserForm(ActionMapping mapping, 
			ActionForm form, HttpServletRequest request, 
			HttpServletResponse res) throws  IOException, ServletException {
		

		init();


       try {          
           HttpSession session = request.getSession();
           loadStaticData(session, this.getServletContext());
           
           DynaValidatorForm userForm = (DynaValidatorForm)form;
           userForm.set("personId", "" );
           String userTypeId = (String)userForm.get("typeId");
           if (userTypeId != null && userTypeId.length()>0) {
        	   MetadataElement[] elementAry = metadataSrvc.getMetadataElementByType(userTypeId);
        	   request.setAttribute("metadata", elementAry);

           }
           request.setAttribute("typeId", userTypeId);
           
           WebApplicationContext webContext =  getWebApplicationContext();
      	   groupDataAcc = new GroupDataServiceAccess(webContext);
      	   request.setAttribute("groupLabelList", groupDataAcc.getAllGroupListAsLabels());
      	   
           
        } catch(Exception e) {
            e.printStackTrace();
        }
       return (mapping.findForward("success"));
	
	}
	


	
   /** 
     * Creates a new User or Saves a Users details if its an existing user.
     */
     public ActionForward saveUser ( ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse res ) throws IOException, ServletException {
        
    	 init();
    	 
    	 WebApplicationContext webContext =  getWebApplicationContext();
    	 groupDataAcc = new GroupDataServiceAccess(webContext);
    	 
    	 
    	 
    	User userData = new User();
        Address adr = new Address(); 
        EmailAddress email = new EmailAddress(); 
        Phone cellPhone = new Phone(); 
        Phone workPhone = new Phone();
        Phone faxPhone = new Phone();
        String personId = null;
      //  String submit = null;
        String logMsg = null;
        Component comp = ComponentFactory.create();
        
        

        try {
           ActionErrors errors = null;
           
           HttpSession session = request.getSession();          
           DynaValidatorForm userForm = (DynaValidatorForm)form;

           // is actually the personId
           personId = (String) userForm.get("personId");
           if (personId != null)
            request.setAttribute("personId", personId);


 		  userData = getUserDetails(userData, userForm);
          formToAddress(adr,userForm);
          formToEmail(email,userForm);
          formToPhone(workPhone,userForm,"WORK");
          formToPhone(cellPhone,userForm,"CELL");
          formToPhone(faxPhone,userForm,"FAX");

           if (personId != null && personId.length() > 0){
     		   userData.setUserId(personId);
     		   
     		   if (userForm.get("submit").equals("Unlock User")) {
     		   		//userData.setStatusId("");
     		   }
     		   if (userForm.get("submit").equals("Blacklist User")){
     		   		userData.setStatus("BLACK LISTED");
     		   		userForm.set("userStatus","BLACK LISTED");
     		   		logMsg = "User id=" + personId + " BLACK LISTED";
     		   }
     		   if (userForm.get("submit").equals("Delete User")){
 		   		userData.setStatus("DELETED");
 		   		userForm.set("userStatus","DELETED");
 		   		logMsg = "User id=" + personId + " DELETED";
     		   }
     		   
     		  // update an existing user record
     		  userDataSrvc.updateUser(userData);
               // LOG the event
               if (logMsg == null) {
               	logMsg = "User id=" + personId + " UPDATED";
               }
               
              
   	//		String actionStatus, String attributesChanges, String clientId,
   	//		String host, int linkSequence, String linkedLogId, String logHash,
   	//		, String loginId, String objectName,
   	//		, String reason, String reasonDetail,
   	//		String reqUrl, String resourceName, String serviceId, 
               
            IdmAuditLog log = new IdmAuditLog(new Date(System.currentTimeMillis()), "UPDATE",
            		   "SUCCESS", null, personId, 
            		   request.getRemoteHost(), 0, null, "00",
            		   null,(String)session.getAttribute("login"), Class.forName("org.openiam.idm.srvc.user.dto.User").getName(),
            		   personId, "USER_UPDATE", logMsg,
            		   request.getRequestURI(),"USER_SERVICE", "IDM", (String)session.getAttribute("userId"));                
               auditService.addLog(log);
                       


               
               
         //      AuditLogAccess.logEvent(logMsg, request.getRemoteHost(), 
         //      		(String)session.getAttribute("userId"),
         //      		(String)session.getAttribute("login"),"IDM");
               
               adr.setParentId(personId);
               adr.setParentType(ContactConstants.PARENT_TYPE_USER);
               userDataSrvc.updateAddress(adr); 
               
               email.setParentId(personId);
               email.setParentType(ContactConstants.PARENT_TYPE_USER);
               userDataSrvc.updateEmailAddress(email);
               
               workPhone.setParentId(personId);
               workPhone.setParentType(ContactConstants.PARENT_TYPE_USER);
               cellPhone.setParentId(personId);
               cellPhone.setParentType(ContactConstants.PARENT_TYPE_USER);
               faxPhone.setParentId(personId);
               faxPhone.setParentType(ContactConstants.PARENT_TYPE_USER);
               userDataSrvc.updatePhone(workPhone);
               userDataSrvc.updatePhone(cellPhone);
               userDataSrvc.updatePhone(faxPhone);

	              // get the attributes
               updateAttributes(request,personId, userDataSrvc, userData.getMetadataTypeId());

     			//WebApplicationContext webCtx = getWebApplicationContext();
 /*     			ldapService = (Spml2Service)webCtx.getBean("adService");
      			
      			ldapService.modify("", 
      						userData.getFirstName() + " " +userData.getLastName(), 
      						userData.getLastName(), userData.getFirstName(),
      						"","", userData.getTitle(),
      						adr.getState(), adr.getAddress1(), adr.getPostalCode());
*/      			
                
           } else {
           	  
           	  // new user
           	  // validate login and password fields
           	  String login = (String)userForm.get("login");
           	  String password = (String)userForm.get("password");
           	  String confPassword = (String)userForm.get("confirmpassword");
           	  
           	  // validation cannot be done in the form as there 2 different sets 
           	  // of fields in the view and edit
      	  	  errors = validateLogin(errors,login,password, confPassword);    	  	  
      	  	  errors = isDuplicate(errors, login, (String) userForm.get("service"));

      	  	  if (errors != null) {     	  	 
      	  		  saveErrors(request, errors);
      	  	  }
      	  	                    
              if (errors == null) {
              	  String createdId = (String)session.getAttribute("userId");

               	  userData.setCreatedBy(createdId);
              	  userData.setCreateDate(new Timestamp(System.currentTimeMillis()));
	              
              	  userDataSrvc.addUser(userData);
              	  
          	  
 	                                             
				  request.setAttribute("personId", userData.getUserId());

	              // new - sas- 12-13-2003
	              personId = userData.getUserId();
	              adr.setParentId(personId);
	              adr.setParentType(ContactConstants.PARENT_TYPE_USER);
	              userDataSrvc.updateAddress(adr);
	               
	              email.setParentId(personId);
	              email.setParentType(ContactConstants.PARENT_TYPE_USER);
	              userDataSrvc.updateEmailAddress(email);
	               
	              workPhone.setParentId(personId);
	              workPhone.setParentType(ContactConstants.PARENT_TYPE_USER);
	              cellPhone.setParentId(personId);
	              cellPhone.setParentType(ContactConstants.PARENT_TYPE_USER);
	              faxPhone.setParentId(personId);
	              faxPhone.setParentType(ContactConstants.PARENT_TYPE_USER);
	              userDataSrvc.updatePhone(workPhone);
	              userDataSrvc.updatePhone(cellPhone);
	              userDataSrvc.updatePhone(faxPhone);

             	  LoginValue lv = new LoginValue();
	              lv.setLogin( login);
	              if (password != null )
	              	lv.setPassword( password);
	              lv.setService( (String) userForm.get("service"));
	              lv.setUserId(userData.getUserId());
	              lv.setResetPassword(true);
	              //lv.setNewUser(true);
	              lv.setChangePassword(new Timestamp(System.currentTimeMillis()));
	              authAccess.addLogin(lv);
	              
	              // password is set in the login bean. need to get it out so that we
	              // can show the user.
	              lv = authAccess.getLogin((String) userForm.get("service"),login);

	              String msg = "Password has been auto set to: " + lv.getPassword();
	              request.setAttribute("msg",msg);	              
	              // save the default group
	              String group = (String)userForm.get("group");
	             // userAccess.addUserGroup(userData.getId(),group);
	              this.groupDataAcc.addUserToGroup(group,lv.getUserId());
	              
	              // get the attributes
	              addAttributes(request,personId, userDataSrvc);
	              
	      			WebApplicationContext webCtx = getWebApplicationContext();
	      		/*	ldapService = (Spml2Service)webCtx.getBean("adService");
	      			ldapService.add(lv.getLogin(), 
	      						userData.getFirstName() + " " +userData.getLastName(), 
	      						userData.getLastName(), userData.getFirstName(),
	      						"",lv.getPassword(),
	      						userData.getTitle(),
	      						adr.getState(), adr.getAddress1(), adr.getPostalCode());
	          	*/		
	      			
	      			logMsg = "User id=" + userData.getUserId() + " created";
           
	                IdmAuditLog log = new IdmAuditLog(new Date(System.currentTimeMillis()), "CREATE",
	             		   "SUCCESS", null, personId, 
	             		   request.getRemoteHost(), 0, null, "00",
	             		   null,(String)session.getAttribute("login"),Class.forName("org.openiam.idm.srvc.user.dto.User").getName(),
	             		   personId, "USER_CREATE", logMsg,
	             		   request.getRequestURI(),"USER_SERVICE", "IDM", (String)session.getAttribute("userId"));                
	                auditService.addLog(log);
	                        
	

	                
	      			
	              // log the event
	          //    AuditLogAccess.logEvent("User id=" + userData.getId() + " created",
              //    		request.getRemoteHost(),createdId,  
              //     		(String)session.getAttribute("login"),"IDM");

	     
              }
           }
           this.editUser(request, userForm, "IDENTITIES");
	              
        } catch(Exception e) {
        	e.printStackTrace();
            ActionErrors errors = new ActionErrors();
            errors.add(ActionMessages.GLOBAL_MESSAGE,new ActionMessage("error.ejb"));
            //this.ssaveErrors(request, errors);
            //saveErrors(request, errors);
            //e.printStackTrace();
        }
        return (mapping.findForward("success"));
    }
     

	private void  addAttributes(HttpServletRequest request, String personId, UserDataService userAccess ) throws RemoteException {
     	UserAttribute comp = null;
        java.util.Enumeration<String> en =  request.getParameterNames();
        if (en != null) {
     	   while (en.hasMoreElements()) {
     		   String fieldName = (String)en.nextElement();
     		   // look for our special marker '*'
     		   if (fieldName.startsWith("*")) {
     			   String value = request.getParameter(fieldName);

     			   comp = new UserAttribute();
     			   int size = fieldName.length();
     			   String elementName = fieldName.substring(1,size);
     			   int indx = elementName.indexOf("-");
     			   String name = elementName.substring(0,indx);
     			   String metadataId = elementName.substring(indx+1,--size);
     			   
     			   
     			   comp.setName(name);
     			   comp.setUserId(personId);
     			   comp.setValue(value);
     			   comp.setMetadataElementId(metadataId);
     			   userDataSrvc.updateAttribute(comp);
     		   }
     	   }
        }     	
     }
     
     private void updateAttributes (HttpServletRequest request, String personId, 
     		UserDataService userAccess,String typeId ) throws RemoteException {
     	// for each item in metadata, check if we have a value.
     	// if we do, then update it. if we don't add it.

 		init();
		
    	MetadataElement[] elementAry = metadataSrvc.getMetadataElementByType(typeId);
     	
    	 //Map metadataMap =  metaAccess.getMetadataByType(typeId);
     	
    	 Map userAttr = userAccess.getAllAttributes(personId);
     	
    	 if (elementAry != null && elementAry.length > 0) {
    		 int size = elementAry.length;
    		 for (int i=0; i<size; i++) {
    	 
     
    		MetadataElement elementVal = elementAry[i];
     	 	String webParamName =  "*" + elementVal.getAttributeName() + "-" + elementVal.getMetadataElementId();
     	 	String webParamValue = request.getParameter(webParamName);
     	 	UserAttribute comp = getAttribute(userAttr, elementVal.getAttributeName());

     	 	if (comp != null ) {
     	 		// update
     	 		if (webParamValue == null) {
     	 			comp.setValue("");
     	 		}else {
     	 			comp.setValue(webParamValue); 
     	 		}
     	 	}else {
     	 		// new
      	 	   if (comp == null) {
     	 	   		comp = new UserAttribute();
     	 	   }
     	 	   if (elementVal != null) {
	  			   comp.setName(elementVal.getAttributeName());
	 			   comp.setUserId(personId);
	 			   if (webParamValue == null) {
	 			   	comp.setValue("");
	 			   }else {
	 			   	comp.setValue(webParamValue);
	 			   }
	 			   comp.setMetadataElementId(elementVal.getMetadataElementId());
     	 	   }
      	 	}
     	 	userDataSrvc.updateAttribute(comp);   	 	
     	}
    }
     	
     }
     private UserAttribute getAttribute(Map userAttr, String attrName) {
     	if (userAttr == null || userAttr.isEmpty()) {
          	return null;
     	}
          	
		Collection col = userAttr.values();
		Iterator it = col.iterator();
		while (it.hasNext()) {
			UserAttribute attr = (UserAttribute)it.next();
			if (attr.getName().equals(attrName)) {
				return attr;
			}
		}
		return null;
     }
     
     private ActionErrors isDuplicate(ActionErrors errors,String login, String serviceId) {
     	try {

     	LoginValue val = authAccess.getLogin(serviceId, login);

     	if (val != null) {
  	  		if (errors == null) {
  	  			errors = new ActionErrors();
  	  		}
  	        errors.add(ActionMessages.GLOBAL_MESSAGE,new ActionMessage("error.ejb"));

     	}
     	}catch(Exception e) {
     		e.printStackTrace();
     	}
     	return errors; 	
     }

     private  ActionErrors validateLogin(ActionErrors errors, String login, 
     							String password, String confPassword) {
 
	  	
     	 if (login == null || login.length() ==0) {
  	  		if (errors == null) {
  	  			errors = new ActionErrors();
  	  		}
  	        errors.add(ActionMessages.GLOBAL_MESSAGE,new ActionMessage("error.ejb"));

  	 	}
      	if (password != null ) {
	     	if (!password.equals(confPassword)) {
	  	  		if (errors == null) {
	  	  			errors = new ActionErrors();
	  	  		}
	  	        errors.add(ActionMessages.GLOBAL_MESSAGE,new ActionMessage("error.ejb"));

	  	 	}
     	}
     	return errors;

     }
 
     /**
      * Forwards it to the input page while validations are done
      * according to the add or edit function 
      */
     public ActionForward userValidate ( ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse res ) throws IOException, ServletException {

        try {

           String personId = request.getParameter("personId");
           DynaValidatorForm userForm = (DynaValidatorForm) form;
           
           if (personId != null && personId.length() > 0) {
               userForm.set("personId", personId);
               Object phoneList = getTabDetail("PHONE", personId);
               request.setAttribute("detailView", phoneList);
               request.setAttribute("personId",personId);
               request.setAttribute("tabOptions", setActiveTab("IDENTITIES"));
           }
        } catch(Exception e) {
             e.printStackTrace();
        }
        return (mapping.findForward("success"));
    }


    /**
     * Deletes a User and all its dependencies
     */
    public ActionForward deleteUser ( ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse res ) throws IOException, ServletException {

        try {
           HttpSession session = request.getSession();
           String personId = request.getParameter("personId");
           UserAccess userAccess = new UserAccess();
           
           UserData ud = userAccess.getUser(personId);
           if (ud != null) {
           	ud.setStatusId("DELETED");
           	userAccess.saveUser(ud);
           	
 /*			WebApplicationContext webCtx = getWebApplicationContext();
  			ldapService = (Spml2Service)webCtx.getBean("ldapService");
  			
  			ldapService.delete(ud.getFirstName() + " " +ud.getLastName());
*/           	
            AuditLogAccess.logEvent("User id=" + personId + " has been deleted.",
              		request.getRemoteHost(),(String)session.getAttribute("userId"),  
               		(String)session.getAttribute("login"),"IDM");
           }
 
        } catch(Exception e) {
            e.printStackTrace();
        }
    	//return (mapping.findForward("success"));
        return (mapping.findForward("search"));
    }




    /**
     * Retrieves a list of Login information for a User
     */
    public ActionForward identities ( ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse res ) throws IOException, ServletException {
    	String mode = null;
    	List statusList = null;
    	HttpSession session = request.getSession();
        try {

           String personId = request.getParameter("personId");
           mode= request.getParameter("mode");
           if (mode == null)
           	mode = "VIEW";
           
           Object identities = getTabDetail("IDENTITIES", personId);
           request.setAttribute("detailView", identities);

           DynaValidatorForm userForm = (DynaValidatorForm) form;
           this.editUser(request, userForm, "Identities");

          	statusList = (List)session.getAttribute("statusList");
        	
        	if (statusList == null) {
	            statusList = getUserStatusList();
	            session.setAttribute("statusList", statusList);        		
        	}
           
        } catch(Exception e) {
            e.printStackTrace();
        }
        if (mode.equals("VIEW"))
    		return (mapping.findForward("success_view"));

        return (mapping.findForward("success"));
    }

    
    public ActionForward smartcard ( ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse res ) throws IOException, ServletException {
    	String mode = null;
    	List statusList = null;
    	HttpSession session = request.getSession();
        try {

           String personId = request.getParameter("personId");
           mode= request.getParameter("mode");
           if (mode == null)
           	mode = "VIEW";
           
           String task = request.getParameter("task");
           if (task != null) {
        	   request.setAttribute("task", task);
           }

           DynaValidatorForm userForm = (DynaValidatorForm) form;
           this.editUser(request, userForm, "Smart Card");

          	statusList = (List)session.getAttribute("statusList");
        	
        	if (statusList == null) {
	            statusList = getUserStatusList();
	            session.setAttribute("statusList", statusList);        		
        	}
           
        } catch(Exception e) {
            e.printStackTrace();
        }
        if (mode.equals("VIEW"))
    		return (mapping.findForward("success_view"));

        return (mapping.findForward("success"));
    }

    public ActionForward physaccess ( ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse res ) throws IOException, ServletException {
    	String mode = null;
    	List statusList = null;
    	HttpSession session = request.getSession();
        try {

           String personId = request.getParameter("personId");
           mode= request.getParameter("mode");
           if (mode == null)
           	mode = "VIEW";
           
           DynaValidatorForm userForm = (DynaValidatorForm) form;
           this.editUser(request, userForm, "Phys Access");

          	statusList = (List)session.getAttribute("statusList");
        	
        	if (statusList == null) {
	            statusList = getUserStatusList();
	            session.setAttribute("statusList", statusList);        		
        	}
           
        } catch(Exception e) {
            e.printStackTrace();
        }
        if (mode.equals("VIEW"))
    		return (mapping.findForward("success_view"));

        return (mapping.findForward("success"));
    }
    
    
    
    /**
     * Retrieves a list of Attributes for a User
     */
    public ActionForward attributes ( ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse res ) throws IOException, ServletException {
       	String mode = null;
        try {
           String personId = request.getParameter("personId");
           mode= request.getParameter("mode");
           if (mode == null)
           	mode = "VIEW";
           
           Object attributes = getTabDetail("ATTRIBUTE", personId);
           request.setAttribute("detailView", attributes);

           DynaValidatorForm userForm = (DynaValidatorForm) form;
           this.editUser(request, userForm, "Attributes");

        } catch(Exception e) {
            e.printStackTrace();
        }
        if (mode.equals("VIEW"))
    		return (mapping.findForward("success_view"));

        return (mapping.findForward("success"));
    }


    

    /**
     * Retrieves a list of Groups and the Groups to which a User belongs
     */
    public ActionForward userGroup ( ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse res ) throws IOException, ServletException {
       	String mode = null;
        try {
        	
       	 WebApplicationContext webContext =  getWebApplicationContext();
    	 groupDataAcc = new GroupDataServiceAccess(webContext);
        	
           String personId = request.getParameter("personId");
           mode= request.getParameter("mode");
           if (mode == null)
           	mode = "VIEW";
           
           Object groupList = groupDataAcc.getAllGroups(true);

           request.setAttribute("detailView", groupList);
           request.setAttribute("groupList", groupList);

           List<Group> userList = groupDataAcc.getUserGroups(personId);
           request.setAttribute("userList",userList);

           DynaValidatorForm userForm = (DynaValidatorForm) form;
           this.editUser(request, userForm, "Group");


        } catch(Exception e) {

            e.printStackTrace();
        }
        if (mode.equals("VIEW"))
    		return (mapping.findForward("success_view"));
        return (mapping.findForward("success"));
    }


    public ActionForward userRole ( ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse res ) throws IOException, ServletException {
       	String mode = null;
        try {
        	
       	 WebApplicationContext webContext =  getWebApplicationContext();
    	 roleDataAcc = new RoleDataServiceAccess(webContext);
    	 servDataAccess = new ServiceAccess(webContext);
        	
           String personId = request.getParameter("personId");
           mode= request.getParameter("mode");
           if (mode == null)
           	mode = "VIEW";
           
           Role[] roleAry = roleDataAcc.getAllRoles();
           Role[] userRoleAry = roleDataAcc.getUserRoles(personId);
           
           request.setAttribute("roleAry",roleAry);
           request.setAttribute("detailView", roleAry);
           request.setAttribute("userRoleAry",userRoleAry);
           
           DynaValidatorForm userForm = (DynaValidatorForm) form;
           
           this.editUser(request, userForm, "Role");


        } catch(Exception e) {

            e.printStackTrace();
        }
        if (mode.equals("VIEW"))
    		return (mapping.findForward("success_view"));
        return (mapping.findForward("success"));
    }


    public ActionForward history ( ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse res ) throws IOException, ServletException {
        //HttpSession session = request.getSession();
        String mode = null;
        
        try {
           String personId = request.getParameter("personId");
           if (personId == null) {
           	// coming here from the notes popup
           	HttpSession session = request.getSession();
           	personId = (String)session.getAttribute("personId");
           }

           Object historyList = getTabDetail("HISTORY", personId);
           request.setAttribute("detailView", historyList);

           DynaValidatorForm userForm = (DynaValidatorForm) form;
           this.editUser(request, userForm, "History");

        } catch(Exception e) {
            e.printStackTrace();
        }
        //set the mode to handle the view / edit
        mode= request.getParameter("mode");
        if (mode == null)
           	mode = "VIEW";
        if (mode.equals("VIEW"))
        	return (mapping.findForward("success_view"));
        
        return (mapping.findForward("success"));
    }


    /** Private methods **/

    /**
     *  Retrieves the information of the user
     */
    private void editUser (HttpServletRequest request, DynaValidatorForm userForm, String activeTab){
        HttpSession session = request.getSession();

		init();

        try {
           
           String personId = request.getParameter("personId");
           if (personId == null) {
           	personId = (String)session.getAttribute("personId");
           }
           if (personId == null || personId.length() == 0)
           	personId = (String) request.getAttribute("personId");
           // for use in popups where we can not easily pass a parameter
           session.setAttribute("personId",personId);

           User userData =  userDataSrvc.getUserWithDependent(personId, false);
           if (userData != null) {
              populateFormBean(userData, userForm);
              request.setAttribute("personData", userData);
              // get the phone, address and email
              Address adr = userDataSrvc.getAddressByName(personId,"DEFAULT ADR");
              EmailAddress email = userDataSrvc.getEmailAddressByName(personId,"EMAIL1");
              Phone workPhone = userDataSrvc.getPhoneByName(personId,"Desk Phone");
              Phone cellPhone = userDataSrvc.getPhoneByName(personId,"Cell Phone");
              Phone faxPhone = userDataSrvc.getPhoneByName(personId,"Fax Phone");
              addressToForm(userForm,adr);
              phoneToForm(userForm,workPhone, "WORK");
              phoneToForm(userForm,cellPhone, "CELL");
              phoneToForm(userForm,faxPhone, "FAX");
              emailToForm(userForm,email);
              // get the metadata for this user
              if (userData.getMetadataTypeId() != null) {
            	   MetadataElement[] elementAry = metadataSrvc.getMetadataElementByType(userData.getMetadataTypeId());
           	             	   
        	   request.setAttribute("metadata", elementAry);
        	   
               //	Map metadataMap = metaAccess.getMetadataByType(userData.getTypeId());
               //	request.setAttribute("metadata", metadataMap);
              	// get the attributes for the metadata
              	Map attrMap =  userDataSrvc.getAllAttributes(personId);
              	request.setAttribute("userAttr", attrMap);
              }
              // get the attributes for the metadata
                           
           }
     	 
         WebApplicationContext webContext =  getWebApplicationContext();
    	 groupDataAcc = new GroupDataServiceAccess(webContext);
    	 request.setAttribute("groupLabelList", groupDataAcc.getAllGroupListAsLabels());
         
    	 request.setAttribute("personId",personId);
         Object identities = getTabDetail(activeTab, personId);
         request.setAttribute("detailView", identities);

         request.setAttribute("tabOptions", setActiveTab(activeTab));

        } catch(Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * Retrieves the data from Form and sets in the UserData object
     */
    private User getUserDetails(User userData, DynaValidatorForm form) {
         CalendarUtil calUtil = new CalendarUtil();
         userData.setFirstName((String) form.get("firstName"));
         userData.setMiddleInit((String) form.get("middleName"));
         userData.setLastName((String) form.get("lastName"));

         //userData.setEMail((String) form.get("email"));
         userData.setUserTypeInd((String) form.get("userInd"));
         userData.setDeptName((String) form.get("dept"));
         userData.setSex((String) form.get("sex"));
         userData.setTitle((String) form.get("title"));

         userData.setLastUpdate(new Timestamp(System.currentTimeMillis()));
         //userData.setCreatedBy(");
         
         String typeId = (String) form.get("typeId");         
         if (typeId != null && typeId.length() > 0)
            userData.setMetadataTypeId(typeId);
         
         String birthdate = (String)form.get("birthday");
         if (birthdate != null && birthdate.length() > 0)
            userData.setBirthdate(calUtil.getSqlDate(birthdate, "MM-dd-yyyy"));   
         
         String companyId = (String)form.get("companyId");
         if (companyId != null && companyId.length() > 0) {
            userData.setCompanyId((String) form.get("companyId"));           
         }

         
         userData.setStatus((String) form.get("status"));  
         return userData;
    }

    private Address formToAddress(Address adr, DynaValidatorForm form) {
    	adr.setAddress1((String) form.get("address1"));
    	adr.setAddress2((String)form.get("address2"));
    	adr.setCity((String)form.get("city"));
    	adr.setIsDefault(new Integer(1));
    	adr.setDescription("DEFAULT");
    	adr.setPostalCd((String)form.get("zip"));
    	adr.setState((String)form.get("state"));
    	adr.setAddressId((String)form.get("addressId"));
    	adr.setCountry((String)form.get("country"));
    	adr.setName("DEFAULT ADR");
    	return adr;
    	
    }
    private EmailAddress formToEmail(EmailAddress email, DynaValidatorForm form) {
    	email.setEmailId((String)form.get("emailId"));
    	email.setEmailAddress((String)form.get("email"));
    	email.setDescription("DEFAULT");
    	email.setName("EMAIL1");
    	email.setIsDefault(new Integer(1));
    	return email;
    	
    }
    private Phone formToPhone(Phone ph, DynaValidatorForm form, String phoneType) {
    	if (phoneType.equals("WORK")) {
    		ph.setAreaCd((String)form.get("phone_areacd"));
    		ph.setDescription("WORK");
    		ph.setName("Desk Phone");
    		ph.setPhoneNbr((String)form.get("phone_nbr"));
    		ph.setPhoneId((String)form.get("workPhoneId"));
    	}
    	if (phoneType.equals("CELL")) {
    		ph.setAreaCd((String)form.get("cell_areacd"));
    		ph.setDescription("CELL");
    		ph.setName("Cell Phone");
    		ph.setPhoneNbr((String)form.get("cell_nbr"));
    		ph.setPhoneId((String)form.get("cellPhoneId"));
    		
    	}
    	if (phoneType.equals("FAX")) {
    		ph.setAreaCd((String)form.get("fax_areacd"));
    		ph.setDescription("FAX");
    		ph.setPhoneNbr((String)form.get("fax_nbr"));
    		ph.setName("Fax Phone");
    		ph.setPhoneId((String)form.get("faxPhoneId"));
   		
    	}
    	return ph;
    	
    }
   
    private void addressToForm(DynaValidatorForm form, Address adr) {
    	if (adr != null) {
       	form.set("address1",adr.getAddress1());
    	form.set("address2",adr.getAddress2());
    	form.set("city", adr.getCity());
    	form.set("zip",adr.getPostalCd());
    	form.set("state",adr.getState());
    	form.set("addressId",adr.getAddressId());
    	form.set("country", adr.getCountry());
    	}
   	
    }
    private void phoneToForm(DynaValidatorForm form, Phone ph, String phoneType) {
       	if (ph == null)
       		return;
    	if (phoneType.equals("WORK")) {
    		form.set("phone_areacd",ph.getAreaCd());
    		form.set("phone_nbr",ph.getPhoneNbr());
    		form.set("workPhoneId",ph.getPhoneId());
    	}
    	if (phoneType.equals("CELL")) {
    		form.set("cell_areacd",ph.getAreaCd());
    		form.set("cell_nbr",ph.getPhoneNbr());
    		form.set("cellPhoneId",ph.getPhoneId());
    		
    	}
    	if (phoneType.equals("FAX")) {
    		form.set("fax_areacd",ph.getAreaCd());
    		form.set("fax_nbr",ph.getPhoneNbr());
    		form.set("faxPhoneId",ph.getPhoneId());
   		
    	}
    	
    }
    private void emailToForm(DynaValidatorForm form,EmailAddress email) {
    if (email != null) {
       	form.set("emailId",email.getEmailId());
    	form.set("email",email.getEmailAddress());
    } 	
    }

    /**
     * Retrieves information from UserData and sets it in Form
     */
    private void populateFormBean(User ud, DynaValidatorForm uform) {
        CalendarUtil calUtil = new CalendarUtil();

      uform.set("personId", ud.getUserId());

      uform.set("firstName", ud.getFirstName());
      uform.set("middleName", ud.getMiddleInit());
      uform.set("lastName", ud.getLastName());

    //  uform.set("email", ud.getEmailAddress());
      uform.set("userInd", ud.getUserTypeInd());

      uform.set("typeId", ud.getMetadataTypeId());
      uform.set("dept", ud.getDeptName());
      uform.set("companyId", ud.getCompanyId());
      if (ud.getCompanyId() != null) { 

      }
      
      
      uform.set("sex", ud.getSex());
      Date createTime = ud.getCreateDate();
      if (createTime != null)
      	uform.set("createDate", createTime.toString());
      uform.set("createdBy", ud.getCreatedBy());

      if (ud.getBirthdate() != null) {
      	try {
      		String dt = CalendarUtil.getDateString(ud.getBirthdate(),"MM-dd-yyyy");
      		uform.set("birthday", dt);
      	}catch(ParseException pe) {
      		log.error("Parse Exception", pe);
      	}
      }


      
       uform.set("status",ud.getStatus());   
      uform.set("title", ud.getTitle());
      
    }


  
    /**
     * Sets the tabs
     */
    private List initTabOptions() {

      List l = new ArrayList();

      TabOption option = new TabOption("Group", false, "idman/user.do?method=userGroup", "usergrplist.jsp");
      l.add(option);

      option = new TabOption("Role", false, "idman/user.do?method=userRole", "userrolelist.jsp");
      l.add(option);
      
      option = new TabOption("Identities", false, "idman/user.do?method=identities", "viewidentities.jsp");
      l.add(option);

      option = new TabOption("History", false, "idman/user.do?method=history", "viewusernotes.jsp");
      l.add(option);
      
  /*    option = new TabOption("Smart Card", false, "idman/user.do?method=smartcard", "view_smartcard.jsp");
      l.add(option);      
  
      option = new TabOption("Phys Access", false, "idman/user.do?method=physaccess", "view_physicalaccess.jsp");
      l.add(option);     
*/
      
      return l;
    }

   /**
     * Sets the active tab, regenerating list each time
     */
    public List setActiveTab(String tab) {
       TabOption option = null;
       List optList = initTabOptions();
       int size = optList.size();

       for (int i=0; i<size; i++) {
         option = (TabOption) optList.get(i);


         if (option.getTitle().equalsIgnoreCase(tab))
           option.setActive(true);
         else
           option.setActive(false);
       }
       return optList;
    }
    public Object getTabDetail(String detailView,String personId) throws RemoteException {
        try {

        if (detailView.equalsIgnoreCase("PHONE")) {
          return userDataSrvc.getPhoneList(personId);
        }
        if (detailView.equalsIgnoreCase("ADDRESS")) {
          return userDataSrvc.getAddressList(personId);
        }
        if (detailView.equalsIgnoreCase("EMAIL")) {
          return userDataSrvc.getEmailAddressList(personId);
        }
        if (detailView.equalsIgnoreCase("IDENTITIES")) {
        	return authAccess.getAllLogins(personId);
        	//return authAccess.getPrincipals(personId);
        }
        if (detailView.equalsIgnoreCase("HISTORY")) {
        	return userDataSrvc.getAllNotes(personId);
        }

        } catch (Exception e) {
             e.printStackTrace();
        }
        return null;
      }

    /* Load Static data */
    
    private Vector codeListToVector(List codes) {
	    Vector vct = new Vector();
		LabelValueBean emptybean = new LabelValueBean("<Select a value>", "-1");
		vct.add(emptybean);

		if (codes != null) {
		  String codeKey;
		  String codeDesc;
		  Iterator codeIter = codes.iterator();
		
	      while(codeIter.hasNext()) {
			StatusCodeValue codeValue = (StatusCodeValue) codeIter.next();
			codeKey = codeValue.getStatusCd();
			codeDesc = codeValue.getDescription(); 
			LabelValueBean bean = new LabelValueBean(codeDesc,codeKey);
			vct.add(bean);
		  }
		} 
		
		return vct;
	  }	
	  
  
	private List getUserStatusList() throws RemoteException {
    	ArrayList newCodeList = new ArrayList();
        CodeAccess cdAccess = new CodeAccess();
        List codeList = cdAccess.getCodesByService("100","IDM","USER","en");
    
        if (codeList != null && codeList.size() > 0) {
        	newCodeList.add(new LabelValueBean("",""));
        	for (int i=0; i<codeList.size(); i++) {       		
        		StatusCodeValue val = (StatusCodeValue)codeList.get(i);
        	 	LabelValueBean label = new LabelValueBean(val.getDescription(),val.getStatusCd());
        	 	newCodeList.add(label);
        	}
        }
        return newCodeList;
    }
	
	private List getDomainList(SecurityDomain[] domainAry) {
	   	ArrayList newDomainList= new ArrayList();
    
        if (domainAry != null && domainAry.length > 0) {
        	newDomainList.add(new LabelValueBean("",""));
        	for (int i=0; i < domainAry.length; i++) {       		
        		SecurityDomain val = (SecurityDomain)domainAry[i];
        	 	LabelValueBean label = new LabelValueBean(val.getName(),val.getDomainId());
        	 	newDomainList.add(label);
        	}
        }
        
         
        return newDomainList;

		
	}
	


		
	/**
	 * Load static data that is used by the web application.
	 * Most this to application cach. 
	 */
	private void loadStaticData(HttpSession session, ServletContext servletCtx) throws RemoteException {

        	
       	if (session.getAttribute("domains") == null) {
    		SecurityDomainDataService secDomainDS = 
    			(SecurityDomainDataService)getWebApplicationContext().getBean("secDomainService");       		
    		SecurityDomain domainAry[] = secDomainDS.getAllSecurityDomains();	
    		session.setAttribute("domains", getDomainList(domainAry));
		}
	
		
		if (session.getAttribute("services") == null) {
           	// we dont have the data for the drop downs.
        	
        	this.servDataAccess = new ServiceAccess(getWebApplicationContext());
        	          	
           	List serviceList = servDataAccess.getAllServices();
           	session.setAttribute("services", servDataAccess.getServiceListAsLabels(serviceList));        	
 
           	// show the status codes if they are not already populated    	
            List statusList = getUserStatusList();
	        session.setAttribute("statusList", statusList);	        

        } 		
	}

}

