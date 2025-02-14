package alma.acs.tmcdb;
// Generated Jun 5, 2017 7:15:51 PM by Hibernate Tools 4.3.1.Final


import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * BL_PadId generated by hbm2java
 */
@SuppressWarnings("serial")
@Embeddable
public class BL_PadId  implements java.io.Serializable {


     private Integer version;
     private Long modTime;
     private Character operation;
     private Integer baseElementId;

    public BL_PadId() {
    }
   


    @Column(name="`VERSION`", nullable=false)
    public Integer getVersion() {
        return this.version;
    }
    
    public void setVersion(Integer version) {    
    	this.version = version;
    }



    @Column(name="`MODTIME`", nullable=false)
    public Long getModTime() {
        return this.modTime;
    }
    
    public void setModTime(Long modTime) {    
    	this.modTime = modTime;
    }



    @Column(name="`OPERATION`", nullable=false, length=1)
    public Character getOperation() {
        return this.operation;
    }
    
    public void setOperation(Character operation) {    
    	this.operation = operation;
    }



    @Column(name="`BASEELEMENTID`", nullable=false)
    public Integer getBaseElementId() {
        return this.baseElementId;
    }
    
    public void setBaseElementId(Integer baseElementId) {    
    	this.baseElementId = baseElementId;
    }



   public boolean equals(Object other) {
         if ( (this == other ) ) return true;
		 if ( (other == null ) ) return false;
		 if ( !(other instanceof BL_PadId) ) return false;
		 BL_PadId castOther = ( BL_PadId ) other;

		 return ( (this.getVersion()==castOther.getVersion()) || ( this.getVersion()!=null && castOther.getVersion()!=null && this.getVersion().equals(castOther.getVersion()) ) )
 && ( (this.getModTime()==castOther.getModTime()) || ( this.getModTime()!=null && castOther.getModTime()!=null && this.getModTime().equals(castOther.getModTime()) ) )
 && ( (this.getOperation()==castOther.getOperation()) || ( this.getOperation()!=null && castOther.getOperation()!=null && this.getOperation().equals(castOther.getOperation()) ) )
 && ( (this.getBaseElementId()==castOther.getBaseElementId()) || ( this.getBaseElementId()!=null && castOther.getBaseElementId()!=null && this.getBaseElementId().equals(castOther.getBaseElementId()) ) );
   }

   public int hashCode() {
         int result = 17;

         result = 37 * result + ( getVersion() == null ? 0 : this.getVersion().hashCode() );
         result = 37 * result + ( getModTime() == null ? 0 : this.getModTime().hashCode() );
         result = 37 * result + ( getOperation() == null ? 0 : this.getOperation().hashCode() );
         result = 37 * result + ( getBaseElementId() == null ? 0 : this.getBaseElementId().hashCode() );
         return result;
   }


}


