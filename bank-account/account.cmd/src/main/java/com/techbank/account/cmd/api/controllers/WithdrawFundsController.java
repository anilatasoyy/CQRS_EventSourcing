package com.techbank.account.cmd.api.controllers;


import com.techbank.account.cmd.api.commands.WithdrawFundsCommand;
import com.techbank.account.common.dto.BaseResponse;
import com.techbank.cqrs.core.exceptions.AggregateNotFoundException;
import com.techbank.cqrs.core.infrastructure.CommandDispatcher;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/v1/withdrawFunds")
public class WithdrawFundsController {

    private final Logger logger = Logger.getLogger(WithdrawFundsController.class.getName());


    private final CommandDispatcher commandDispatcher;

    public WithdrawFundsController(CommandDispatcher commandDispatcher) {
        this.commandDispatcher = commandDispatcher;
    }

    @PutMapping(path = "/{id}")
    ResponseEntity<BaseResponse> withdrawFunds(@PathVariable(value = "id") String id, @RequestBody WithdrawFundsCommand command){

        try {
            command.setId(id);
            commandDispatcher.send(command);
            return new ResponseEntity<>(new BaseResponse("Withdraw funds request completed successfully!"), HttpStatus.OK);

        } catch (Exception e) {
            if (e instanceof IllegalStateException || e instanceof AggregateNotFoundException) {
                logger.log(Level.WARNING, MessageFormat.format("Client made a bad request - {0}.", e.toString()));
                return new ResponseEntity<>(new BaseResponse(e.toString()), HttpStatus.BAD_REQUEST);

            } else {
                var safeErrorMessage = MessageFormat.format("Error while processing request to withdraw funds to bank account with id - {0}.", id);
                logger.log(Level.SEVERE, safeErrorMessage, e);
                return new ResponseEntity<>(new BaseResponse(safeErrorMessage), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

    }
}
