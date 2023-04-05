/*
Copyright [2022] [Cardiff University]

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.dcom.core.services;


import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import com.owlike.genson.Genson;
import java.util.HashMap;
import org.dcom.core.security.DCOMBearerToken;
import javax.xml.transform.OutputKeys;
import com.owlike.genson.GensonBuilder;
import java.io.StringReader;
import java.io.StringWriter;
import org.xml.sax.InputSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
*A private class providing a friendly interface onto the OKHTTP library for the other classes in this folder.
*/
class HttpClient {


  public static String JSON = "JSON";
  public static String XML = "XML";
  private static Genson genson;
  private static Transformer xmlTransformer;
  private static DocumentBuilder xmlBuilder;
  private static OkHttpClient client;

  private static final Logger LOGGER = LoggerFactory.getLogger( HttpClient.class );


  public HttpClient() {
    if (this.client==null) {
        try {
          genson=new GensonBuilder().create();
          TransformerFactory transformerFactory = TransformerFactory.newInstance();
          xmlTransformer = transformerFactory.newTransformer();
          xmlTransformer.setOutputProperty(OutputKeys.METHOD, "xml");
          xmlTransformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
          DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
          xmlBuilder = factory.newDocumentBuilder();
          client=new OkHttpClient.Builder().readTimeout(0, TimeUnit.MILLISECONDS).build();
        }  catch (Exception e) {
           LOGGER.error("Could not create HTTP Client!"+e.toString());
        }
      }
  }

  private MediaType getMediaType(String s) {
          if (s.equals("JSON")) return MediaType.get("application/json");
          else if (s.equals("XML")) return MediaType.get("application/xml");
          else if (s.equals("FORM")) return MediaType.get("application/x-www-form-urlencoded");
          return MediaType.get("text/plain;");
  }

  private String makeRequest(String url,String type, String mediaType,String body,DCOMBearerToken bearerToken) {
          MediaType mt=getMediaType(mediaType);
          RequestBody rBody=null;
          if (body!=null) rBody=RequestBody.create(body, mt);
          Request.Builder request = new Request.Builder().url(url);
          if (type.equals("POST")) {
              request=request.post(rBody);
          } else if (type.equals("DELETE")) {
              request=request.delete();
          } else if (type.equals("PUT")) {
            if (body!=null) request=request.put(rBody);
          } else if (type.equals("PATCH")) {
            if (body!=null) request=request.patch(rBody);
          }
          request=request.addHeader("Content-Type", mt.toString());
          request=request.addHeader("Accept", mt.toString());
          if (bearerToken!=null)  request=request.addHeader("Authorization", "Bearer " + bearerToken.getToken());
          try {
            Response response = client.newCall(request.build()).execute();
            if (response.code() < 200 || response.code() > 299 ) {
              LOGGER.error("Response Code:"+response.code()+" returned for request to:"+url);
              if (response.code()==500) {
                LOGGER.error(response.body().string());
                response.close();
              }
              return null;
            }
            String responseString=response.body().string();
            response.close();
            //System.out.println(responseString);
            return responseString;
          } catch (IOException e) {
            LOGGER.error("Could not make HTTP Request!"+e.toString());
          }
          return null;

  }

  private Document makeRequestXML(String url,String type, Document body,DCOMBearerToken bearerToken) {
      try {
        DOMSource source = new DOMSource(body);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        xmlTransformer.transform(source, result);
        String dataResponse=makeRequest(url,type,"XML",writer.toString(),bearerToken);
        if (dataResponse==null) return null;
        Document document = xmlBuilder.parse(new InputSource(new StringReader(dataResponse)));
        return document;
      } catch (Exception e) {
          LOGGER.error("Could not make HTTP Request!"+e.toString());
      }
      return null;
  }

  private HashMap<String,Object> makeRequestJSON(String url,String type, HashMap<String,Object> body,DCOMBearerToken bearerToken) {
      String dataIn=genson.serialize(body);
      String dataResponse=makeRequest(url,type,"JSON",dataIn,bearerToken);
      if (dataResponse==null) return null;
      HashMap<String, Object> data = genson.deserialize(dataResponse, HashMap.class);
      if (data==null) {
        LOGGER.error(dataResponse);
      }
      return data;
  }
  
