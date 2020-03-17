package com.tiket.tix.dummy.flight.integrator.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tiket.tix.common.entity.CommonModel;
import com.tiket.tix.dummy.flight.integrator.entity.constant.enums.DiscountLabelType;
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
public class DiscountLabel extends CommonModel implements Serializable {

  private static final long serialVersionUID = 1L;
  private String type;
  private String headerText;
  private String headerDescription;
  private String totalText;
  private String headerIcon;
  private String listIcon;
  private DiscountLabelType discountLabelType;
}
