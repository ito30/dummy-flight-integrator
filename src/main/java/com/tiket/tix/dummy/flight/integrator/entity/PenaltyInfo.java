package com.tiket.tix.dummy.flight.integrator.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tiket.tix.common.entity.CommonModel;
import com.tiket.tix.dummy.flight.integrator.entity.constant.enums.TimeFrame;
import com.tiket.tix.flight.common.model.constant.enums.PenaltyStatus;
import com.tiket.tix.flight.common.model.constant.enums.PenaltyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PenaltyInfo extends CommonModel implements Serializable {
  private static final long serialVersionUID = 1L;

  private PenaltyType penaltyType;
  private PenaltyStatus penaltyStatus;
  private int minutes;
  private TimeFrame timeFrame;
  private Double amount;
  private Double percent;
  private String currencyCode;
  private Double decimalPlaces;
  private Double originalAmount;
  private String originalCurrencyCode;
  private Double originalDecimalPlaces;
  private String applicability;
  private String penaltyDescription;
  private Double maxAmount;
}
