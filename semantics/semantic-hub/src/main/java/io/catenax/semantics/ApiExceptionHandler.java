/*
 * Copyright (c) 2021-2022 Robert Bosch Manufacturing Solutions GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.catenax.semantics;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentConversionNotSupportedException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import io.catenax.semantics.hub.AspectModelNotFoundException;
import io.catenax.semantics.hub.EntityNotFoundException;
import io.catenax.semantics.hub.InvalidAspectModelException;
import io.catenax.semantics.hub.InvalidStateTransitionException;
import io.catenax.semantics.hub.ModelPackageNotFoundException;
import io.catenax.semantics.hub.model.Error;
import io.catenax.semantics.hub.model.ErrorResponse;



@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

   @Override
   protected ResponseEntity<Object> handleMethodArgumentNotValid( final MethodArgumentNotValidException ex,
         final HttpHeaders headers,
         final HttpStatus status, final WebRequest request ) {
      final String path = ((ServletWebRequest) request).getRequest().getRequestURI();
      final Map<String, Object> errors = ex.getBindingResult()
                                           .getFieldErrors()
                                           .stream()
                                           .collect( Collectors.toMap( FieldError::getField, e -> {
                                              if ( null == e.getDefaultMessage() ) {
                                                 return "null";
                                              }
                                              return e.getDefaultMessage();
                                           } ) );
      // TODO: the ErrorResponse classes are currently in the AAS api definition
      // we should move that out to a general api definition. Error response should be identical for all semantic layer
      // services.
      return new ResponseEntity<>( new ErrorResponse()
            .error( new Error()
                  .message( "Validation failed." )
                  .details( errors )
                  .path( path ) ), HttpStatus.BAD_REQUEST );
   }

   @ExceptionHandler( InvalidAspectModelException.class )
   public ResponseEntity<ErrorResponse> handleInvalidAspectModelException(final HttpServletRequest request,
                                                                          final InvalidAspectModelException exception ) {
      final Map<String, Object> errors = exception.getDetails()
                                                  .entrySet()
                                                  .stream().collect( Collectors.toMap(
                  Map.Entry::getKey,
                  Map.Entry::getValue
            ) );
      return new ResponseEntity<>( new ErrorResponse()
            .error( new Error()
                  .message( "Validation failed." )
                  .details( errors )
                  .path( request.getRequestURI() ) ), HttpStatus.BAD_REQUEST );
   }

   @ExceptionHandler( { AspectModelNotFoundException.class, ModelPackageNotFoundException.class,  EntityNotFoundException.class  } )
   public ResponseEntity<ErrorResponse> handleNotFoundException( final HttpServletRequest request,
         final RuntimeException exception ) {
      return new ResponseEntity<>( new ErrorResponse()
            .error( new Error()
                  .message( exception.getMessage() )
                  .path( request.getRequestURI() ) ), HttpStatus.NOT_FOUND );
   }

   @ExceptionHandler( {IllegalArgumentException.class})
   public ResponseEntity<ErrorResponse> handleIllegalArgumentException( final HttpServletRequest request,
         final IllegalArgumentException exception ) {
      return new ResponseEntity<>( new ErrorResponse()
            .error( new Error()
                  .message( exception.getMessage() )
                  .path( request.getRequestURI() ) ), HttpStatus.BAD_REQUEST );
   }

    @ExceptionHandler( {MethodArgumentConversionNotSupportedException.class})
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotSupportedException( final HttpServletRequest request ) {
        String queryString = request.getQueryString();
        return new ResponseEntity<>( new ErrorResponse()
                .error( new Error()
                        .message( String.format("The provided parameters are invalid. %s", URLDecoder.decode(queryString, StandardCharsets.UTF_8)) )
                        .path( request.getRequestURI() ) ), HttpStatus.BAD_REQUEST );
    }

    @ExceptionHandler( {InvalidStateTransitionException.class})
    public ResponseEntity<ErrorResponse> handleInvalidStateTransitionException( final HttpServletRequest request, final InvalidStateTransitionException exception ) {
        String queryString = request.getQueryString();
        return new ResponseEntity<>( new ErrorResponse()
                .error( new Error()
                        .message(exception.getMessage())
                        .path( request.getRequestURI() ) ), HttpStatus.BAD_REQUEST );
    }

}