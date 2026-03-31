package com.project.cozystay.accommodation.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AccommodationEvent {
    private final Long accommodationId;
    private final OperationType operationType;

    public enum OperationType {
        SAVE, DELETE
    }
}
