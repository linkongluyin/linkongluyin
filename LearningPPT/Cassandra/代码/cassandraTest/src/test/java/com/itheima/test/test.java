package com.itheima.test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.geotools.data.*;
import org.geotools.data.simple.*;
import org.geotools.factory.*;
import org.geotools.feature.*;
import org.locationtech.geomesa.utils.interop.SimpleFeatureTypes;
import org.opengis.feature.simple.*;
import org.opengis.filter.Filter;

public class GeomesaCassandraDemo {

    public static void main(String[] args) throws Exception {
        // Geomesa Cassandra连接参数
        Map<String, String> params = new HashMap<>();
        params.put("cassandra.keyspace", "geomesa");
        params.put("cassandra.contact.point", "localhost");
        params.put("cassandra.port", "9042");
        params.put("cassandra.username", "cassandra");
        params.put("cassandra.password", "cassandra");
        params.put("cassandra.catalog", "geomesa_test");

        // 获取数据存储
        DataStore ds = DataStoreFinder.getDataStore(params);
        if (ds == null) {
            throw new RuntimeException("无法获取数据存储");
        }

        // 获取SimpleFeatureType
        SimpleFeatureType sft = ds.getSchema("myfeaturetype");
        if (sft == null) {
            throw new RuntimeException("无法获取SimpleFeatureType");
        }

        // 创建SimpleFeature
        SimpleFeatureBuilder builder = new SimpleFeatureBuilder(sft);
        builder.set("name", "name1");
        builder.set("dtg", "2022-04-23T12:34:56Z");
        builder.set("geom", "POINT(40.0 30.0)");
        SimpleFeature feature = builder.buildFeature("1");

        // 写入数据
        SimpleFeatureStore store = (SimpleFeatureStore) ds.getFeatureSource("myfeaturetype");
        store.setTransaction(Transaction.AUTO_COMMIT);
        store.addFeatures(Collections.singleton(feature));

        // 关闭数据存储
        ds.dispose();
    }
}