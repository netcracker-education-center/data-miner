package org.netcracker.learningcenter.confluence;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConfluenceClientFactory  {
    @Autowired
    private ObjectMapper mapper;
    public ConfluenceClient createConfluenceClient(String url,String token){
        return new ConfluenceClient(url,token,mapper);
    }
}
