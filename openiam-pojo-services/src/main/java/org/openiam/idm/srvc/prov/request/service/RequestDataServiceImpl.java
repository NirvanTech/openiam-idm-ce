package org.openiam.idm.srvc.prov.request.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.ws.ResponseCode;
import org.openiam.idm.srvc.mngsys.dto.ManagedSys;
import org.openiam.idm.srvc.mngsys.dto.ApproverAssociation;
import org.openiam.idm.srvc.mngsys.service.ApproverAssociationDAO;
import org.openiam.idm.srvc.mngsys.service.ManagedSystemDataService;
import org.openiam.idm.srvc.msg.service.MailService;
import org.openiam.idm.srvc.prov.request.dto.ProvisionRequest;
import org.openiam.idm.srvc.prov.request.dto.RequestApprover;
import org.openiam.idm.srvc.prov.request.dto.RequestUser;
import org.openiam.idm.srvc.prov.request.dto.SearchRequest;
import org.openiam.idm.srvc.user.dto.Supervisor;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.service.UserDataService;

/*
 * Service implementation to manage provisioning requests
 */
public class RequestDataServiceImpl implements RequestDataService {
	private static final Log log = LogFactory.getLog(RequestDataServiceImpl.class);
	

	protected ProvisionRequestDAO requestDao;
	protected ManagedSystemDataService  managedResource;
	protected ApproverAssociationDAO resourceApproverDao;
	protected UserDataService userManager;
	protected MailService mailSender;
	
	protected String defaultSender;
	protected String subjectPrefix;
	
	
	public void addRequest(ProvisionRequest request) {
		if (request == null) {
			throw new NullPointerException("request is null");
		}
		requestDao.add(request);

	}

	public ProvisionRequest getRequest(String requestId) {
		if (requestId == null) {
			throw new NullPointerException("requestId is null");
		}
		return requestDao.findById(requestId);
	}

	public void removeRequest(String requestId) {
		if (requestId == null) {
			throw new NullPointerException("requestId is null");
		}
		ProvisionRequest request = new ProvisionRequest();
		request.setRequestId(requestId);
		requestDao.remove(request);

	}

	public List<ProvisionRequest> search(SearchRequest search) {
		
		List<ProvisionRequest> reqList = requestDao.search(search);
		if (reqList == null || reqList.size() == 0)
			return null;
		
		return reqList;
	}
	
	public List<ProvisionRequest> requestByApprover(String approverId, String status) {
		List<ProvisionRequest> reqList = requestDao.findRequestByApprover(approverId, status);
		if (reqList == null || reqList.size() == 0) {
			return null;
		}
		return reqList;
	}

	public void setRequestStatus(String requestId, String approverId, String status) {
		if (requestId == null) {
			throw new NullPointerException("requestId is null");
		}
		if (approverId == null) {
			throw new NullPointerException("userId is null");
		}
		if (status == null) {
			throw new NullPointerException("status is null");
		}
	 	ProvisionRequest request = requestDao.findById(requestId);
		request.setStatus(status);
		request.setStatusDate(new Date(System.currentTimeMillis()));
		
	
		requestDao.update(request);
	}

	public void updateRequest(ProvisionRequest request) {
		if (request == null) {
			throw new NullPointerException("request is null");
		}
		requestDao.update(request);
	}

	public ProvisionRequestDAO getRequestDao() {
		return requestDao;
	}

