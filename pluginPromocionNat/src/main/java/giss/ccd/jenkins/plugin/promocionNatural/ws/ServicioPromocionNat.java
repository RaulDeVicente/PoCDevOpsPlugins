package giss.ccd.jenkins.plugin.promocionNatural.ws;

import es.seg_social.ccd.promocionnatservice.GissCcdNatDevOpsNtdoPromocionWsPromocionNatService;
import es.seg_social.ccd.promocionnatservice.PromocionNatServicePortType;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.handler.Handler;
import jakarta.xml.ws.handler.HandlerResolver;
import jakarta.xml.ws.handler.PortInfo;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class ServicioPromocionNat {

    public static final String UNION_PROTOCOLO = "://";
    public static final String UNION_PUERTO = ":";
    public static final String SERVICE = "/ws/giss.ccd.natDevOps.ntdo.promocion.ws:promocionNatService";

    public PromocionNatServicePortType inicializarServicioPromocionNat (String endpoint) throws MalformedURLException, SOAPException {


            URL url =  URI.create(endpoint).toURL();
            GissCcdNatDevOpsNtdoPromocionWsPromocionNatService service = new GissCcdNatDevOpsNtdoPromocionWsPromocionNatService(url);
            return service.getGissCcdNatDevOpsNtdoPromocionWsPromocionNatServicePort();
    }
}