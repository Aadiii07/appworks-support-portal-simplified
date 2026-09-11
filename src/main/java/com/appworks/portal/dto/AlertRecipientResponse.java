package com.appworks.portal.dto;

import com.appworks.portal.entity.AlertRecipient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertRecipientResponse {

    private Long id;
    private String email;

    public static AlertRecipientResponse fromEntity(AlertRecipient recipient) {
        return AlertRecipientResponse.builder()
                .id(recipient.getId())
                .email(recipient.getEmail())
                .build();
    }
}
