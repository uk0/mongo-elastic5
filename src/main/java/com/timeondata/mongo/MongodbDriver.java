package com.timeondata.mongo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.timeondata.inter.watcherInterface;
import com.timeondata.utils.File2Json;
import org.bson.BsonTimestamp;
import org.bson.Document;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

/**
 * Mongo oplog
 * @author ZhangJianxin
 */
public class MongodbDriver  implements  Runnable{

    private MongoClient mongoClient = null;
    public static ConcurrentHashMap<String, Cursor> cursorList;
    public static MongodbDriver mongodbDriver = null;
    public Semaphore binary = new Semaphore(2);
    private MongodbDriver() {
        JSONObject config = File2Json.getConfig("driver").getJSONObject("Mongodb");
        MongoClientOptions.Builder mcob = MongoClientOptions.builder();
        mcob.connectionsPerHost(config.getInteger("connectionsPerHost"));
        mcob.socketKeepAlive(config.getBoolean("socketKeepAlive"));
        mcob.readPreference(ReadPreference.primary());
        MongoClientOptions mco = mcob.build();
        String[] clusterIp = config.getString("ServerIP").split(",");
        int port = config.getInteger("ServerPort");
        List<ServerAddress> seeds = new ArrayList<ServerAddress>();
        for (String ip : clusterIp) {
            seeds.add(new ServerAddress(ip, port));
        }
        List<MongoCredential> credentialList = new ArrayList<MongoCredential>();
        if (config.containsKey("credentials")) {
            JSONArray credentials = config.getJSONArray("credentials");
            if (credentials != null && credentials.size() > 0) {
                for (int i = 0; i < credentials.size(); i++) {
                    try {
                        JSONObject c = credentials.getJSONObject(i);
                        credentialList.add(MongoCredential.createCredential(
                                c.getString("username"),
                                c.getString("database"),
                                c.getString("password").toCharArray()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                mongoClient = new MongoClient(seeds, credentialList, mco);
            }
        } else {
            mongoClient = new MongoClient(seeds, mco);
        }

        cursorList = new ConcurrentHashMap();
    }

    public synchronized static MongodbDriver getMongodbDriver() {
        return mongodbDriver == null ? new MongodbDriver() : mongodbDriver;
    }
    /**
     *
     * @param watcherInterface
     *
     * */

    public void watchOplog(watcherInterface watcherInterface) throws InterruptedException {


        MongoDatabase db = mongoClient.getDatabase("local");
        MongoCollection coll = db.getCollection("oplog.rs");
        if (coll.count() == 0) {
            coll = db.getCollection("oplog.$main");
        }

        FindIterable<Document> tsFind = coll.find().sort(new BasicDBObject("$natural", -1)).limit(1);
        BsonTimestamp ts = (BsonTimestamp) tsFind.first().get("ts");
        while (true) {
            Document mcJson = new Document();
            Document queryJson = new Document();
            mcJson.put("$gt", ts);
            queryJson.append("ts", mcJson);
            while (true) {
                FindIterable<Document> dstFind = coll.find(queryJson);
                for (Document doc : dstFind) {
                    ts = (BsonTimestamp)  doc.get("ts");
                    JSONObject toJSON = JSON.parseObject(doc.toJson());
                    JSONObject data =  toJSON.getJSONObject("o");
                    String tableName = toJSON.getString("ns");
                    switch (doc.getString("op")) {
                        case "i":
                            watcherInterface.insert(data,getTableInfo(tableName));
                            break;
                        case "u":
                            watcherInterface.updata(data,getTableInfo(tableName));
                            break;
                        case "d":
                            watcherInterface.delete(data,getTableInfo(tableName));
                            break;
                        default:
                            System.out.println("default:   "+ doc);
                    }

                }
                Thread.sleep(1000L);
                break;
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        MongodbDriver mongodbDriver = new MongodbDriver();
        Thread thread = new Thread(mongodbDriver);
        thread.start();
    }

    public static  JSONObject getTableInfo(String infos){
        JSONObject info = new JSONObject();
        String[] tableInfo = infos.split("\\.");
        String dbName = tableInfo[0];
        String tableNmae = tableInfo[1];
        info.put("database",dbName);
        info.put("table",tableNmae);
        return info;
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        MongodbDriver d = new MongodbDriver();
        try {
            binary.acquire();
            d.watchOplog(new watcherInterface() {
                @Override
                public int insert(JSONObject inJson,JSONObject dbTable) {
                    System.out.println(inJson);
                    System.out.println(dbTable);
                    return 0;
                }

                @Override
                public int delete(JSONObject inJson,JSONObject dbTable) {
                    System.out.println(inJson);
                    System.out.println(dbTable);
                    return 0;
                }

                @Override
                public int updata(JSONObject inJson,JSONObject dbTable) {
                    System.out.println(inJson);
                    System.out.println(dbTable);
                    return 0;
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            binary.release(); //out
        }
    }
}