  private ArrayList<Object> makeListRequestJSON(String url,String type, HashMap<String,Object> body,DCOMBearerToken bearerToken) {
      String dataIn=genson.serialize(body);
      String dataResponse=makeRequest(url,type,"JSON",dataIn,bearerToken);
      //System.out.println(dataResponse);
      if (dataResponse==null) return null;
      ArrayList<Object> data = genson.deserialize(dataResponse, ArrayList.class);
      return data;
  }

  public String getString(String url,String mediaType) {
      return getString(url,mediaType,null);
  }

  public String getString(String url,String mediaType,DCOMBearerToken bearerToken) {
      return makeRequest(url,"GET",mediaType,null,bearerToken);
  }

  public String postString(String url,String mediaType,String body) {
      return postString(url,mediaType,body,null);
  }

  public String postString(String url,String mediaType,String body,DCOMBearerToken bearerToken) {
    return makeRequest(url,"POST",mediaType,body,bearerToken);
  }

  public String putString(String url,String mediaType,String body,DCOMBearerToken bearerToken) {
    return makeRequest(url,"PUT",mediaType,body,bearerToken);
  }

  public String putString(String url,String mediaType,String body) {
    return putString(url,mediaType,body,null);
  }
  
  public String patchString(String url,String mediaType,String body) {
    return putString(url,mediaType,body,null);
  }
  
  public String patchString(String url,String mediaType,String body,DCOMBearerToken token) {
    return putString(url,mediaType,body,token);
  }

  public String deleteString(String url,String mediaType) {
    return deleteString(url,mediaType,null);
  }

  public String deleteString(String url,String mediaType,DCOMBearerToken bearerToken) {
    return makeRequest(url,"DELETE",mediaType,null,bearerToken);
  }


  public HashMap<String,Object> getJSON(String url) {
      return getJSON(url,null);
  }

  public HashMap<String,Object> getJSON(String url,DCOMBearerToken bearerToken) {
      return makeRequestJSON(url,"GET",null,bearerToken);
  }
  
  public ArrayList<Object> getListJSON(String url) {
      return makeListRequestJSON(url,"GET",null,null);
  }
  
  public ArrayList<Object> postListJSON(String url,HashMap<String,Object> body) {
      return makeListRequestJSON(url,"POST",body,null);
  }

  public HashMap<String,Object> postJSON(String url,HashMap<String,Object> body) {
      return postJSON(url,body,null);
  }

  public HashMap<String,Object> postJSON(String url,HashMap<String,Object> body,DCOMBearerToken bearerToken) {
    return makeRequestJSON(url,"POST",body,bearerToken);
  }

  public HashMap<String,Object> putJSON(String url,HashMap<String,Object> body,DCOMBearerToken bearerToken) {
    return makeRequestJSON(url,"PUT",body,bearerToken);
  }

  public HashMap<String,Object> putJSON(String url,HashMap<String,Object> body) {
    return putJSON(url,body,null);
  }

  public HashMap<String,Object> deleteJSON(String url) {
    return deleteJSON(url,null);
  }

  public HashMap<String,Object> deleteJSON(String url,DCOMBearerToken bearerToken) {
    return makeRequestJSON(url,"DELETE",null,bearerToken);
  }

  public Document getXML(String url) {
      return getXML(url,null);
  }

  public Document getXML(String url,DCOMBearerToken bearerToken) {
      return makeRequestXML(url,"GET",null,bearerToken);
  }

  public Document postXML(String url,Document body) {
      return postXML(url,body,null);
  }

  public Document postXML(String url,Document body,DCOMBearerToken bearerToken) {
    return makeRequestXML(url,"POST",body,bearerToken);
  }

  public Document putXML(String url,Document body,DCOMBearerToken bearerToken) {
    return makeRequestXML(url,"PUT",body,bearerToken);
  }

  public Document putXML(String url,Document body) {
    return putXML(url,body,null);
  }

  public Document deleteXML(String url) {
    return deleteXML(url,null);
  }

  public Document deleteXML(String url,DCOMBearerToken bearerToken) {
    return makeRequestXML(url,"DELETE",null,bearerToken);
  }

}
