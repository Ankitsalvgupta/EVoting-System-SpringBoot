package com.codingninjas.EVotingSystem.exception;

public record ErrorResponse(int status, String error, String message, String timestamp){
}
