package com.tiket.tix.dummy.flight.integrator.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tiket.tix.common.entity.CommonModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Airline extends CommonModel implements Serializable {

  private static final long serialVersionUID = 1L;

  private String code;
  private String name;
  private String shortName;
  private String displayName;

  @JsonProperty("urlIcon")
  private String icon;

  private String url;
}
