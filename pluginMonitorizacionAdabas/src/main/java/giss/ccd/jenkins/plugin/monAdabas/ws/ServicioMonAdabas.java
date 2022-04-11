package giss.ccd.jenkins.plugin.monAdabas.ws;

import es.seg_social.ccd.monadabas.service.GissCcdNatDevOpsNtdoMonAdabasWsMonAdabasService;
import es.seg_social.ccd.monadabas.service.MonAdabasServicePortType;


import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;


public class ServicioMonAdabas {

    public static final String UNION_PROTOCOLO = "://";
    public static final String UNION_PUERTO = ":";
    public static final String SERVICE = "/ws/giss.ccd.natDevOps.ntdo.monAdabas.ws:monAdabasService";
    public static final String TICKET_PRUEBA = "TICKET_PRUEBA";

    public MonAdabasServicePortType inicializarServicioMonitAdabas (String endpoint) throws MalformedURLException {

            URL url =  URI.create(endpoint).toURL();
            GissCcdNatDevOpsNtdoMonAdabasWsMonAdabasService service = new GissCcdNatDevOpsNtdoMonAdabasWsMonAdabasService(url);
            return service.getGissCcdNatDevOpsNtdoMonAdabasWsMonAdabasServicePort();
    }
}
