package com.tiket.tix.dummy.flight.integrator.entity.constant.enums;

public enum TimeFrame {
  AFTER_PURCHASE("AFTER_PURCHASE"),
  BEFORE_DEPARTURE("BEFORE_DEPARTURE"),
  AFTER_DEPARTURE("AFTER_DEPARTURE");

  private String name;

  TimeFrame(String name) {
    this.name = name;
  }
}
