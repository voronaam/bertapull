package ca.vorona.bertapull;

import java.util.Date;
import java.util.List;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import static org.elasticsearch.common.xcontent.XContentFactory.*;

public class ElasticSearchIndexer implements Indexer {
    private final Client client;
    
    public ElasticSearchIndexer() {
        client = new TransportClient()
            .addTransportAddress(new InetSocketTransportAddress("localhost", 9300)); // TODO: Configure
    }

    @Override
    public void indexMethods(String testName, List<String> methods, Date date) throws Exception {
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        
        for(String method: methods) {
            String[] parts = method.split("#");
            // TODO: Configure index names
            bulkRequest.add(client.prepareIndex("bertatest", "methods").setSource(jsonBuilder().startObject()
                    .field("testName", testName)
                    .field("testDate", date)
                    .field("class", parts[0])
                    .field("method", parts[1])
                    .endObject()
                    ));
        }
        
        bulkRequest.execute().actionGet();
        // We can use response.getId(); - generated id
    }
    
    public void disconnect() {
        client.close();
    }

}
