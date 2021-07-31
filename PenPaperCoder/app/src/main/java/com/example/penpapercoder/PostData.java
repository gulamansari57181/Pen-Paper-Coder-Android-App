package com.example.penpapercoder;

public class PostData {


    private String clientId;
    private String clientSecret;
    private String script;
    private String language;
    private String versionIndex;
    private  String stdin;



    public PostData(String script ,String stdin) {
        this.script = script;
        this.clientId = APIClient.API_ID;
        this.clientSecret = APIClient.API_SECRET;
        this.language = APIClient.LANGUAGE;
        this.versionIndex = APIClient.VERSION_INDEX;
        this.stdin = stdin;

    }
}

