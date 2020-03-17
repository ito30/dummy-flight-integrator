package com.tiket.tix.dummy.flight.integrator.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tiket.tix.common.entity.CommonModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Fare extends CommonModel implements Serializable {

  private static final long serialVersionUID = 1L;

  private double total;
  private double totalWithoutAdjustment;
  private double totalWithMarkupWithoutDiscount;
  private double totalWithInsurance;
  private double totalWithoutAdjustmentWithInsurance;
  private double totalWithMarkupWithoutDiscountWithInsurance;
  private double base;
  private double commission;
  private double iwjr;
  private double tax;
  private double markup;
  private double discount;
  private double supplierDiscount;
  private double loyaltyPoint;
  private int seatAvailables;
  private double insurance;
  private String premiNumber;
  private List<PenaltyInfo> penaltyInfos;
  private List<String> fareBasisCodes;
}
