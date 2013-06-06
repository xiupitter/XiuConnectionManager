package org.jivesoftware.multiplexer.spi;

import org.dom4j.Element;
import org.jivesoftware.multiplexer.ConnectionManager;
import org.jivesoftware.util.Log;

public class ServerFailoverSwitchDeliverer extends ServerFailoverDeliverer {
	
    public void deliver(Element stanza) {
    	String streamID=null;
        if ("route".equals(stanza.getName())) {
            streamID = stanza.attributeValue("streamid");
            if(stanza.elements().size()>0){
            	ConnectionManager.getInstance().getServerSurrogate().send(((Element)stanza.elements().get(0)).asXML(), streamID);
            	return;
            }
        }else if("iq".equals(stanza.getName())){
        	Element e = (Element)stanza.elements().get(0);
        	if(e!=null&&e.getName().equals("session")){
        		streamID = e.attributeValue("id");
        		Element e2 =(Element) e.elements().get(0);
        		if(e2.getName().equals("create")){
        			while(e2.elementIterator().hasNext()){
        				Element hostElem = (Element)e2.elementIterator().next();
        				if(hostElem.getName().equals("host")){
                			ConnectionManager.getInstance().getServerSurrogate().clientSessionCreated(streamID, hostElem.attributeValue("address"),hostElem.attributeValue("name"));
                        	return;
        				}
        			}
        		}else if (e2.getName().equals("close")){
        			ConnectionManager.getInstance().getServerSurrogate().clientSessionClosed(streamID);
                	return;
        		}
        	}
        }
        super.deliver(stanza);
    }
}
