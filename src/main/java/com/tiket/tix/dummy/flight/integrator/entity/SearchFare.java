package com.tiket.tix.dummy.flight.integrator.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tiket.tix.common.entity.CommonModel;
import com.tiket.tix.flight.common.model.response.fare.OtherFare;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchFare extends CommonModel implements Serializable {

  private static final long serialVersionUID = 1L;

  private String supplierId;
  private String distributionType;
  private String currency;
  private String originalCurrency;
  private double cheapestFare;
  private Fare totalFare;
  private Fare adultFare;
  private Fare childFare;
  private Fare infantFare;
  private Fare originalTotalFare;
  private Fare originalAdultFare;
  private Fare originalChildFare;
  private Fare originalInfantFare;
  private List<String> discountLabels;
  private Map<String, DiscountLabel> discountLabelProperties;
  private String fareClasses;
  private List<OtherFare> otherFares;
  private List<PenaltyFeeDescription> penaltyFeeDescriptions;
}
