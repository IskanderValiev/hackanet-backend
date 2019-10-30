//package com.hackanet.config;
//
//import org.apache.http.HttpHost;
//import org.elasticsearch.client.Client;
//import org.elasticsearch.client.RestClient;
//import org.elasticsearch.client.RestHighLevelClient;
//import org.elasticsearch.client.transport.TransportClient;
//import org.elasticsearch.common.settings.Settings;
//import org.elasticsearch.common.transport.InetSocketTransportAddress;
//import org.elasticsearch.transport.client.PreBuiltTransportClient;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
//import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
//import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
//
//import java.net.InetAddress;
//
///**
// * @author Iskander Valiev
// * created by isko
// * on 10/28/19
// */
//@EnableElasticsearchRepositories(basePackages = "com.hackanet.repositories")
//@Configuration
//@ConfigurationProperties(prefix = "es")
//public class ElasticsearchConfig {
//    private String host;
//    private int port;
//    private String clusterName;
//
//    @Bean
//    public Client client() throws Exception {
//
//        Settings esSettings = Settings.builder()
//                .put("cluster.name", clusterName)
//                .put("client.transport.sniff", true)
//                .build();
//
//        //https://www.elastic.co/guide/en/elasticsearch/guide/current/_transport_client_versus_node_client.html
//
//        TransportClient client = new PreBuiltTransportClient(esSettings);
//        client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), port));
//        return client;
//    }
//
//    @Bean
//    public ElasticsearchOperations elasticsearchTemplate() throws Exception {
//        return new ElasticsearchTemplate(client());
//    }
//
//    //Embedded Elasticsearch Server
//    /*@Bean
//    public ElasticsearchOperations elasticsearchTemplate() {
//        return new ElasticsearchTemplate(nodeBuilder().local(true).node().client());
//    }*/
//}
