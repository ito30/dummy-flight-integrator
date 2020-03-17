package com.tiket.tix.dummy.flight.integrator.entity;

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
public class TotalTime extends CommonModel implements Serializable {

  private static final long serialVersionUID = 1L;

  private int day;
  private int hour;
  private int minute;
}