	public void setRequestDao(ProvisionRequestDAO requestDao) {
		this.requestDao = requestDao;
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.prov.request.service.RequestDataService#nextApproverForRequest(java.lang.String, java.lang.String)
	 */
	public List<User> nextApproverForRequest(String requestId,	String resourceName) {
		// get the current approval level for the request
		// get the resource approver for the level
		// look at the approver type
		// -- if its SUPERVISOR - LOOK UP THE SUPERVISOR ON THE REQUEST
		// -- if its a group - look up the users in the
		// -- otherwise its an individual user.
		

		return null;
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.prov.request.service.RequestDataService#approve()
	 */
	public void approve() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.prov.request.service.RequestDataService#initiateRequest(org.openiam.idm.srvc.prov.request.dto.ProvisionRequest)
	 */
	public ProvisionRequest initiateRequest(ProvisionRequest request, Supervisor supervisor) {
		log.info("initiateRequest called.");
		
		Set<RequestApprover> reqApproverSet = null;
		RequestApprover reqApprover = null;
		
		if (request == null) {
			throw new NullPointerException("request is null");
		}
		
		// get the request type
		String reqType = request.getRequestType();
		log.info("Request type:" + reqType);
		
		// look up the approver for this type of request and level
		List<ApproverAssociation> approverList = getApproversByRequestType(reqType, 1);
		if (approverList != null) {
			reqApproverSet = getApprover(approverList, supervisor);
			if (reqApproverSet != null) {
				request.setRequestApprovers(reqApproverSet);
				// get the first approver
				Iterator<RequestApprover> it =  reqApproverSet.iterator();
				if (it.hasNext()) {
					reqApprover  = it.next();
				}
			}
		}
		
	
		// create the request
		requestDao.add(request);
		
		// notify the approver
		sendNotification(reqApprover, request.getRequestReason(), request.getRequestorId());
		
		return request;
		
	}
	
	public List<ApproverAssociation> getApproversByRequestType(String requestType, int level) {
		return resourceApproverDao.findApproverTypeRequestType(requestType, level);
	}
	
	private void sendNotification(RequestApprover reqApprover, String reason, String requestorId) {
		
		String approverId = reqApprover.getApproverId();
		User approverDetail = userManager.getUserWithDependent(approverId, false);
		User requestor = userManager.getUserWithDependent(requestorId, false);
		
		// TODO Move this into a template engine 
		
		String msg = "A request for " + reason + " by " + requestor.getFirstName() + " " + requestor.getLastName() + 
			 " is pending approval \n" +
			 "Please log into the self-service application to process this request.\n" +
			 "http://localhost:8080/selfservice .\n";
		
		mailSender.send(this.defaultSender,
				approverDetail.getEmail(), 
				this.getSubjectPrefix() + " " + reason + " Request", 
				msg);
		
		
	}
	
	private Set<RequestApprover> getApprover(List<ApproverAssociation> approverList,	Supervisor supervisor) {
		Set<RequestApprover> reqApproverList = new HashSet<RequestApprover>();
		
		// look at the first approver to figure the type of approver 
		if (approverList == null || approverList.isEmpty()) {
			 return null;
		}
		ApproverAssociation approver = approverList.get(0);
		String assocType = approver.getAssociationType();
		if (assocType == null) {
			throw new IllegalArgumentException("Approver association is not defined.");
		}
		
		
		if (assocType.equalsIgnoreCase("SUPERVISOR")) {

			String supervisorUserId = supervisor.getSupervisor().getUserId();
			RequestApprover app = new RequestApprover();
			
			app.setApproverId(supervisorUserId);
			app.setApproverType("SUPERVISOR");
			app.setApproverLevel(approver.getApproverLevel());
			reqApproverList.add(app);
			return reqApproverList;
		}
		if (assocType.equalsIgnoreCase("GROUP") || 
				assocType.equalsIgnoreCase("ROLE")	) {

			for ( ApproverAssociation assoc : approverList) {
				RequestApprover app = new RequestApprover();
				app.setApproverType(assocType);
				app.setApproverId(assoc.getApproverUserId());
				app.setApproverLevel(app.getApproverLevel());
				reqApproverList.add(app);
			}

			return reqApproverList;
		}

		return null;
		
	}
	


	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.prov.request.service.RequestDataService#reject()
	 */
	public void reject() {
		// TODO Auto-generated method stub
		
	}

	public ManagedSystemDataService getManagedResource() {
		return managedResource;
	}

	public void setManagedResource(ManagedSystemDataService managedResource) {
		this.managedResource = managedResource;
	}

	public UserDataService getUserManager() {
		return userManager;
	}

	public void setUserManager(UserDataService userManager) {
		this.userManager = userManager;
	}


	public String getDefaultSender() {
		return defaultSender;
	}

	public void setDefaultSender(String defaultSender) {
		this.defaultSender = defaultSender;
	}

	public String getSubjectPrefix() {
		return subjectPrefix;
	}

	public void setSubjectPrefix(String subjectPrefix) {
		this.subjectPrefix = subjectPrefix;
	}

	public MailService getMailSender() {
		return mailSender;
	}

	public void setMailSender(MailService mailSender) {
		this.mailSender = mailSender;
	}

	public ApproverAssociationDAO getResourceApproverDao() {
		return resourceApproverDao;
	}

	public void setResourceApproverDao(ApproverAssociationDAO resourceApproverDao) {
		this.resourceApproverDao = resourceApproverDao;
	}



}
