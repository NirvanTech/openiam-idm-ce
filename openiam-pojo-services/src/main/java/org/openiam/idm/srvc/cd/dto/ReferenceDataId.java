package org.openiam.idm.srvc.cd.dto;
// Generated Dec 2, 2007 5:41:37 PM by Hibernate Tools 3.2.0.b11



/**
 * ReferenceDataId generated by hbm2java
 */
public class ReferenceDataId  implements java.io.Serializable {


     private String codeGroup;
     private String statusCd;
     private String languageCd;

    public ReferenceDataId() {
    }

    public ReferenceDataId(String codeGroup, String statusCd, String languageCd) {
       this.codeGroup = codeGroup;
       this.statusCd = statusCd;
       this.languageCd = languageCd;
    }
   
    public String getCodeGroup() {
        return this.codeGroup;
    }
    
    public void setCodeGroup(String codeGroup) {
        this.codeGroup = codeGroup;
    }
    public String getStatusCd() {
        return this.statusCd;
    }
    
    public void setStatusCd(String statusCd) {
        this.statusCd = statusCd;
    }
    public String getLanguageCd() {
        return this.languageCd;
    }
    
    public void setLanguageCd(String languageCd) {
        this.languageCd = languageCd;
    }


   public boolean equals(Object other) {
         if ( (this == other ) ) return true;
		 if ( (other == null ) ) return false;
		 if ( !(other instanceof ReferenceDataId) ) return false;
		 ReferenceDataId castOther = ( ReferenceDataId ) other; 
         
		 return ( (this.getCodeGroup()==castOther.getCodeGroup()) || ( this.getCodeGroup()!=null && castOther.getCodeGroup()!=null && this.getCodeGroup().equals(castOther.getCodeGroup()) ) )
 && ( (this.getStatusCd()==castOther.getStatusCd()) || ( this.getStatusCd()!=null && castOther.getStatusCd()!=null && this.getStatusCd().equals(castOther.getStatusCd()) ) )
 && ( (this.getLanguageCd()==castOther.getLanguageCd()) || ( this.getLanguageCd()!=null && castOther.getLanguageCd()!=null && this.getLanguageCd().equals(castOther.getLanguageCd()) ) );
   }
   
   public int hashCode() {
         int result = 17;
         
         result = 37 * result + ( getCodeGroup() == null ? 0 : this.getCodeGroup().hashCode() );
         result = 37 * result + ( getStatusCd() == null ? 0 : this.getStatusCd().hashCode() );
         result = 37 * result + ( getLanguageCd() == null ? 0 : this.getLanguageCd().hashCode() );
         return result;
   }   


}

