package alma.acs.tmcdb;
// Generated Jun 5, 2017 7:15:51 PM by Hibernate Tools 4.3.1.Final


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * ReceiverQualityParameters generated by hbm2java
 */
@SuppressWarnings("serial")
@Entity
@Table(name="`RECEIVERQUALITYPARAMETERS`"
)
public class ReceiverQualityParameters extends alma.acs.tmcdb.translator.TmcdbObject implements java.io.Serializable {


     protected Integer receiverQualityParamId;
     protected ReceiverQuality receiverQuality;
     protected Double frequency;
     protected Double sidebandRatio;
     protected Double trx;
     protected Double polarization;
     protected Double bandPassQuality;

    public ReceiverQualityParameters() {
    }
   
    @Id @GeneratedValue(generator="generator")
    @GenericGenerator(name="generator", strategy="native",
       parameters = {@Parameter(name="sequence", value="ReceivQP_seq")}
	)

    
    @Column(name="`RECEIVERQUALITYPARAMID`", unique=true, nullable=false)
    public Integer getReceiverQualityParamId() {
        return this.receiverQualityParamId;
    }
    
    public void setReceiverQualityParamId(Integer receiverQualityParamId) {    
        if( propertyChangeSupport != null )
            propertyChangeSupport.firePropertyChange("receiverQualityParamId", this.receiverQualityParamId, this.receiverQualityParamId = receiverQualityParamId);
        else
            this.receiverQualityParamId = receiverQualityParamId;
    }


@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="`RECEIVERQUALITYID`", nullable=false)
    public ReceiverQuality getReceiverQuality() {
        return this.receiverQuality;
    }
    
    public void setReceiverQuality(ReceiverQuality receiverQuality) {    
        if( propertyChangeSupport != null )
            propertyChangeSupport.firePropertyChange("receiverQuality", this.receiverQuality, this.receiverQuality = receiverQuality);
        else
            this.receiverQuality = receiverQuality;
    }


    
    @Column(name="`FREQUENCY`", nullable=false, precision=64, scale=0)
    public Double getFrequency() {
        return this.frequency;
    }
    
    public void setFrequency(Double frequency) {    
        if( propertyChangeSupport != null )
            propertyChangeSupport.firePropertyChange("frequency", this.frequency, this.frequency = frequency);
        else
            this.frequency = frequency;
    }


    
    @Column(name="`SIDEBANDRATIO`", nullable=false, precision=64, scale=0)
    public Double getSidebandRatio() {
        return this.sidebandRatio;
    }
    
    public void setSidebandRatio(Double sidebandRatio) {    
        if( propertyChangeSupport != null )
            propertyChangeSupport.firePropertyChange("sidebandRatio", this.sidebandRatio, this.sidebandRatio = sidebandRatio);
        else
            this.sidebandRatio = sidebandRatio;
    }


    
    @Column(name="`TRX`", nullable=false, precision=64, scale=0)
    public Double getTrx() {
        return this.trx;
    }
    
    public void setTrx(Double trx) {    
        if( propertyChangeSupport != null )
            propertyChangeSupport.firePropertyChange("trx", this.trx, this.trx = trx);
        else
            this.trx = trx;
    }


    
    @Column(name="`POLARIZATION`", nullable=false, precision=64, scale=0)
    public Double getPolarization() {
        return this.polarization;
    }
    
    public void setPolarization(Double polarization) {    
        if( propertyChangeSupport != null )
            propertyChangeSupport.firePropertyChange("polarization", this.polarization, this.polarization = polarization);
        else
            this.polarization = polarization;
    }


    
    @Column(name="`BANDPASSQUALITY`", nullable=false, precision=64, scale=0)
    public Double getBandPassQuality() {
        return this.bandPassQuality;
    }
    
    public void setBandPassQuality(Double bandPassQuality) {    
        if( propertyChangeSupport != null )
            propertyChangeSupport.firePropertyChange("bandPassQuality", this.bandPassQuality, this.bandPassQuality = bandPassQuality);
        else
            this.bandPassQuality = bandPassQuality;
    }





}


