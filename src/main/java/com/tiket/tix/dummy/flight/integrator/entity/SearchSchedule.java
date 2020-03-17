package com.tiket.tix.dummy.flight.integrator.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@ToString
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchSchedule extends ScheduleDetailResponse {
}
