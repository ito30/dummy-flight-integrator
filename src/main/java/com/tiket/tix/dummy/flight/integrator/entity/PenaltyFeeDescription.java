package com.tiket.tix.dummy.flight.integrator.entity;

import com.tiket.tix.common.entity.CommonModel;
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
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PenaltyFeeDescription extends CommonModel implements Serializable {
  private static final long serialVersionUID = 1L;

  private String penaltyInfoTitle;
  private String penaltyInfoIcon;
  private String penaltyInfoTitleWithInsurance;
  private String penaltyInfoIconWithInsurance;
  private PenaltyType penaltyType;
  private PenaltyStatus penaltyStatus;
}
