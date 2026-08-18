package com.meridian.policy.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "insured_risk_location_schedule", schema = "pas",
       uniqueConstraints = @UniqueConstraint(columnNames = {"policy_id","location_sequence_number"}))
public class InsuredRiskLocationSchedule {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "risk_location_id") private Long riskLocationId;
    @Column(name = "policy_id", nullable = false) private Long policyId;
    @Column(name = "location_sequence_number", nullable = false) private Integer locationSequenceNumber;
    @Column(name = "address_line_1", length = 200) private String addressLine1;
    @Column(name = "city", length = 100) private String city;
    @Column(name = "state_code", length = 2) private String stateCode;
    @Column(name = "postal_code", length = 10) private String postalCode;
    @Column(name = "county_fips", length = 5) private String countyFips;
    @Column(name = "latitude", precision = 10, scale = 7) private BigDecimal latitude;
    @Column(name = "longitude", precision = 10, scale = 7) private BigDecimal longitude;
    @Column(name = "construction_class_code", length = 20) private String constructionClassCode;
    @Column(name = "occupancy_class_code", length = 20) private String occupancyClassCode;
    @Column(name = "sprinklered_flag") private Boolean sprinkleredFlag;
    @Column(name = "building_value_amount", precision = 14, scale = 2) private BigDecimal buildingValueAmount;
    @Column(name = "contents_value_amount", precision = 14, scale = 2) private BigDecimal contentsValueAmount;
    @Column(name = "business_income_value_amount", precision = 14, scale = 2) private BigDecimal businessIncomeValueAmount;
    @Column(name = "annual_receipts_amount", precision = 14, scale = 2) private BigDecimal annualReceiptsAmount;
    @Column(name = "catastrophe_zone_code", length = 20) private String catastropheZoneCode;

    public Long getRiskLocationId() { return riskLocationId; }
    public void setRiskLocationId(Long v) { riskLocationId = v; }
    public Long getPolicyId() { return policyId; }
    public void setPolicyId(Long v) { policyId = v; }
    public Integer getLocationSequenceNumber() { return locationSequenceNumber; }
    public void setLocationSequenceNumber(Integer v) { locationSequenceNumber = v; }
    public String getAddressLine1() { return addressLine1; }
    public void setAddressLine1(String v) { addressLine1 = v; }
    public String getCity() { return city; }
    public void setCity(String v) { city = v; }
    public String getStateCode() { return stateCode; }
    public void setStateCode(String v) { stateCode = v; }
    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String v) { postalCode = v; }
    public String getCountyFips() { return countyFips; }
    public void setCountyFips(String v) { countyFips = v; }
    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal v) { latitude = v; }
    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal v) { longitude = v; }
    public String getConstructionClassCode() { return constructionClassCode; }
    public void setConstructionClassCode(String v) { constructionClassCode = v; }
    public String getOccupancyClassCode() { return occupancyClassCode; }
    public void setOccupancyClassCode(String v) { occupancyClassCode = v; }
    public Boolean getSprinkleredFlag() { return sprinkleredFlag; }
    public void setSprinkleredFlag(Boolean v) { sprinkleredFlag = v; }
    public BigDecimal getBuildingValueAmount() { return buildingValueAmount; }
    public void setBuildingValueAmount(BigDecimal v) { buildingValueAmount = v; }
    public BigDecimal getContentsValueAmount() { return contentsValueAmount; }
    public void setContentsValueAmount(BigDecimal v) { contentsValueAmount = v; }
    public BigDecimal getBusinessIncomeValueAmount() { return businessIncomeValueAmount; }
    public void setBusinessIncomeValueAmount(BigDecimal v) { businessIncomeValueAmount = v; }
    public BigDecimal getAnnualReceiptsAmount() { return annualReceiptsAmount; }
    public void setAnnualReceiptsAmount(BigDecimal v) { annualReceiptsAmount = v; }
    public String getCatastropheZoneCode() { return catastropheZoneCode; }
    public void setCatastropheZoneCode(String v) { catastropheZoneCode = v; }
}
