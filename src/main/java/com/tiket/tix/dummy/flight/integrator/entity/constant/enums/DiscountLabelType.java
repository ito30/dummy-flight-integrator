package com.tiket.tix.dummy.flight.integrator.entity.constant.enums;

/**
 * @author ockyaditiasaputra
 */
public enum DiscountLabelType {
  SPECIAL_DISCOUNT("SPECIAL_DISCOUNT", "SPECIAL_DISCOUNT"),
  REGULAR_DISCOUNT("REGULAR_DISCOUNT", "REGULAR_DISCOUNT");

  private final String name;
  private final String value;

  DiscountLabelType(String name, String value) {
    this.name = name;
    this.value = value;
  }

  public String getName() {
    return name;
  }

  public String getValue() {
    return value;
  }
}
