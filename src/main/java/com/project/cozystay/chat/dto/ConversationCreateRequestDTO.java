package com.project.cozystay.chat.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ConversationCreateRequestDTO {
    @NotNull
    private Long hostId;

    private Long accommodationId;

}
