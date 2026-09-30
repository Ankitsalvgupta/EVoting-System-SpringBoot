package com.codingninjas.EVotingSystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(@NotBlank String  name,
                              @NotBlank @Size(min = 8) String password) {}
