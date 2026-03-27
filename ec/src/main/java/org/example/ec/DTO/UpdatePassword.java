package org.example.ec.DTO;

import lombok.Data;

@Data
public class UpdatePassword {
    private String oldPassword;
    private String newPassword;
}
