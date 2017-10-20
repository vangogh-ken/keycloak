/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.fast;

import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.OperationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class SysoutEventListenerProvider implements EventListenerProvider {
	private static Logger LOG = LoggerFactory.getLogger(SysoutEventListenerProvider.class);

    private Set<EventType> excludedEvents;
    private Set<OperationType> excludedAdminOperations;
    private MqQueueConnectonConsumer consumer;
    
    static{
    	LOG.info("SysoutEventListenerProvider static");
    }

    public SysoutEventListenerProvider(Set<EventType> excludedEvents, Set<OperationType> excludedAdminOpearations) {
        this.excludedEvents = excludedEvents;
        this.excludedAdminOperations = excludedAdminOpearations;
        //consumer = new MqQueueConnectonConsumer("", "", "tcp://127.0.0.1:61616", "logback");
    }

    @Override
    public void onEvent(Event event) {
    	
    	LOG.info("event:  {}", toString(event));
    	//consumer.message(new EventSerialized(event));
    	// Ignore excluded events
        if (excludedEvents != null && excludedEvents.contains(event.getType())) {
            return;
        } else {
            System.out.println("EVENT: " + toString(event));
        }
    }

    @Override
    public void onEvent(AdminEvent event, boolean includeRepresentation) {
    	LOG.info("event:  {}", toString(event));
    	//consumer.message(new EventSerialized(event));
    	// Ignore excluded operations
        if (excludedAdminOperations != null && excludedAdminOperations.contains(event.getOperationType())) {
            return;
        } else {
            System.out.println("EVENT: " + toString(event));
        }
    }

    private String toString(Event event) {
        StringBuilder sb = new StringBuilder();

        sb.append("type=");
        sb.append(event.getType());
        sb.append(", realmId=");
        sb.append(event.getRealmId());
        sb.append(", clientId=");
        sb.append(event.getClientId());
        sb.append(", userId=");
        sb.append(event.getUserId());
        sb.append(", ipAddress=");
        sb.append(event.getIpAddress());

        if (event.getError() != null) {
            sb.append(", error=");
            sb.append(event.getError());
        }

        if (event.getDetails() != null) {
            for (Map.Entry<String, String> e : event.getDetails().entrySet()) {
                sb.append(", ");
                sb.append(e.getKey());
                if (e.getValue() == null || e.getValue().indexOf(' ') == -1) {
                    sb.append("=");
                    sb.append(e.getValue());
                } else {
                    sb.append("='");
                    sb.append(e.getValue());
                    sb.append("'");
                }
            }
        }

        return sb.toString();
    }
    
    private String toString(AdminEvent adminEvent) {
        StringBuilder sb = new StringBuilder();

        sb.append("operationType=");
        sb.append(adminEvent.getOperationType());
        sb.append(", realmId=");
        sb.append(adminEvent.getAuthDetails().getRealmId());
        sb.append(", clientId=");
        sb.append(adminEvent.getAuthDetails().getClientId());
        sb.append(", userId=");
        sb.append(adminEvent.getAuthDetails().getUserId());
        sb.append(", ipAddress=");
        sb.append(adminEvent.getAuthDetails().getIpAddress());
        sb.append(", resourcePath=");
        sb.append(adminEvent.getResourcePath());

        if (adminEvent.getError() != null) {
            sb.append(", error=");
            sb.append(adminEvent.getError());
        }
        
        return sb.toString();
    }
    
    @Override
    public void close() {
    	//consumer.close();
    }
    
    public static void main(String[] args) {
    	new MqQueueConnectonConsumer("", "", "tcp://127.0.0.1:61616", "logback").message("TEST1").retrieve().message("TEST2").close();
	}

    public static class EventSerialized extends Event implements Serializable{
    	/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public EventSerialized(Event event){
    		this.time = event.getTime();
    		this.type = event.getType();
    		this.realmId = event.getRealmId();
    		this.clientId = event.getClientId();
    		this.userId = event.getUserId();
    		this.sessionId = event.getSessionId();
    		this.ipAddress = event.getIpAddress();
    		this.error = event.getError();
    		this.details = event.getDetails();
    	}
		
		public EventSerialized(AdminEvent adminEvent){
			this.adminEvent = adminEvent;
		}
    	
		AdminEvent adminEvent;
		
    	private long time;
        private EventType type;
        private String realmId;
        private String clientId;
        private String userId;
        private String sessionId;
        private String ipAddress;
        private String error;
        private Map<String, String> details;
        public long getTime() {
            return time;
        }
        public void setTime(long time) {
            this.time = time;
        }
        public EventType getType() {
            return type;
        }
        public void setType(EventType type) {
            this.type = type;
        }
        public String getRealmId() {
            return realmId;
        }
        public void setRealmId(String realmId) {
            this.realmId = maxLength(realmId, 255);
        }
        public String getClientId() {
            return clientId;
        }
        public void setClientId(String clientId) {
            this.clientId = maxLength(clientId, 255);
        }
        public String getUserId() {
            return userId;
        }
        public void setUserId(String userId) {
            this.userId = maxLength(userId, 255);
        }
        public String getSessionId() {
            return sessionId;
        }
        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }
        public String getIpAddress() {
            return ipAddress;
        }
        public void setIpAddress(String ipAddress) {
            this.ipAddress = ipAddress;
        }
        public String getError() {
            return error;
        }
        public void setError(String error) {
            this.error = error;
        }
        public Map<String, String> getDetails() {
            return details;
        }
        public void setDetails(Map<String, String> details) {
            this.details = details;
        }
        static String maxLength(String string, int length){
            if (string != null && string.length() > length) {
                return string.substring(0, length - 1);
            }
            return string;
        }
    }
}
