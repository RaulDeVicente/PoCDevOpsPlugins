package giss.ccd.jenkins.plugin.promocionNatural.ws;

import jakarta.xml.ws.handler.MessageContext;
import jakarta.xml.ws.handler.soap.SOAPHandler;
import jakarta.xml.ws.handler.soap.SOAPMessageContext;

import javax.xml.namespace.QName;
import java.util.Set;

public class SoapFaultHandler implements SOAPHandler<SOAPMessageContext> {

    @Override
    public boolean handleMessage(SOAPMessageContext context) {
        System.out.println(context.getMessage());
        return true;
    }

    @Override
    public boolean handleFault(SOAPMessageContext context) {

        return true;
    }

    @Override
    public void close(MessageContext messageContext) {
        System.out.println("adios");
    }


    @Override
    public Set<QName> getHeaders() {
        return null;
    }
}
