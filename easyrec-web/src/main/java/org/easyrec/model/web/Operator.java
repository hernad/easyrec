/**Copyright 2010 Research Studios Austria Forschungsgesellschaft mBH
 *
 * This file is part of easyrec.
 *
 * easyrec is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * easyrec is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with easyrec.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.easyrec.model.web;


/**
 * An Operator is a registered User/Company at our easyRec Portal.
 * An Operator can have one ore more teanants assigned to.
 * <p/>
 * <p><b>Company:&nbsp;</b>
 * SAT, Research Studios Austria</p>
 * <p/>
 * <p><b>Copyright:&nbsp;</b>
 * (c) 2007</p>
 * <p/>
 * <p><b>last modified:</b><br/>
 * $Author: pmarschik $<br/>
 * $Date: 2011-02-22 14:35:41 +0100 (Di, 22 Feb 2011) $<br/>
 * $Revision: 17734 $</p>
 *
 * @author phlavac
 * @version <CURRENT PROJECT VERSION>
 * @since <PROJECT VERSION ON FILE CREATION>
 */
public class Operator {
    private String operatorId;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String company;
    private String address;
    private String apiKey;
    private String ip;
    private boolean active;
    private String creationDate;
    private int accessLevel;
    private int loginCount;
    private String lastLoginDate;

    // TODO: move to vocabulary?
    public static final String DEFAULT_API_KEY = "8ab9dc3ffcdac576d0f298043a60517a";
    public static final String DEFAULT_OPERATORID = "easyrec";
    public static final int ACCESS_LEVEL_USER = 0;
    public static final int ACCESS_LEVEL_ADMINISTRATOR = 1;

    public static final int MIN_PASSWORD_LENGTH = 5;

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getCreationDate() {
        return creationDate;
        //creationDate.substring(4,10) + ", " +
        //creationDate.substring(creationDate.length()-4,creationDate.length()) ;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public int getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(int accessLevel) {
        this.accessLevel = accessLevel;
    }

    public String getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(String lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public int getLoginCount() {
        return loginCount;
    }

    public void setLoginCount(int loginCount) {
        this.loginCount = loginCount;
    }

    public Operator(String operatorId, String password, String firstName, String lastName, String email, String phone,
                    String company, String address, String apiKey, String ip, boolean active, String creationDate,
                    int accessLevel, int loginCount, String lastLoginDate) {
        this.operatorId = operatorId;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.company = company;
        this.address = address;
        this.apiKey = apiKey;
        this.ip = ip;
        this.active = active;
        this.creationDate = creationDate;
        this.accessLevel = accessLevel;
        this.loginCount = loginCount;
        this.lastLoginDate = lastLoginDate;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Operator other = (Operator) obj;
        if ((this.operatorId == null) ? (other.operatorId != null) : !this.operatorId.equals(other.operatorId)) {
            return false;
        }
        if ((this.password == null) ? (other.password != null) : !this.password.equals(other.password)) {
            return false;
        }
        if ((this.firstName == null) ? (other.firstName != null) : !this.firstName.equals(other.firstName)) {
            return false;
        }
        if ((this.lastName == null) ? (other.lastName != null) : !this.lastName.equals(other.lastName)) {
            return false;
        }
        if ((this.email == null) ? (other.email != null) : !this.email.equals(other.email)) {
            return false;
        }
        if ((this.phone == null) ? (other.phone != null) : !this.phone.equals(other.phone)) {
            return false;
        }
        if ((this.company == null) ? (other.company != null) : !this.company.equals(other.company)) {
            return false;
        }
        if ((this.address == null) ? (other.address != null) : !this.address.equals(other.address)) {
            return false;
        }
        if ((this.apiKey == null) ? (other.apiKey != null) : !this.apiKey.equals(other.apiKey)) {
            return false;
        }
        if ((this.ip == null) ? (other.ip != null) : !this.ip.equals(other.ip)) {
            return false;
        }
        if (this.active != other.active) {
            return false;
        }
        if ((this.creationDate == null) ? (other.creationDate != null) :
                !this.creationDate.equals(other.creationDate)) {
            return false;
        }
        if (this.accessLevel != other.accessLevel) {
            return false;
        }
        if (this.loginCount != other.loginCount) {
            return false;
        }
        if ((this.lastLoginDate == null) ? (other.lastLoginDate != null) :
                !this.lastLoginDate.equals(other.lastLoginDate)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 43 * hash + (this.operatorId != null ? this.operatorId.hashCode() : 0);
        hash = 43 * hash + (this.password != null ? this.password.hashCode() : 0);
        hash = 43 * hash + (this.firstName != null ? this.firstName.hashCode() : 0);
        hash = 43 * hash + (this.lastName != null ? this.lastName.hashCode() : 0);
        hash = 43 * hash + (this.email != null ? this.email.hashCode() : 0);
        hash = 43 * hash + (this.phone != null ? this.phone.hashCode() : 0);
        hash = 43 * hash + (this.company != null ? this.company.hashCode() : 0);
        hash = 43 * hash + (this.address != null ? this.address.hashCode() : 0);
        hash = 43 * hash + (this.apiKey != null ? this.apiKey.hashCode() : 0);
        hash = 43 * hash + (this.ip != null ? this.ip.hashCode() : 0);
        hash = 43 * hash + (this.active ? 1 : 0);
        hash = 43 * hash + (this.creationDate != null ? this.creationDate.hashCode() : 0);
        hash = 43 * hash + this.accessLevel;
        hash = 43 * hash + this.loginCount;
        hash = 43 * hash + (this.lastLoginDate != null ? this.lastLoginDate.hashCode() : 0);
        return hash;
    }


}
