package es.udc.psi14.lab08trabazo;

import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class XmlTask extends AsyncTask<String, Void, List<String>> {

    @Override
    protected List<String> doInBackground(String... params) {
        AndroidHttpClient client = AndroidHttpClient.newInstance("");
        HttpGet request = new HttpGet(params[0]);
        XMLResponseHandler respHandler = new XMLResponseHandler();

        try {
            return client.execute(request, respHandler);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}

class XMLResponseHandler implements ResponseHandler<List<String>> {
    @Override
    public List<String> handleResponse(HttpResponse response) throws IOException {
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();
            XMLReader xr = sp.getXMLReader();
            XMLContentHandler handler = new XMLContentHandler();
            xr.setContentHandler(handler);
            xr.parse(new InputSource(response.getEntity().getContent()));
            return handler.getData();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

        return null;
    }
}

class XMLContentHandler extends DefaultHandler {
    String idlinea = null, idautobus = null, idparada = null;
    boolean parsingIdlinea = false, parsingIdautobus = false, parsingIdparada = false;
    List<String> results = new ArrayList<>();

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {

        if (localName.equals("idlinea")) {
            parsingIdlinea = true;
        } else if (localName.equals("idautobus")) {
            parsingIdautobus = true;
        } else if (localName.equals("idparada")) {
            parsingIdparada = true;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (parsingIdlinea) {
            idlinea = new String(ch, start, length).trim();
        } else if (parsingIdautobus) {
            idautobus = new String(ch, start, length).trim();
        } else if (parsingIdparada) {
            idparada = new String(ch, start, length).trim();
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if (localName.equals("idlinea")) {
            parsingIdlinea = false;
        } else if (localName.equals("idautobus")) {
            parsingIdautobus = false;
        } else if (localName.equals("idparada")) {
            parsingIdparada = false;
        } else if (localName.equals("llegada")) {
            results.add("idlinea:" + idlinea + " idautobus: " + idautobus + " idparada:" + idparada);
            idlinea = null; idautobus = null; idparada = null;
        }
    }

    public List<String> getData() {
        return results;
    }
}