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

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaggageResponse extends CommonModel implements Serializable {

  private static final long serialVersionUID = 1L;

  private BaggageDetail cabin;
  private BaggageDetail checkIn;
  private boolean additionalBaggage;
  private String urlIcon;
}
