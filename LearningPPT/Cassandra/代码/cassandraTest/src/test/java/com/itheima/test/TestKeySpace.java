package com.itheima.test;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.KeyspaceMetadata;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.schemabuilder.DropKeyspace;
import com.datastax.driver.core.schemabuilder.KeyspaceOptions;
import com.datastax.driver.core.schemabuilder.SchemaBuilder;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 测试操作键空间
 */
public class TestKeySpace {

    Session session =null;
    /**
     * 连接cassandra的服务端
     */
    @Before
    public void init(){
//        服务器的地址
        String host = "192.168.5.142";

        int port = 9042;

//        连接服务端，获取会话
        Cluster cluster = Cluster.builder()
                .addContactPoint(host)
                .withPort(port)
                .build();

        session = cluster.connect();
    }

    /**
     * 查询所有键空间
     */
    @Test
    public void findKeySpace(){
        List<KeyspaceMetadata> keyspaces = session.getCluster().getMetadata().getKeyspaces();
        for (KeyspaceMetadata keyspace : keyspaces) {
            System.out.println(keyspace.getName());
        }
    }

    /**
     * 创建键空间
     */
    @Test
    public void createKeySpace() {
//      1、使用cql来创建
//        session.execute("CREATE KEYSPACE school WITH replication = {'class':'SimpleStrategy', 'replication_factor' : 3}");
        Map<String, Object> replicaton = new HashMap<>();
        replicaton.put("class","SimpleStrategy");
        replicaton.put("replication_factor",2);
        KeyspaceOptions options = SchemaBuilder.createKeyspace("geomesa")
                .ifNotExists()
                .with()
                .replication(replicaton);
        session.execute(options);
    }

    /**
     * 删除 键空间
     */
    @Test
    public void deleteKeySpace(){
        DropKeyspace dropKeyspace = SchemaBuilder.dropKeyspace("geomesa").ifExists();
        session.execute(dropKeyspace);
    }

    /**
     * 修改键空间
     */
    @Test
    public void alterKeySpace(){
        Map<String, Object> replicaton = new HashMap<>();
        replicaton.put("class","SimpleStrategy");
        replicaton.put("replication_factor",1);
        KeyspaceOptions options = SchemaBuilder.
                alterKeyspace("geomesa").
                with().
                replication(replicaton);
        session.execute(options);
    }
}
