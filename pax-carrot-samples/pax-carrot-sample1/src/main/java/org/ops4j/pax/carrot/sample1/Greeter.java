/*
 * Copyright 2013 Harald Wellmann
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ops4j.pax.carrot.sample1;


/**
 * @author Harald Wellmann
 *
 */
public class Greeter {

    private String receiver;
    private String salutation;
    
    private String message;

    
    public String run() {
        message = String.format("%s, %s!", salutation, receiver);
        return message;
    }
    
    /**
     * @return the receiver
     */
    public String getReceiver() {
        return receiver;
    }

    
    /**
     * @param receiver the receiver to set
     */
    public void receiver(String _receiver) {
        this.receiver = _receiver;
    }

    
    /**
     * @return the salutation
     */
    public String getSalutation() {
        return salutation;
    }

    
    /**
     * @param salutation the salutation to set
     */
    public void setSalutation(String salutation) {
        this.salutation = salutation;
    }

    
    /**
     * @return the message
     */
    public String getMessage() {
        if (message == null) {
            run();
        }
        return message;
    }
}
