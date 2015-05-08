package ca.vorona.bertapull;

import java.util.Date;
import java.util.List;

public interface Indexer {
    
    public void indexMethods(String testName, List<String> methods, Date date) throws Exception;

}
