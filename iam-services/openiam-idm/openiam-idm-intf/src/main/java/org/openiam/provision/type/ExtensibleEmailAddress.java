/*
 * Copyright 2009, OpenIAM LLC 
 * This file is part of the OpenIAM Identity and Access Management Suite
 *
 *   OpenIAM Identity and Access Management Suite is free software: 
 *   you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License 
 *   version 3 as published by the Free Software Foundation.
 *
 *   OpenIAM is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   Lesser GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with OpenIAM.  If not, see <http://www.gnu.org/licenses/>. *
 */

/**
 * 
 */
package org.openiam.provision.type;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.idm.srvc.continfo.dto.Address;
import org.openiam.idm.srvc.continfo.dto.EmailAddress;

/**
 * Role object that is passed to the connector service when provisioning a role.
 * @author suneet
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExtensibleEmailAddress")
public class ExtensibleEmailAddress extends ExtensibleObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6938373330651437873L;
	
	protected EmailAddress emailAddress;
	
	public ExtensibleEmailAddress() {
        super(ExtensibleObjectType.EMAIL);
	}
	
	public ExtensibleEmailAddress(EmailAddress adr) {
		emailAddress = adr;
		this.operation = ModificationAttribute.add;
        extensibleObjectType= ExtensibleObjectType.EMAIL;
	}

	public EmailAddress getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(EmailAddress emailAddress) {
		this.emailAddress = emailAddress;
	}

    @Override
    public String toString() {
        return "ExtensibleEmailAddress{" +
                "emailAddress=" + emailAddress +
                '}';
    }
}
