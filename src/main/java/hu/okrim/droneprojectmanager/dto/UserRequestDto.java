package hu.okrim.droneprojectmanager.dto;

public record UserRequestDto(
    Long accountNumber,
    String password
) {
}
