package com.techbank.account.cmd.api.controllers;


import com.techbank.account.cmd.api.commands.RestoreReadDbCommand;

import com.techbank.account.common.dto.BaseResponse;
import com.techbank.cqrs.core.infrastructure.CommandDispatcher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping(path = "/api/v1/restoreReadDb")
public class RestoreDbController {
    private final Logger logger = Logger.getLogger(RestoreReadDbCommand.class.getName());

    private final CommandDispatcher commandDispatcher;

    public RestoreDbController(CommandDispatcher commandDispatcher) {
        this.commandDispatcher = commandDispatcher;
    }

    @PostMapping
    public ResponseEntity<BaseResponse> restoreReadDb() {

        try {
            commandDispatcher.send(new RestoreReadDbCommand());
            return new ResponseEntity<>(new BaseResponse("Read database restore request completed successfully!"), HttpStatus.OK);
        }  catch (Exception e) {

            if (e instanceof IllegalStateException) {
                logger.log(Level.WARNING, MessageFormat.format("Client made a bad request - {0}.", e.toString()));
                return new ResponseEntity<>(new BaseResponse(e.toString()), HttpStatus.BAD_REQUEST);
            }
            var safeErrorMessage = "Error while processing request to restore read database";
            logger.log(Level.SEVERE, safeErrorMessage, e);
            return new ResponseEntity<>(new BaseResponse(safeErrorMessage), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
